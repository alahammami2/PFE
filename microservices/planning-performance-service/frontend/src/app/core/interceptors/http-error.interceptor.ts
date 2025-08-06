import { Injectable, inject } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { Router } from '@angular/router';
import { ApiConfig } from '../config/api.config';
import { NotificationService } from '../services/notification.service';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

  private notificationService = inject(NotificationService);
  private router = inject(Router);

  constructor() {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      retry(1), // Retry une fois en cas d'erreur réseau
      catchError((error: HttpErrorResponse) => {
        let errorMessage = '';

        if (error.error instanceof ErrorEvent) {
          // Erreur côté client
          errorMessage = `Erreur: ${error.error.message}`;
          console.error('Erreur côté client:', error.error.message);
        } else {
          // Erreur côté serveur
          errorMessage = this.getServerErrorMessage(error);
          console.error(`Erreur côté serveur: ${error.status} - ${error.message}`);
        }

        // Gestion spécifique selon le code d'erreur
        this.handleSpecificErrors(error);

        // Affichage du message d'erreur
        this.showErrorMessage(error, errorMessage);

        return throwError(() => error);
      })
    );
  }

  private getServerErrorMessage(error: HttpErrorResponse): string {
    switch (error.status) {
      case 400:
        return error.error?.message || ApiConfig.ERROR_MESSAGES.VALIDATION_ERROR;
      case 401:
        return ApiConfig.ERROR_MESSAGES.UNAUTHORIZED;
      case 403:
        return ApiConfig.ERROR_MESSAGES.FORBIDDEN;
      case 404:
        return ApiConfig.ERROR_MESSAGES.NOT_FOUND;
      case 408:
        return ApiConfig.ERROR_MESSAGES.TIMEOUT_ERROR;
      case 500:
        return ApiConfig.ERROR_MESSAGES.SERVER_ERROR;
      case 0:
        return ApiConfig.ERROR_MESSAGES.NETWORK_ERROR;
      default:
        return error.error?.message || `Erreur ${error.status}: ${error.message}`;
    }
  }

  private handleSpecificErrors(error: HttpErrorResponse): void {
    switch (error.status) {
      case 401:
        // Redirection vers la page de connexion
        this.router.navigate(['/login']);
        break;
      case 403:
        // Redirection vers une page d'accès refusé
        this.router.navigate(['/access-denied']);
        break;
      case 404:
        // Optionnel: redirection vers une page 404
        break;
    }
  }

  private showErrorMessage(error: HttpErrorResponse, message: string): void {
    // Ne pas afficher de toast pour certaines erreurs
    const silentErrors = [401, 403];

    if (!silentErrors.includes(error.status)) {
      this.notificationService.error(message);
    }
  }
}
