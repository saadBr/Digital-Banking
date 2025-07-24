import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, throwError } from 'rxjs';
export const AppHttpInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const token = authService.accessToken || localStorage.getItem('access_token');

  if (!request.url.includes('/auth/login') && token) {
    const newRequest = request.clone({
      headers: request.headers.set('Authorization', 'Bearer ' + token),
    });
    return next(newRequest).pipe(
      catchError((err) => {
        if (err.status == 401) {
          authService.logout();
        }
        return throwError(err.message);
      }),
    );
  }

  return next(request);
};
