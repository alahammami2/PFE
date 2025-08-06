import { Injectable } from '@angular/core';
import { CanActivate, CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

/**
 * Guard d'authentification pour protéger les routes
 * Vérifie si l'utilisateur est connecté et a les permissions nécessaires
 */
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate, CanActivateChild {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Vérification de l'accès à une route
   * @param route Route activée
   * @param state État du routeur
   * @returns Observable<boolean>
   */
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    return this.checkAccess(route, state.url);
  }

  /**
   * Vérification de l'accès aux routes enfants
   * @param childRoute Route enfant
   * @param state État du routeur
   * @returns Observable<boolean>
   */
  canActivateChild(
    childRoute: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    return this.checkAccess(childRoute, state.url);
  }

  /**
   * Vérification de l'accès avec gestion des rôles
   * @param route Route à vérifier
   * @param url URL demandée
   * @returns Observable<boolean>
   */
  private checkAccess(route: ActivatedRouteSnapshot, url: string): Observable<boolean> {
    // Vérification de base de l'authentification
    if (!this.authService.isAuthenticated()) {
      console.log('Utilisateur non authentifié, redirection vers login');
      this.redirectToLogin(url);
      return of(false);
    }

    // Validation du token côté serveur
    return this.authService.validateToken().pipe(
      map(isValid => {
        if (!isValid) {
          console.log('Token invalide, redirection vers login');
          this.redirectToLogin(url);
          return false;
        }

        // Vérification des rôles requis
        const requiredRoles = route.data?.['roles'] as string[];
        if (requiredRoles && requiredRoles.length > 0) {
          const hasRequiredRole = this.authService.hasAnyRole(requiredRoles);
          
          if (!hasRequiredRole) {
            console.log('Rôle insuffisant pour accéder à cette route');
            this.router.navigate(['/unauthorized']);
            return false;
          }
        }

        // Vérification des permissions spécifiques
        const requiredPermissions = route.data?.['permissions'] as string[];
        if (requiredPermissions && requiredPermissions.length > 0) {
          const hasPermissions = this.checkPermissions(requiredPermissions);
          
          if (!hasPermissions) {
            console.log('Permissions insuffisantes pour accéder à cette route');
            this.router.navigate(['/unauthorized']);
            return false;
          }
        }

        return true;
      }),
      catchError(error => {
        console.error('Erreur lors de la validation du token:', error);
        this.redirectToLogin(url);
        return of(false);
      })
    );
  }

  /**
   * Vérification des permissions spécifiques
   * @param permissions Liste des permissions requises
   * @returns boolean
   */
  private checkPermissions(permissions: string[]): boolean {
    const currentUser = this.authService.getCurrentUser();
    
    if (!currentUser) {
      return false;
    }

    // Logique de vérification des permissions selon le rôle
    switch (currentUser.role) {
      case 'ADMINISTRATEUR':
        // Les administrateurs ont toutes les permissions
        return true;
        
      case 'COACH':
        // Les coaches ont accès aux permissions liées aux joueurs et entraînements
        const coachPermissions = [
          'view_players',
          'manage_training',
          'view_statistics',
          'manage_team'
        ];
        return permissions.every(permission => coachPermissions.includes(permission));
        
      case 'JOUEUR':
        // Les joueurs ont accès limité
        const playerPermissions = [
          'view_profile',
          'update_profile',
          'view_training',
          'view_team'
        ];
        return permissions.every(permission => playerPermissions.includes(permission));
        
      case 'STAFF_MEDICAL':
        // Le staff médical a accès aux informations médicales
        const medicalPermissions = [
          'view_players',
          'manage_medical',
          'view_injuries',
          'manage_health'
        ];
        return permissions.every(permission => medicalPermissions.includes(permission));
        
      case 'RESPONSABLE_FINANCIER':
        // Le responsable financier a accès aux données financières
        const financialPermissions = [
          'view_finances',
          'manage_budget',
          'view_salaries',
          'generate_reports'
        ];
        return permissions.every(permission => financialPermissions.includes(permission));
        
      default:
        return false;
    }
  }

  /**
   * Redirection vers la page de connexion
   * @param returnUrl URL de retour après connexion
   */
  private redirectToLogin(returnUrl: string): void {
    this.router.navigate(['/login'], { 
      queryParams: { returnUrl } 
    });
  }
}

/**
 * Guard spécifique pour les administrateurs
 */
@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): Observable<boolean> {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return of(false);
    }

    const hasAdminRole = this.authService.hasRole('ADMINISTRATEUR');
    
    if (!hasAdminRole) {
      this.router.navigate(['/unauthorized']);
      return of(false);
    }

    return of(true);
  }
}

/**
 * Guard spécifique pour les coaches
 */
@Injectable({
  providedIn: 'root'
})
export class CoachGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): Observable<boolean> {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return of(false);
    }

    const hasCoachRole = this.authService.hasAnyRole(['COACH', 'ADMINISTRATEUR']);
    
    if (!hasCoachRole) {
      this.router.navigate(['/unauthorized']);
      return of(false);
    }

    return of(true);
  }
}

/**
 * Guard spécifique pour le staff médical
 */
@Injectable({
  providedIn: 'root'
})
export class MedicalGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): Observable<boolean> {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return of(false);
    }

    const hasMedicalRole = this.authService.hasAnyRole(['STAFF_MEDICAL', 'ADMINISTRATEUR']);
    
    if (!hasMedicalRole) {
      this.router.navigate(['/unauthorized']);
      return of(false);
    }

    return of(true);
  }
}
