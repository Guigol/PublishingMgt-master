/// <reference types="jasmine" />

import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { authorGuard } from './author.guard';
import { AuthService } from '../services/auth.service';

describe('authorGuard', () => {
  let router: jasmine.SpyObj<Router>;
  let authService: any;

  const routeMock = {} as any;
  const stateMock = {} as any;

  beforeEach(() => {
    router = jasmine.createSpyObj('Router', ['navigate']);

    authService = {
      getCurrentUser: jasmine.createSpy('getCurrentUser')
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: router },
        { provide: AuthService, useValue: authService }
      ]
    });
  });

  it('should allow AUTHOR', () => {
    authService.getCurrentUser.and.returnValue({ role: 'AUTHOR' });

    const result = TestBed.runInInjectionContext(() =>
      authorGuard(routeMock, stateMock)
    );

    expect(result).toBeTrue();
  });

  it('should deny non AUTHOR', () => {
    authService.getCurrentUser.and.returnValue({ role: 'USER' });

    const result = TestBed.runInInjectionContext(() =>
      authorGuard(routeMock, stateMock)
    );

    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });
});