/**
 * Configuration d'environnement pour la production
 * Microservice auth-user-service - Frontend Angular
 */
export const environment = {
  production: true,
  
  // URLs des services (à adapter selon l'environnement de production)
  authServiceUrl: 'https://api.sprintbot.com/auth-user-service',
  apiUrl: 'https://api.sprintbot.com/auth-user-service/api',
  
  // Configuration de l'authentification
  auth: {
    tokenKey: 'sprintbot_access_token',
    refreshTokenKey: 'sprintbot_refresh_token',
    userKey: 'sprintbot_user',
    tokenValidationInterval: 10 * 60 * 1000, // 10 minutes en production
    autoRefreshEnabled: true
  },
  
  // Configuration des timeouts (plus longs en production)
  timeouts: {
    httpRequest: 60000, // 60 secondes
    authValidation: 20000, // 20 secondes
    refreshToken: 30000 // 30 secondes
  },
  
  // Configuration du logging (réduit en production)
  logging: {
    level: 'error',
    enableConsoleLog: false,
    enableRemoteLog: true,
    remoteLogUrl: 'https://api.sprintbot.com/logging/frontend'
  },
  
  // Configuration de l'interface
  ui: {
    theme: 'light',
    language: 'fr',
    dateFormat: 'dd/MM/yyyy',
    timeFormat: 'HH:mm',
    pagination: {
      defaultPageSize: 25,
      pageSizeOptions: [10, 25, 50, 100]
    }
  },
  
  // Configuration des fonctionnalités
  features: {
    enableUserRegistration: false, // Désactivé en production
    enablePasswordReset: true,
    enableProfilePicture: true,
    enableNotifications: true,
    enableAdvancedSearch: true,
    enableExport: true
  },
  
  // Configuration de validation (plus stricte en production)
  validation: {
    password: {
      minLength: 12,
      requireUppercase: true,
      requireLowercase: true,
      requireNumbers: true,
      requireSpecialChars: true
    },
    email: {
      allowedDomains: ['sprintbot.com', 'company.com'],
      blockedDomains: [
        'tempmail.com', 
        '10minutemail.com', 
        'guerrillamail.com',
        'mailinator.com'
      ]
    }
  },
  
  // Configuration des rôles et permissions
  roles: {
    ADMINISTRATEUR: {
      label: 'Administrateur',
      permissions: ['*'],
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
    duration: 3000, // Plus court en production
    maxNotifications: 3,
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
    maxFileSize: 2 * 1024 * 1024, // 2MB en production
    allowedImageTypes: ['image/jpeg', 'image/png'],
    allowedDocumentTypes: ['application/pdf'],
    uploadUrl: 'https://api.sprintbot.com/auth-user-service/api/upload'
  },
  
  // Configuration de sécurité (renforcée en production)
  security: {
    enableCSRF: false,
    enableXSSProtection: true,
    sessionTimeout: 8 * 60 * 60 * 1000, // 8 heures
    maxLoginAttempts: 3,
    lockoutDuration: 30 * 60 * 1000 // 30 minutes
  },
  
  // Configuration de performance (optimisée pour la production)
  performance: {
    enableCaching: true,
    cacheTimeout: 15 * 60 * 1000, // 15 minutes
    enableLazyLoading: true,
    enableVirtualScrolling: true,
    debounceTime: 500 // Plus long pour réduire les requêtes
  },
  
  // URLs externes
  externalUrls: {
    documentation: 'https://docs.sprintbot.com/auth-user-service',
    support: 'mailto:support@sprintbot.com',
    privacy: 'https://sprintbot.com/privacy-policy',
    terms: 'https://sprintbot.com/terms-of-service'
  },
  
  // Configuration de production
  development: {
    enableMockData: false,
    enableDebugMode: false,
    showPerformanceMetrics: false,
    enableHotReload: false
  }
};
