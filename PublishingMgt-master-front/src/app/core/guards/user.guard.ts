import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const userGuard: CanActivateFn = () => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.getCurrentUser();

  if (user && user.role === 'USER') {
    return true;
  }

   router.navigate(['/user-dashboard']);
   return false;

};