import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { AuthService, User } from './auth.service';

export interface CreateUserRequest {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  role: string;
  telephone?: string;
  specialite?: string;
  departement?: string;
  poste?: string;
  taille?: number;
  poids?: number;
}

export interface UpdateProfilRequest {
  nom: string;
  prenom: string;
  email: string;
  telephone?: string;
  avatarUrl?: string;
}

export interface ChangePasswordRequest {
  nouveauMotDePasse: string;
}

export interface UserStatistics {
  totalUtilisateurs: number;
  utilisateursActifs: number;
  utilisateursInactifs: number;
  repartitionParRole: { [role: string]: number };
  nouvellesConnexions7j: number;
  derniereMiseAJour: string;
}

/**
 * Service de gestion des utilisateurs pour le microservice auth-user-service
 * Gère les opérations CRUD sur les utilisateurs
 */
@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = `${environment.authServiceUrl}/api/users`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  /**
   * Récupération de tous les utilisateurs
   * @returns Observable<User[]>
   */
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.API_URL, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Récupération d'un utilisateur par son ID
   * @param id ID de l'utilisateur
   * @returns Observable<User>
   */
  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/${id}`, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Récupération des utilisateurs par rôle
   * @param role Rôle recherché
   * @returns Observable<User[]>
   */
  getUsersByRole(role: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.API_URL}/role/${role}`, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Recherche d'utilisateurs
   * @param searchTerm Terme de recherche
   * @returns Observable<User[]>
   */
  searchUsers(searchTerm: string): Observable<User[]> {
    const params = new HttpParams().set('searchTerm', searchTerm);
    
    return this.http.get<User[]>(`${this.API_URL}/search`, { 
      headers: this.getAuthHeaders(),
      params 
    }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Récupération des utilisateurs actifs
   * @returns Observable<User[]>
   */
  getActiveUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.API_URL}/active`, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Création d'un nouvel utilisateur
   * @param createRequest Données de création
   * @returns Observable<User>
   */
  createUser(createRequest: CreateUserRequest): Observable<User> {
    return this.http.post<User>(this.API_URL, createRequest, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Mise à jour d'un utilisateur
   * @param id ID de l'utilisateur
   * @param updateRequest Données de mise à jour
   * @returns Observable<User>
   */
  updateUser(id: number, updateRequest: UpdateProfilRequest): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/${id}`, updateRequest, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Changement de mot de passe
   * @param id ID de l'utilisateur
   * @param passwordRequest Nouveau mot de passe
   * @returns Observable<any>
   */
  changePassword(id: number, passwordRequest: ChangePasswordRequest): Observable<any> {
    return this.http.put(`${this.API_URL}/${id}/password`, passwordRequest, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Activation d'un utilisateur
   * @param id ID de l'utilisateur
   * @returns Observable<User>
   */
  activateUser(id: number): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/${id}/activate`, {}, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Désactivation d'un utilisateur
   * @param id ID de l'utilisateur
   * @returns Observable<User>
   */
  deactivateUser(id: number): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/${id}/deactivate`, {}, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Suppression d'un utilisateur
   * @param id ID de l'utilisateur
   * @returns Observable<any>
   */
  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${id}`, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Récupération des statistiques des utilisateurs
   * @returns Observable<UserStatistics>
   */
  getUserStatistics(): Observable<UserStatistics> {
    return this.http.get<UserStatistics>(`${this.API_URL}/statistics`, { headers: this.getAuthHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Vérification de l'existence d'un email
   * @param email Email à vérifier
   * @returns Observable<boolean>
   */
  checkEmailExists(email: string): Observable<boolean> {
    const params = new HttpParams().set('email', email);
    
    return this.http.get<any>(`${this.API_URL}/check-email`, { 
      headers: this.getAuthHeaders(),
      params 
    }).pipe(
      map(response => response.exists),
      catchError(this.handleError)
    );
  }

  /**
   * Création d'un joueur avec propriétés spécifiques
   * @param userData Données du joueur
   * @returns Observable<User>
   */
  createJoueur(userData: CreateUserRequest): Observable<User> {
    const joueurData = {
      ...userData,
      role: 'JOUEUR'
    };
    
    return this.createUser(joueurData);
  }

  /**
   * Création d'un coach avec propriétés spécifiques
   * @param userData Données du coach
   * @returns Observable<User>
   */
  createCoach(userData: CreateUserRequest): Observable<User> {
    const coachData = {
      ...userData,
      role: 'COACH'
    };
    
    return this.createUser(coachData);
  }

  /**
   * Création d'un administrateur
   * @param userData Données de l'administrateur
   * @returns Observable<User>
   */
  createAdministrateur(userData: CreateUserRequest): Observable<User> {
    const adminData = {
      ...userData,
      role: 'ADMINISTRATEUR'
    };
    
    return this.createUser(adminData);
  }

  /**
   * Création d'un membre du staff médical
   * @param userData Données du staff médical
   * @returns Observable<User>
   */
  createStaffMedical(userData: CreateUserRequest): Observable<User> {
    const staffData = {
      ...userData,
      role: 'STAFF_MEDICAL'
    };
    
    return this.createUser(staffData);
  }

  /**
   * Création d'un responsable financier
   * @param userData Données du responsable financier
   * @returns Observable<User>
   */
  createResponsableFinancier(userData: CreateUserRequest): Observable<User> {
    const financierData = {
      ...userData,
      role: 'RESPONSABLE_FINANCIER'
    };
    
    return this.createUser(financierData);
  }

  /**
   * Filtrage des utilisateurs par critères
   * @param filters Critères de filtrage
   * @returns Observable<User[]>
   */
  filterUsers(filters: {
    role?: string;
    actif?: boolean;
    searchTerm?: string;
  }): Observable<User[]> {
    let params = new HttpParams();
    
    if (filters.role) {
      return this.getUsersByRole(filters.role);
    }
    
    if (filters.actif !== undefined) {
      if (filters.actif) {
        return this.getActiveUsers();
      }
    }
    
    if (filters.searchTerm) {
      return this.searchUsers(filters.searchTerm);
    }
    
    return this.getAllUsers();
  }

  /**
   * Génération des headers d'authentification
   * @returns HttpHeaders
   */
  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getAccessToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  /**
   * Gestion des erreurs HTTP
   * @param error Erreur HTTP
   * @returns Observable<never>
   */
  private handleError = (error: any): Observable<never> => {
    console.error('Erreur dans UserService:', error);
    
    let errorMessage = 'Une erreur est survenue';
    
    if (error.error?.message) {
      errorMessage = error.error.message;
    } else if (error.message) {
      errorMessage = error.message;
    }
    
    return throwError(() => new Error(errorMessage));
  };
}
