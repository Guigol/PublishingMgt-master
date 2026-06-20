import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { APP_INITIALIZER } from '@angular/core';

import { firstValueFrom, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { Chart, registerables } from 'chart.js'; // ✅ IMPORTANT

import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { jwtInterceptor } from './app/core/interceptors/jwt.interceptor';
import { AuthService } from './app/core/services/auth.service';

// REGISTER CHART.JS
Chart.register(...registerables);

// auth init
export function initAuthFactory(authService: AuthService) {
  return (): Promise<any> => {
    return firstValueFrom(
      authService.me().pipe(
        catchError(() => {
          return of(null);
        })
      )
    );
  };
}

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([jwtInterceptor])),

    {
      provide: APP_INITIALIZER,
      useFactory: initAuthFactory,
      deps: [AuthService],
      multi: true
    }
  ]
});