import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Interceptor pour ajouter le token d'authentification aux requêtes
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor() {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    
    // Récupération du token d'authentification
    const authToken = this.getAuthToken();
    
    // Si pas de token, on laisse passer la requête telle quelle
    if (!authToken) {
      return next.handle(req);
    }
    
    // Clonage de la requête avec ajout du header Authorization
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${authToken}`
      }
    });
    
    return next.handle(authReq);
  }

  /**
   * Récupère le token d'authentification
   */
  private getAuthToken(): string | null {
    // TODO: Récupérer le token depuis le localStorage, sessionStorage ou un service
    
    // Pour le développement, on simule un token
    return localStorage.getItem('access_token');
  }
}
