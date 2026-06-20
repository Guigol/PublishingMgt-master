import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const clonedRequest = req.clone({
    withCredentials: true
  });

  return next(clonedRequest).pipe(

    catchError((error: HttpErrorResponse) => {

      if (
        error.status === 401 &&
        !req.url.includes('/auth/login') &&
        !req.url.includes('/auth/refresh') &&
        !req.url.includes('/auth/me')
      ) {

        return authService.refresh().pipe(

          switchMap(() => {
           
            const retryRequest = req.clone({
              withCredentials: true
            });

            return next(retryRequest);
          }),

          catchError(() => {
            authService.logout().subscribe();
            router.navigate(['/login']);
            return throwError(() => error);
          })

        );

      }

      return throwError(() => error);

    })

  );

};