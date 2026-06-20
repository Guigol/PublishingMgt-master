import { TestBed, ComponentFixture } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {

  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {

    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['login']);
    routerSpy = jasmine.createSpyObj<Router>('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // -------------------------
  // TEST 1 : AUTHOR redirect
  // -------------------------
  it('should navigate to AUTHOR dashboard', () => {

    authServiceSpy.login.and.returnValue(
      of({
        id: 1,
        login: 'test',
        created_at: '2024-01-01',
        role: 'AUTHOR'
      } as any)
    );

    component.login = 'a@test.com';
    component.password = '123';

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      login: 'a@test.com',
      password: '123'
    });

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['/author-overview']
    );
  });

  // -------------------------
  // TEST 2 : MANAGER redirect
  // -------------------------
  it('should navigate to MANAGER dashboard', () => {

    authServiceSpy.login.and.returnValue(
      of({
        id: 1,
        login: 'test',
        created_at: '2024-01-01',
        role: 'MANAGER'
      } as any)
    );

    component.login = 'm@test.com';
    component.password = '123';

    component.onSubmit();

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['/mgr-overview']
    );
  });

  // -------------------------
  // TEST 3 : USER redirect
  // -------------------------
  it('should navigate to USER dashboard', () => {

    authServiceSpy.login.and.returnValue(
      of({
        id: 1,
        login: 'test',
        created_at: '2024-01-01',
        role: 'USER'
      } as any)
    );

    component.login = 'u@test.com';
    component.password = '123';

    component.onSubmit();

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['/user-overview']
    );
  });

  // -------------------------
  // TEST 4 : ADMIN redirect
  // -------------------------
  it('should navigate to ADMIN dashboard', () => {

    authServiceSpy.login.and.returnValue(
      of({
        id: 1,
        login: 'test',
        created_at: '2024-01-01',
        role: 'ADMIN'
      } as any)
    );

    component.login = 'admin@test.com';
    component.password = '123';

    component.onSubmit();

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['/admin-overview']
    );
  });

  // -------------------------
  // TEST 5 : DEFAULT route
  // -------------------------
  it('should navigate to home for unknown role', () => {

    authServiceSpy.login.and.returnValue(
      of({
        id: 1,
        login: 'test',
        created_at: '2024-01-01',
        role: 'UNKNOWN'
      } as any)
    );

    component.login = 'x@test.com';
    component.password = '123';

    component.onSubmit();

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['/']
    );
  });

  // -------------------------
  // TEST 6 : ERROR handling
  // -------------------------
  it('should set error message on login failure', () => {

    authServiceSpy.login.and.returnValue(
      throwError(() => new Error('fail'))
    );

    component.onSubmit();

    expect(component.error).toBe('Login incorrect');
  });

});