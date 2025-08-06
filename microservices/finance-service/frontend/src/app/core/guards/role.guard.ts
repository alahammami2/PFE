import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';

/**
 * Guard pour vérifier les rôles utilisateur
 */
@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    
    const requiredRoles = route.data['roles'] as string[];
    
    if (!requiredRoles || requiredRoles.length === 0) {
      return true;
    }
    
    const userRoles = this.getUserRoles();
    const hasRequiredRole = requiredRoles.some(role => userRoles.includes(role));
    
    if (!hasRequiredRole) {
      // Redirection vers une page d'accès refusé ou dashboard
      this.router.navigate(['/dashboard']);
      return false;
    }
    
    return true;
  }

  /**
   * Récupère les rôles de l'utilisateur
   */
  private getUserRoles(): string[] {
    // TODO: Récupérer les rôles depuis le token JWT ou un service
    
    // Simulation pour le développement
    const userRoles = localStorage.getItem('user_roles');
    if (userRoles) {
      try {
        return JSON.parse(userRoles);
      } catch (error) {
        console.error('Erreur lors du parsing des rôles utilisateur:', error);
        return [];
      }
    }
    
    // Rôles par défaut pour le développement
    return ['ADMIN', 'FINANCE_MANAGER'];
  }
}
