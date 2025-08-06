/**
 * Configuration d'environnement pour la production
 */
export const environment = {
  production: true,
  apiUrl: 'https://api.sprintbot.com/finance',
  appName: 'SprintBot Finance',
  version: '1.0.0',
  
  // Configuration des services externes
  authServiceUrl: 'https://api.sprintbot.com/auth',
  
  // Configuration des fonctionnalités
  features: {
    enableDebugMode: false,
    enableMockData: false,
    enableAnalytics: true,
    enableNotifications: true
  },
  
  // Configuration de l'authentification
  auth: {
    tokenKey: 'access_token',
    refreshTokenKey: 'refresh_token',
    userRolesKey: 'user_roles',
    tokenExpirationBuffer: 300000 // 5 minutes en millisecondes
  },
  
  // Configuration des timeouts
  timeouts: {
    httpRequest: 30000, // 30 secondes
    fileUpload: 120000, // 2 minutes
    reportGeneration: 180000 // 3 minutes
  },
  
  // Configuration de la pagination
  pagination: {
    defaultPageSize: 10,
    pageSizeOptions: [5, 10, 25, 50, 100]
  },
  
  // Configuration des formats
  formats: {
    currency: 'EUR',
    locale: 'fr-FR',
    dateFormat: 'dd/MM/yyyy',
    dateTimeFormat: 'dd/MM/yyyy HH:mm',
    numberFormat: '1.2-2'
  },
  
  // Configuration des validations
  validation: {
    maxFileSize: 10485760, // 10 MB
    allowedFileTypes: ['pdf', 'jpg', 'jpeg', 'png', 'doc', 'docx', 'xls', 'xlsx'],
    maxDescriptionLength: 1000,
    maxCommentLength: 500
  },
  
  // Configuration des notifications
  notifications: {
    autoHideDelay: 5000,
    maxNotifications: 5,
    enableSound: false
  }
};
