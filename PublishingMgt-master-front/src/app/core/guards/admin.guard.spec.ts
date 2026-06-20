/// <reference types="jasmine" />

import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, GuardResult } from '@angular/router';
import { BehaviorSubject, firstValueFrom, Observable } from 'rxjs';

import { adminGuard } from './admin.guard';
import { AuthService } from '../services/auth.service';

describe('adminGuard', () => {

  let authService: { currentUser$: BehaviorSubject<any> };
  let router: { navigate: jasmine.Spy };

  const route = {} as ActivatedRouteSnapshot;
  const state = {} as RouterStateSnapshot;

  beforeEach(() => {

    authService = {
      currentUser$: new BehaviorSubject(null)
    };

    router = {
      navigate: jasmine.createSpy('navigate')
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    });

  });

  // =========================
  // TEST 1 - ADMIN OK
  // =========================
  it('should allow ADMIN', async () => {

    authService.currentUser$.next({ role: 'ADMIN' });

    const result$ = TestBed.runInInjectionContext(() =>
      adminGuard(route, state)
    );

    const result = await firstValueFrom(result$ as Observable<GuardResult>);

    expect(result).toBe(true);
  });

  // =========================
  // TEST 2 - NON ADMIN REDIRECTION
  // =========================
  it('should redirect non-ADMIN to login/dashboard', async () => {

    authService.currentUser$.next({ role: 'USER' });

    const result$ = TestBed.runInInjectionContext(() =>
      adminGuard(route, state)
    );

    const result = await firstValueFrom(result$ as Observable<GuardResult>);

    expect(result).not.toBe(true);
    expect(router.navigate).toHaveBeenCalled();
  });

});
