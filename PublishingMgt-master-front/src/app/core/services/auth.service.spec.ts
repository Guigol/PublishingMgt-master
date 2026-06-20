import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { AuthService } from './auth.service';

describe('AuthService', () => {

  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

// TEST 1
it('should login and update current user', () => {

  const mockUser = {
    id: 1,
    login: 'author@test.com',
    role: 'AUTHOR'
  };

  service.login({
    login: 'author@test.com',
    password: 'pwd'
  }).subscribe(user => {

    expect(user.login).toBe('author@test.com');

  });

  const req = httpMock.expectOne('/api/auth/login');

  expect(req.request.method).toBe('POST');
  expect(req.request.withCredentials).toBe(true);

  req.flush(mockUser);

  expect(service.getCurrentUser()?.login)
    .toBe('author@test.com');

});

// TEST 2
it('should logout and clear current user', () => {

  service['currentUserSubject'].next({
    id: 1,
    login: 'author@test.com',
    role: 'AUTHOR'
  } as any);

  service.logout().subscribe();

  const req = httpMock.expectOne('/api/auth/logout');

  expect(req.request.method).toBe('POST');

  req.flush({});

  expect(service.getCurrentUser())
    .toBeNull();

});

// TEST 3
it('should load current user from me endpoint', () => {

  const mockUser = {
    id: 1,
    login: 'author@test.com',
    role: 'AUTHOR'
  };

  service.me().subscribe(user => {

    expect(user?.login)
      .toBe('author@test.com');

  });

  const req = httpMock.expectOne('/api/auth/me');

  expect(req.request.method).toBe('GET');

  req.flush(mockUser);

  expect(service.getCurrentUser()?.login)
    .toBe('author@test.com');

    });

    // TEST 4
    it('should return null when me fails', () => {

  service.me().subscribe(user => {

    expect(user).toBeNull();

  });

  const req = httpMock.expectOne('/api/auth/me');

  req.flush(
    {},
    {
      status: 401,
      statusText: 'Unauthorized'
    }
  );

  expect(service.getCurrentUser())
    .toBeNull();

    });

    // TEST 5
it('should refresh session and reload current user', () => {

  const mockUser = {
    id: 1,
    login: 'author@test.com',
    role: 'AUTHOR'
  };

  service.refresh().subscribe(user => {

    expect(user?.login)
      .toBe('author@test.com');

  });

  const refreshReq =
    httpMock.expectOne('/api/auth/refresh');

  expect(refreshReq.request.method)
    .toBe('POST');

  refreshReq.flush({});

  const meReq =
    httpMock.expectOne('/api/auth/me');

  meReq.flush(mockUser);

    });

    // TEST 6
it('should return current user', () => {

  service['currentUserSubject'].next({
    id: 1,
    login: 'author@test.com',
    role: 'AUTHOR'
  } as any);

  expect(service.getCurrentUser()?.login)
    .toBe('author@test.com');

    });

    // TEST 7
    it('should load authors', () => {

  const authors = [
    {
      id: 1,
      firstname: 'Victor',
      surname: 'Hugo'
    }
  ];

  service.getAuthors().subscribe(data => {

    expect(data.length).toBe(1);

  });

  const req =
    httpMock.expectOne('/api/auth/authors');

  expect(req.request.method)
    .toBe('GET');

  req.flush(authors);

    });



});