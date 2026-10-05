import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }

  const expectedRoles = route.data?.['roles'] as Array<string>;
  if (expectedRoles && expectedRoles.length > 0) {
    const userRole = authService.getRole();
    if (!expectedRoles.includes(userRole) && userRole !== 'ADMIN') {
      // Admin has universal access, other roles get redirected to their permitted dashboard
      const targetRoute = authService.getDashboardRouteForRole(userRole);
      router.navigate([targetRoute]);
      return false;
    }
  }

  return true;
};
