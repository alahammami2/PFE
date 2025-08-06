import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';

/**
 * Guard pour vérifier l'authentification
 */
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    
    // TODO: Implémenter la vérification d'authentification réelle
    // Pour l'instant, on simule un utilisateur authentifié
    const isAuthenticated = this.checkAuthentication();
    
    if (!isAuthenticated) {
      // Redirection vers la page de connexion
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: state.url }
      });
      return false;
    }
    
    return true;
  }

  /**
   * Vérifie si l'utilisateur est authentifié
   */
  private checkAuthentication(): boolean {
    // TODO: Vérifier le token JWT dans le localStorage/sessionStorage
    // ou appeler un service d'authentification
    
    // Simulation pour le développement
    const token = localStorage.getItem('access_token');
    return !!token;
  }
}
