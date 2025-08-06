import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

/**
 * Interceptor pour gérer les erreurs HTTP
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(
    private router: Router,
    private toastr: ToastrService
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        this.handleError(error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Gère les erreurs HTTP
   */
  private handleError(error: HttpErrorResponse): void {
    let errorMessage = 'Une erreur inattendue s\'est produite';
    
    switch (error.status) {
      case 0:
        // Erreur réseau
        errorMessage = 'Erreur de connexion au serveur';
        this.toastr.error(errorMessage, 'Erreur réseau');
        break;
        
      case 400:
        // Bad Request
        errorMessage = error.error?.message || 'Requête invalide';
        this.toastr.error(errorMessage, 'Erreur de validation');
        break;
        
      case 401:
        // Non autorisé
        errorMessage = 'Session expirée, veuillez vous reconnecter';
        this.toastr.error(errorMessage, 'Authentification requise');
        this.handleUnauthorized();
        break;
        
      case 403:
        // Accès interdit
        errorMessage = 'Vous n\'avez pas les droits pour effectuer cette action';
        this.toastr.error(errorMessage, 'Accès refusé');
        break;
        
      case 404:
        // Non trouvé
        errorMessage = 'Ressource non trouvée';
        this.toastr.error(errorMessage, 'Ressource introuvable');
        break;
        
      case 409:
        // Conflit
        errorMessage = error.error?.message || 'Conflit de données';
        this.toastr.error(errorMessage, 'Conflit');
        break;
        
      case 422:
        // Entité non traitable
        errorMessage = error.error?.message || 'Données invalides';
        this.toastr.error(errorMessage, 'Validation échouée');
        break;
        
      case 500:
        // Erreur serveur
        errorMessage = 'Erreur interne du serveur';
        this.toastr.error(errorMessage, 'Erreur serveur');
        break;
        
      case 502:
        // Bad Gateway
        errorMessage = 'Service temporairement indisponible';
        this.toastr.error(errorMessage, 'Service indisponible');
        break;
        
      case 503:
        // Service indisponible
        errorMessage = 'Service en maintenance';
        this.toastr.error(errorMessage, 'Maintenance en cours');
        break;
        
      default:
        // Autres erreurs
        errorMessage = error.error?.message || `Erreur ${error.status}`;
        this.toastr.error(errorMessage, 'Erreur');
        break;
    }
    
    console.error('Erreur HTTP:', error);
  }

  /**
   * Gère les erreurs d'authentification
   */
  private handleUnauthorized(): void {
    // Suppression des tokens
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user_roles');
    
    // Redirection vers la page de connexion
    this.router.navigate(['/login']);
  }
}
