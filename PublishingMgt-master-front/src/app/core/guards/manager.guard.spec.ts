/// <reference types="jasmine" />

import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { managerGuard } from './manager.guard';
import { AuthService } from '../services/auth.service';

describe('managerGuard', () => {
  let router: jasmine.SpyObj<Router>;
  let authService: any;

  const route = {} as ActivatedRouteSnapshot;
  const state = {} as RouterStateSnapshot;

  beforeEach(() => {
    router = jasmine.createSpyObj('Router', ['navigate']);

    authService = {
      getCurrentUser: jasmine.createSpy('getCurrentUser'),
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: router },
        { provide: AuthService, useValue: authService },
      ],
    });
  });

  it('should allow MANAGER', () => {
    authService.getCurrentUser.and.returnValue({ role: 'MANAGER' });

    const result = TestBed.runInInjectionContext(() =>
      managerGuard(route, state)
    );

    expect(result).toBe(true);
  });

  it('should deny non MANAGER', () => {
    authService.getCurrentUser.and.returnValue({ role: 'USER' });

    const result = TestBed.runInInjectionContext(() =>
      managerGuard(route, state)
    );

    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });
});