import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authorGuard: CanActivateFn = () => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.getCurrentUser();

  if (user && user.role === 'AUTHOR') {
    return true;
  }

  router.navigate(['/dashboard']);
  return false;

};