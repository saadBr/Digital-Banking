import { CanActivateFn } from '@angular/router';

export const authorizationGuard: CanActivateFn = (route, state) => {
  return true;
};
