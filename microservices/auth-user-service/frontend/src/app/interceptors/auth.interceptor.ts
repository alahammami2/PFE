import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, take, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

/**
 * Intercepteur HTTP pour l'authentification
 * Ajoute automatiquement le token JWT aux requêtes et gère le rafraîchissement automatique
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Interception des requêtes HTTP
   * @param request Requête HTTP
   * @param next Handler suivant
   * @returns Observable<HttpEvent<any>>
   */
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Ajout du token d'authentification si disponible
    const authRequest = this.addAuthHeader(request);

    return next.handle(authRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        // Gestion des erreurs d'authentification
        if (error.status === 401) {
          return this.handle401Error(authRequest, next);
        }
        
        // Gestion des autres erreurs
        return this.handleOtherErrors(error);
      })
    );
  }

  /**
   * Ajout du header d'authentification à la requête
   * @param request Requête HTTP originale
   * @returns HttpRequest<any> Requête modifiée
   */
  private addAuthHeader(request: HttpRequest<any>): HttpRequest<any> {
    const token = this.authService.getAccessToken();
    
    // Ne pas ajouter le token pour les endpoints publics
    if (this.isPublicEndpoint(request.url) || !token) {
      return request;
    }

    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  /**
   * Gestion des erreurs 401 (Non autorisé)
   * @param request Requête originale
   * @param next Handler suivant
   * @returns Observable<HttpEvent<any>>
   */
  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Si on est déjà en train de rafraîchir le token
    if (this.isRefreshing) {
      return this.refreshTokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap(token => {
          return next.handle(this.addAuthHeader(request));
        })
      );
    }

    // Début du processus de rafraîchissement
    this.isRefreshing = true;
    this.refreshTokenSubject.next(null);

    return this.authService.refreshToken().pipe(
      switchMap((authResponse: any) => {
        this.isRefreshing = false;
        this.refreshTokenSubject.next(authResponse.accessToken);
        
        // Retry de la requête originale avec le nouveau token
        return next.handle(this.addAuthHeader(request));
      }),
      catchError((refreshError) => {
        this.isRefreshing = false;
        this.refreshTokenSubject.next(null);
        
        // Impossible de rafraîchir le token, déconnexion
        console.error('Impossible de rafraîchir le token:', refreshError);
        this.authService.logout().subscribe();
        
        return throwError(() => refreshError);
      })
    );
  }

  /**
   * Gestion des autres erreurs HTTP
   * @param error Erreur HTTP
   * @returns Observable<never>
   */
  private handleOtherErrors(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Une erreur est survenue';

    switch (error.status) {
      case 0:
        errorMessage = 'Impossible de contacter le serveur. Vérifiez votre connexion internet.';
        break;
      case 403:
        errorMessage = 'Accès refusé. Vous n\'avez pas les permissions nécessaires.';
        break;
      case 404:
        errorMessage = 'Ressource non trouvée.';
        break;
      case 500:
        errorMessage = 'Erreur interne du serveur. Veuillez réessayer plus tard.';
        break;
      case 503:
        errorMessage = 'Service temporairement indisponible. Veuillez réessayer plus tard.';
        break;
      default:
        if (error.error?.message) {
          errorMessage = error.error.message;
        } else if (error.message) {
          errorMessage = error.message;
        }
    }

    console.error('Erreur HTTP:', {
      status: error.status,
      message: errorMessage,
      url: error.url,
      error: error.error
    });

    return throwError(() => new Error(errorMessage));
  }

  /**
   * Vérification si l'endpoint est public (ne nécessite pas d'authentification)
   * @param url URL de la requête
   * @returns boolean
   */
  private isPublicEndpoint(url: string): boolean {
    const publicEndpoints = [
      '/api/auth/login',
      '/api/auth/refresh',
      '/api/auth/health',
      '/actuator',
      '/swagger-ui',
      '/v3/api-docs'
    ];

    return publicEndpoints.some(endpoint => url.includes(endpoint));
  }
}

/**
 * Intercepteur pour la gestion des erreurs globales
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        // Gestion spécifique des erreurs selon le contexte
        if (error.status === 403) {
          // Redirection vers la page d'accès refusé
          this.router.navigate(['/unauthorized']);
        }

        // Log détaillé pour le debugging
        this.logError(error, request);

        return throwError(() => error);
      })
    );
  }

  /**
   * Logging détaillé des erreurs
   * @param error Erreur HTTP
   * @param request Requête originale
   */
  private logError(error: HttpErrorResponse, request: HttpRequest<any>): void {
    const errorLog = {
      timestamp: new Date().toISOString(),
      method: request.method,
      url: request.url,
      status: error.status,
      statusText: error.statusText,
      message: error.message,
      error: error.error
    };

    console.group('🚨 Erreur HTTP Détaillée');
    console.error('Détails de l\'erreur:', errorLog);
    console.groupEnd();

    // En production, on pourrait envoyer ces logs à un service de monitoring
    if (error.status >= 500) {
      // Erreur serveur - pourrait être envoyée à un service de monitoring
      console.warn('Erreur serveur détectée - à signaler au monitoring');
    }
  }
}

/**
 * Intercepteur pour ajouter des headers communs
 */
@Injectable()
export class CommonHeadersInterceptor implements HttpInterceptor {

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Ajout d'headers communs à toutes les requêtes
    const modifiedRequest = request.clone({
      setHeaders: {
        'X-Requested-With': 'XMLHttpRequest',
        'X-Client-Version': '1.0.0',
        'X-Client-Type': 'Angular-Frontend'
      }
    });

    return next.handle(modifiedRequest);
  }
}
