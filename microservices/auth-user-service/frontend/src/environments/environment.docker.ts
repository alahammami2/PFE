/**
 * Configuration d'environnement pour Docker
 * Microservice auth-user-service - Frontend Angular
 */
export const environment = {
  production: false,
  
  // URLs des services (via Gateway dans l'environnement Docker)
  authServiceUrl: 'http://gateway-service:8080/auth-user-service',
  apiUrl: 'http://gateway-service:8080/auth-user-service/api',
  
  // Configuration de l'authentification
  auth: {
    tokenKey: 'sprintbot_access_token',
    refreshTokenKey: 'sprintbot_refresh_token',
    userKey: 'sprintbot_user',
    tokenValidationInterval: 5 * 60 * 1000, // 5 minutes
    autoRefreshEnabled: true
  },
  
  // Configuration des timeouts
  timeouts: {
    httpRequest: 30000, // 30 secondes
    authValidation: 10000, // 10 secondes
    refreshToken: 15000 // 15 secondes
  },
  
  // Configuration du logging
  logging: {
    level: 'info',
    enableConsoleLog: true,
    enableRemoteLog: false,
    remoteLogUrl: ''
  },
  
  // Configuration de l'interface
  ui: {
    theme: 'light',
    language: 'fr',
    dateFormat: 'dd/MM/yyyy',
    timeFormat: 'HH:mm',
    pagination: {
      defaultPageSize: 10,
      pageSizeOptions: [5, 10, 25, 50, 100]
    }
  },
  
  // Configuration des fonctionnalités
  features: {
    enableUserRegistration: true,
    enablePasswordReset: true,
    enableProfilePicture: true,
    enableNotifications: true,
    enableAdvancedSearch: true,
    enableExport: true
  },
  
  // Configuration de validation
  validation: {
    password: {
      minLength: 8,
      requireUppercase: true,
      requireLowercase: true,
      requireNumbers: true,
      requireSpecialChars: false
    },
    email: {
      allowedDomains: [], // Vide = tous les domaines autorisés
      blockedDomains: ['tempmail.com', '10minutemail.com']
    }
  },
  
  // Configuration des rôles et permissions
  roles: {
    ADMINISTRATEUR: {
      label: 'Administrateur',
      permissions: ['*'], // Toutes les permissions
      color: '#dc3545',
      icon: 'admin_panel_settings'
    },
    COACH: {
      label: 'Entraîneur',
      permissions: [
        'view_players',
        'manage_training',
        'view_statistics',
        'manage_team'
      ],
      color: '#007bff',
      icon: 'sports_volleyball'
    },
    JOUEUR: {
      label: 'Joueur',
      permissions: [
        'view_profile',
        'update_profile',
        'view_training',
        'view_team'
      ],
      color: '#28a745',
      icon: 'person'
    },
    STAFF_MEDICAL: {
      label: 'Staff Médical',
      permissions: [
        'view_players',
        'manage_medical',
        'view_injuries',
        'manage_health'
      ],
      color: '#fd7e14',
      icon: 'medical_services'
    },
    RESPONSABLE_FINANCIER: {
      label: 'Responsable Financier',
      permissions: [
        'view_finances',
        'manage_budget',
        'view_salaries',
        'generate_reports'
      ],
      color: '#6f42c1',
      icon: 'account_balance'
    }
  },
  
  // Configuration des notifications
  notifications: {
    position: 'top-right',
    duration: 5000,
    maxNotifications: 5,
    enableSound: false,
    types: {
      success: { icon: 'check_circle', color: '#28a745' },
      error: { icon: 'error', color: '#dc3545' },
      warning: { icon: 'warning', color: '#ffc107' },
      info: { icon: 'info', color: '#17a2b8' }
    }
  },
  
  // Configuration des uploads
  upload: {
    maxFileSize: 5 * 1024 * 1024, // 5MB
    allowedImageTypes: ['image/jpeg', 'image/png', 'image/gif'],
    allowedDocumentTypes: ['application/pdf', 'application/msword'],
    uploadUrl: 'http://gateway-service:8080/auth-user-service/api/upload'
  },
  
  // Configuration de sécurité
  security: {
    enableCSRF: false, // Désactivé pour les API REST
    enableXSSProtection: true,
    sessionTimeout: 24 * 60 * 60 * 1000, // 24 heures
    maxLoginAttempts: 5,
    lockoutDuration: 15 * 60 * 1000 // 15 minutes
  },
  
  // Configuration de performance
  performance: {
    enableCaching: true,
    cacheTimeout: 5 * 60 * 1000, // 5 minutes
    enableLazyLoading: true,
    enableVirtualScrolling: true,
    debounceTime: 300 // ms pour les recherches
  },
  
  // URLs externes
  externalUrls: {
    documentation: 'http://gateway-service:8080/auth-user-service/swagger-ui/index.html',
    support: 'mailto:support@sprintbot.com',
    privacy: '/privacy-policy',
    terms: '/terms-of-service'
  },
  
  // Configuration de développement
  development: {
    enableMockData: false,
    enableDebugMode: true,
    showPerformanceMetrics: true,
    enableHotReload: false
  }
};
