import { routes } from './../app.routes';
import { inject } from '@angular/core';
import { AuthService } from './auth-service';
import { CanActivate, CanActivateFn, Router } from "@angular/router";

export const authGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  if(authService.isAutenticado()){
    return true;
  }

  authService.logout(false);

  return router.createUrlTree(['/login']);
}
