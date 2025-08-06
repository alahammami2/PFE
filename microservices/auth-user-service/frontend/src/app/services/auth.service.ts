import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

export interface LoginRequest {
  email: string;
  motDePasse: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
  expiresIn: number;
}

export interface User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
  telephone?: string;
  avatarUrl?: string;
  actif: boolean;
  derniereConnexion?: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

/**
 * Service d'authentification pour le microservice auth-user-service
 * Gère la connexion, déconnexion, et la gestion des tokens JWT
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = `${environment.authServiceUrl}/api/auth`;
  private readonly TOKEN_KEY = 'sprintbot_access_token';
  private readonly REFRESH_TOKEN_KEY = 'sprintbot_refresh_token';
  private readonly USER_KEY = 'sprintbot_user';

  // BehaviorSubject pour suivre l'état de connexion
  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Vérification périodique de la validité du token
    this.startTokenValidationTimer();
  }

  /**
   * Connexion de l'utilisateur
   * @param email Email de l'utilisateur
   * @param motDePasse Mot de passe
   * @returns Observable<AuthResponse>
   */
  login(email: string, motDePasse: string): Observable<AuthResponse> {
    const loginRequest: LoginRequest = { email, motDePasse };

    return this.http.post<AuthResponse>(`${this.API_URL}/login`, loginRequest)
      .pipe(
        tap(response => {
          this.handleAuthSuccess(response);
        }),
        catchError(error => {
          console.error('Erreur de connexion:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Déconnexion de l'utilisateur
   */
  logout(): Observable<any> {
    const user = this.getCurrentUser();
    const logoutRequest = user ? { email: user.email } : {};

    return this.http.post(`${this.API_URL}/logout`, logoutRequest)
      .pipe(
        tap(() => {
          this.handleLogout();
        }),
        catchError(error => {
          // Même en cas d'erreur, on déconnecte localement
          this.handleLogout();
          return throwError(() => error);
        })
      );
  }

  /**
   * Rafraîchissement du token d'accès
   * @returns Observable<AuthResponse>
   */
  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();
    
    if (!refreshToken) {
      this.handleLogout();
      return throwError(() => new Error('Aucun refresh token disponible'));
    }

    const refreshRequest: RefreshTokenRequest = { refreshToken };

    return this.http.post<AuthResponse>(`${this.API_URL}/refresh`, refreshRequest)
      .pipe(
        tap(response => {
          this.handleAuthSuccess(response);
        }),
        catchError(error => {
          console.error('Erreur de rafraîchissement du token:', error);
          this.handleLogout();
          return throwError(() => error);
        })
      );
  }

  /**
   * Validation du token actuel
   * @returns Observable<boolean>
   */
  validateToken(): Observable<boolean> {
    const token = this.getAccessToken();
    
    if (!token) {
      return new Observable(observer => {
        observer.next(false);
        observer.complete();
      });
    }

    return this.http.post<any>(`${this.API_URL}/validate`, { token })
      .pipe(
        map(response => response.valid === true),
        catchError(() => {
          this.handleLogout();
          return new Observable(observer => {
            observer.next(false);
            observer.complete();
          });
        })
      );
  }

  /**
   * Récupération des informations de l'utilisateur connecté
   * @returns Observable<User>
   */
  getCurrentUserInfo(): Observable<User> {
    const headers = this.getAuthHeaders();
    
    return this.http.get<User>(`${this.API_URL}/me`, { headers })
      .pipe(
        tap(user => {
          this.setUser(user);
          this.currentUserSubject.next(user);
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des informations utilisateur:', error);
          this.handleLogout();
          return throwError(() => error);
        })
      );
  }

  /**
   * Vérification si l'utilisateur est connecté
   * @returns boolean
   */
  isAuthenticated(): boolean {
    return this.hasValidToken();
  }

  /**
   * Récupération de l'utilisateur actuel
   * @returns User | null
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Récupération du token d'accès
   * @returns string | null
   */
  getAccessToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Récupération du refresh token
   * @returns string | null
   */
  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  /**
   * Vérification des permissions utilisateur
   * @param requiredRole Rôle requis
   * @returns boolean
   */
  hasRole(requiredRole: string): boolean {
    const user = this.getCurrentUser();
    return user ? user.role === requiredRole : false;
  }

  /**
   * Vérification de plusieurs rôles
   * @param roles Liste des rôles autorisés
   * @returns boolean
   */
  hasAnyRole(roles: string[]): boolean {
    const user = this.getCurrentUser();
    return user ? roles.includes(user.role) : false;
  }

  /**
   * Gestion du succès d'authentification
   * @param response Réponse d'authentification
   */
  private handleAuthSuccess(response: AuthResponse): void {
    // Stockage des tokens
    localStorage.setItem(this.TOKEN_KEY, response.accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refreshToken);
    
    // Stockage des informations utilisateur
    this.setUser(response.user);
    
    // Mise à jour des observables
    this.currentUserSubject.next(response.user);
    this.isAuthenticatedSubject.next(true);
    
    console.log('Connexion réussie pour:', response.user.email);
  }

  /**
   * Gestion de la déconnexion
   */
  private handleLogout(): void {
    // Suppression des données stockées
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    
    // Mise à jour des observables
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    
    // Redirection vers la page de connexion
    this.router.navigate(['/login']);
    
    console.log('Déconnexion effectuée');
  }

  /**
   * Stockage des informations utilisateur
   * @param user Utilisateur à stocker
   */
  private setUser(user: User): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  /**
   * Récupération des informations utilisateur depuis le stockage
   * @returns User | null
   */
  private getUserFromStorage(): User | null {
    const userJson = localStorage.getItem(this.USER_KEY);
    return userJson ? JSON.parse(userJson) : null;
  }

  /**
   * Vérification de la validité du token
   * @returns boolean
   */
  private hasValidToken(): boolean {
    const token = this.getAccessToken();
    const user = this.getUserFromStorage();
    return !!(token && user);
  }

  /**
   * Génération des headers d'authentification
   * @returns HttpHeaders
   */
  private getAuthHeaders(): HttpHeaders {
    const token = this.getAccessToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  /**
   * Timer de validation périodique du token
   */
  private startTokenValidationTimer(): void {
    // Vérification toutes les 5 minutes
    setInterval(() => {
      if (this.isAuthenticated()) {
        this.validateToken().subscribe({
          next: (isValid) => {
            if (!isValid) {
              console.log('Token invalide, tentative de rafraîchissement...');
              this.refreshToken().subscribe({
                error: () => {
                  console.log('Impossible de rafraîchir le token, déconnexion...');
                  this.handleLogout();
                }
              });
            }
          },
          error: () => {
            console.log('Erreur de validation du token, déconnexion...');
            this.handleLogout();
          }
        });
      }
    }, 5 * 60 * 1000); // 5 minutes
  }
}
