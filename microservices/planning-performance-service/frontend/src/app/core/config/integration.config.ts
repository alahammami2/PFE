/**
 * Configuration pour l'intégration backend-frontend
 */

export interface IntegrationConfig {
  enableRealData: boolean;
  fallbackToMockData: boolean;
  retryAttempts: number;
  timeoutMs: number;
  enableLogging: boolean;
}

export const INTEGRATION_CONFIG: IntegrationConfig = {
  // Active l'utilisation des vraies données du backend
  enableRealData: true,
  
  // Utilise les données simulées en cas d'erreur
  fallbackToMockData: true,
  
  // Nombre de tentatives en cas d'échec
  retryAttempts: 2,
  
  // Timeout pour les requêtes (en millisecondes)
  timeoutMs: 10000,
  
  // Active les logs de débogage
  enableLogging: true
};

/**
 * Configuration des endpoints par service
 */
export const SERVICE_ENDPOINTS = {
  ENTRAINEMENTS: {
    enabled: true,
    endpoints: [
      'GET /api/entrainements',
      'POST /api/entrainements',
      'GET /api/entrainements/{id}',
      'PUT /api/entrainements/{id}',
      'DELETE /api/entrainements/{id}',
      'GET /api/entrainements/coach/{coachId}',
      'GET /api/entrainements/search',
      'GET /api/entrainements/statistiques'
    ]
  },
  
  PARTICIPATIONS: {
    enabled: true,
    endpoints: [
      'GET /api/participations',
      'POST /api/participations',
      'GET /api/participations/{id}',
      'PUT /api/participations/{id}',
      'DELETE /api/participations/{id}',
      'GET /api/participations/entrainement/{entrainementId}',
      'GET /api/participations/joueur/{joueurId}',
      'PATCH /api/participations/{id}/presence',
      'GET /api/participations/statistiques'
    ]
  },
  
  PERFORMANCES: {
    enabled: true,
    endpoints: [
      'GET /api/performances',
      'POST /api/performances',
      'GET /api/performances/{id}',
      'PUT /api/performances/{id}',
      'DELETE /api/performances/{id}',
      'GET /api/performances/joueur/{joueurId}',
      'GET /api/performances/entrainement/{entrainementId}',
      'GET /api/performances/search',
      'GET /api/performances/statistiques'
    ]
  },
  
  ABSENCES: {
    enabled: true,
    endpoints: [
      'GET /api/absences',
      'POST /api/absences',
      'GET /api/absences/{id}',
      'PUT /api/absences/{id}',
      'DELETE /api/absences/{id}',
      'GET /api/absences/joueur/{joueurId}',
      'GET /api/absences/entrainement/{entrainementId}',
      'PATCH /api/absences/{id}/approuver',
      'PATCH /api/absences/{id}/rejeter',
      'GET /api/absences/statistiques'
    ]
  },
  
  OBJECTIFS: {
    enabled: true,
    endpoints: [
      'GET /api/objectifs',
      'POST /api/objectifs',
      'GET /api/objectifs/{id}',
      'PUT /api/objectifs/{id}',
      'DELETE /api/objectifs/{id}',
      'GET /api/objectifs/joueur/{joueurId}',
      'PATCH /api/objectifs/{id}/progression',
      'GET /api/objectifs/statistiques'
    ]
  },
  
  STATISTIQUES: {
    enabled: true,
    endpoints: [
      'GET /api/statistiques',
      'GET /api/statistiques/globales',
      'GET /api/statistiques/joueur/{joueurId}',
      'GET /api/statistiques/equipe',
      'GET /api/statistiques/periode',
      'GET /api/statistiques/comparaison'
    ]
  }
};

/**
 * Messages d'erreur personnalisés
 */
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Erreur de connexion au serveur. Vérifiez votre connexion internet.',
  TIMEOUT_ERROR: 'La requête a pris trop de temps. Veuillez réessayer.',
  SERVER_ERROR: 'Erreur du serveur. Veuillez contacter l\'administrateur.',
  NOT_FOUND: 'Ressource non trouvée.',
  UNAUTHORIZED: 'Vous n\'êtes pas autorisé à accéder à cette ressource.',
  FORBIDDEN: 'Accès interdit.',
  VALIDATION_ERROR: 'Données invalides. Vérifiez les informations saisies.',
  UNKNOWN_ERROR: 'Une erreur inattendue s\'est produite.'
};

/**
 * Configuration des notifications
 */
export const NOTIFICATION_CONFIG = {
  SUCCESS_DURATION: 3000,
  ERROR_DURATION: 5000,
  WARNING_DURATION: 4000,
  INFO_DURATION: 3000,
  
  SHOW_SUCCESS_NOTIFICATIONS: true,
  SHOW_ERROR_NOTIFICATIONS: true,
  SHOW_WARNING_NOTIFICATIONS: true,
  SHOW_INFO_NOTIFICATIONS: true,
  
  POSITION: 'toast-top-right'
};

/**
 * Configuration du cache
 */
export const CACHE_CONFIG = {
  ENABLE_CACHE: true,
  DEFAULT_TTL: 5 * 60 * 1000, // 5 minutes
  MAX_CACHE_SIZE: 100,
  
  CACHE_KEYS: {
    ENTRAINEMENTS: 'entrainements',
    PARTICIPATIONS: 'participations',
    PERFORMANCES: 'performances',
    ABSENCES: 'absences',
    OBJECTIFS: 'objectifs',
    STATISTIQUES: 'statistiques'
  }
};

/**
 * Configuration de la pagination
 */
export const PAGINATION_CONFIG = {
  DEFAULT_PAGE_SIZE: 20,
  MAX_PAGE_SIZE: 100,
  SHOW_SIZE_OPTIONS: [10, 20, 50, 100],
  SHOW_FIRST_LAST_BUTTONS: true,
  SHOW_PAGE_SIZE_SELECTOR: true
};

/**
 * Utilitaires pour la configuration
 */
export class IntegrationConfigUtils {
  
  /**
   * Vérifie si un service est activé
   */
  static isServiceEnabled(serviceName: keyof typeof SERVICE_ENDPOINTS): boolean {
    return SERVICE_ENDPOINTS[serviceName]?.enabled || false;
  }
  
  /**
   * Obtient les endpoints d'un service
   */
  static getServiceEndpoints(serviceName: keyof typeof SERVICE_ENDPOINTS): string[] {
    return SERVICE_ENDPOINTS[serviceName]?.endpoints || [];
  }
  
  /**
   * Obtient le message d'erreur approprié
   */
  static getErrorMessage(errorCode: string): string {
    return ERROR_MESSAGES[errorCode as keyof typeof ERROR_MESSAGES] || ERROR_MESSAGES.UNKNOWN_ERROR;
  }
  
  /**
   * Vérifie si le cache est activé
   */
  static isCacheEnabled(): boolean {
    return CACHE_CONFIG.ENABLE_CACHE;
  }
  
  /**
   * Obtient la configuration de pagination par défaut
   */
  static getDefaultPaginationConfig() {
    return {
      pageSize: PAGINATION_CONFIG.DEFAULT_PAGE_SIZE,
      showSizeOptions: PAGINATION_CONFIG.SHOW_SIZE_OPTIONS,
      showFirstLastButtons: PAGINATION_CONFIG.SHOW_FIRST_LAST_BUTTONS,
      showPageSizeSelector: PAGINATION_CONFIG.SHOW_PAGE_SIZE_SELECTOR
    };
  }
}
