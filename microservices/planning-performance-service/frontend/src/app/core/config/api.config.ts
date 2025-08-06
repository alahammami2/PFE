import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiConfig {
  
  // URL de base de l'API
  public static readonly BASE_URL = environment.apiUrl || 'http://localhost:8082/api';
  
  // Endpoints pour les entraînements
  public static readonly ENTRAINEMENTS = {
    BASE: `${ApiConfig.BASE_URL}/entrainements`,
    BY_ID: (id: number) => `${ApiConfig.BASE_URL}/entrainements/${id}`,
    BY_COACH: (coachId: number) => `${ApiConfig.BASE_URL}/entrainements/coach/${coachId}`,
    BY_DATE_RANGE: `${ApiConfig.BASE_URL}/entrainements/periode`,
    SEARCH: `${ApiConfig.BASE_URL}/entrainements/search`,
    STATISTICS: `${ApiConfig.BASE_URL}/entrainements/statistiques`
  };

  // Endpoints pour les participations
  public static readonly PARTICIPATIONS = {
    BASE: `${ApiConfig.BASE_URL}/participations`,
    BY_ID: (id: number) => `${ApiConfig.BASE_URL}/participations/${id}`,
    BY_ENTRAINEMENT: (entrainementId: number) => `${ApiConfig.BASE_URL}/participations/entrainement/${entrainementId}`,
    BY_JOUEUR: (joueurId: number) => `${ApiConfig.BASE_URL}/participations/joueur/${joueurId}`,
    PRESENCE: (id: number) => `${ApiConfig.BASE_URL}/participations/${id}/presence`,
    STATISTICS: `${ApiConfig.BASE_URL}/participations/statistiques`
  };

  // Endpoints pour les performances
  public static readonly PERFORMANCES = {
    BASE: `${ApiConfig.BASE_URL}/performances`,
    BY_ID: (id: number) => `${ApiConfig.BASE_URL}/performances/${id}`,
    BY_JOUEUR: (joueurId: number) => `${ApiConfig.BASE_URL}/performances/joueur/${joueurId}`,
    BY_ENTRAINEMENT: (entrainementId: number) => `${ApiConfig.BASE_URL}/performances/entrainement/${entrainementId}`,
    BY_CATEGORIE: (categorie: string) => `${ApiConfig.BASE_URL}/performances/categorie/${categorie}`,
    EVOLUTION: (joueurId: number) => `${ApiConfig.BASE_URL}/performances/joueur/${joueurId}/evolution`,
    MOYENNE: (joueurId: number) => `${ApiConfig.BASE_URL}/performances/joueur/${joueurId}/moyenne`,
    STATISTICS: `${ApiConfig.BASE_URL}/performances/statistiques`
  };

  // Endpoints pour les absences
  public static readonly ABSENCES = {
    BASE: `${ApiConfig.BASE_URL}/absences`,
    BY_ID: (id: number) => `${ApiConfig.BASE_URL}/absences/${id}`,
    BY_JOUEUR: (joueurId: number) => `${ApiConfig.BASE_URL}/absences/joueur/${joueurId}`,
    BY_ENTRAINEMENT: (entrainementId: number) => `${ApiConfig.BASE_URL}/absences/entrainement/${entrainementId}`,
    BY_STATUT: (statut: string) => `${ApiConfig.BASE_URL}/absences/statut/${statut}`,
    APPROVE: (id: number) => `${ApiConfig.BASE_URL}/absences/${id}/approuver`,
    REJECT: (id: number) => `${ApiConfig.BASE_URL}/absences/${id}/rejeter`,
    STATISTICS: `${ApiConfig.BASE_URL}/absences/statistiques`
  };

  // Endpoints pour les objectifs
  public static readonly OBJECTIFS = {
    BASE: `${ApiConfig.BASE_URL}/objectifs`,
    BY_ID: (id: number) => `${ApiConfig.BASE_URL}/objectifs/${id}`,
    BY_JOUEUR: (joueurId: number) => `${ApiConfig.BASE_URL}/objectifs/joueur/${joueurId}`,
    BY_STATUT: (statut: string) => `${ApiConfig.BASE_URL}/objectifs/statut/${statut}`,
    UPDATE_PROGRESS: (id: number) => `${ApiConfig.BASE_URL}/objectifs/${id}/progression`,
    STATISTICS: `${ApiConfig.BASE_URL}/objectifs/statistiques`
  };

  // Endpoints pour les statistiques
  public static readonly STATISTIQUES = {
    BASE: `${ApiConfig.BASE_URL}/statistiques`,
    DASHBOARD: `${ApiConfig.BASE_URL}/statistiques/dashboard`,
    JOUEUR: (joueurId: number) => `${ApiConfig.BASE_URL}/statistiques/joueur/${joueurId}`,
    EQUIPE: `${ApiConfig.BASE_URL}/statistiques/equipe`,
    PERFORMANCE_EVOLUTION: (joueurId: number) => `${ApiConfig.BASE_URL}/statistiques/performance-evolution/${joueurId}`,
    PRESENCE_STATS: (joueurId: number) => `${ApiConfig.BASE_URL}/statistiques/presence/${joueurId}`,
    GLOBAL_STATS: `${ApiConfig.BASE_URL}/statistiques/global`
  };

  // Headers par défaut
  public static readonly DEFAULT_HEADERS = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  };

  // Configuration des timeouts
  public static readonly TIMEOUT = {
    DEFAULT: 30000, // 30 secondes
    UPLOAD: 60000,  // 1 minute pour les uploads
    LONG_RUNNING: 120000 // 2 minutes pour les opérations longues
  };

  // Configuration de pagination
  public static readonly PAGINATION = {
    DEFAULT_PAGE: 0,
    DEFAULT_SIZE: 20,
    MAX_SIZE: 100
  };

  // Configuration des filtres de date
  public static readonly DATE_FILTERS = {
    LAST_7_DAYS: 7,
    LAST_30_DAYS: 30,
    LAST_90_DAYS: 90,
    LAST_6_MONTHS: 180,
    LAST_YEAR: 365
  };

  // Messages d'erreur par défaut
  public static readonly ERROR_MESSAGES = {
    NETWORK_ERROR: 'Erreur de connexion au serveur',
    TIMEOUT_ERROR: 'Délai d\'attente dépassé',
    SERVER_ERROR: 'Erreur interne du serveur',
    NOT_FOUND: 'Ressource non trouvée',
    UNAUTHORIZED: 'Accès non autorisé',
    FORBIDDEN: 'Accès interdit',
    VALIDATION_ERROR: 'Erreur de validation des données'
  };

  /**
   * Construit une URL avec des paramètres de requête
   */
  public static buildUrlWithParams(baseUrl: string, params: Record<string, any>): string {
    const url = new URL(baseUrl);
    Object.keys(params).forEach(key => {
      if (params[key] !== null && params[key] !== undefined) {
        url.searchParams.append(key, params[key].toString());
      }
    });
    return url.toString();
  }

  /**
   * Construit les paramètres de pagination
   */
  public static buildPaginationParams(page: number = 0, size: number = 20, sort?: string): Record<string, any> {
    const params: Record<string, any> = {
      page: Math.max(0, page),
      size: Math.min(size, ApiConfig.PAGINATION.MAX_SIZE)
    };
    
    if (sort) {
      params.sort = sort;
    }
    
    return params;
  }

  /**
   * Construit les paramètres de filtre de date
   */
  public static buildDateRangeParams(startDate?: Date, endDate?: Date): Record<string, any> {
    const params: Record<string, any> = {};
    
    if (startDate) {
      params.startDate = startDate.toISOString().split('T')[0];
    }
    
    if (endDate) {
      params.endDate = endDate.toISOString().split('T')[0];
    }
    
    return params;
  }
}
