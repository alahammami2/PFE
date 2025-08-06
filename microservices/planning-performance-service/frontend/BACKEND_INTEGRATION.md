# Guide d'Intégration Backend - Frontend

## Vue d'ensemble

Ce document explique comment connecter le frontend Angular avec le backend Spring Boot du microservice planning-performance-service.

## Architecture mise en place

### 1. Configuration API (ApiConfig)
- **Fichier**: `src/app/core/config/api.config.ts`
- **Rôle**: Centralise toutes les URLs des endpoints backend
- **Utilisation**: Remplace les URLs hardcodées dans les services

### 2. Service HTTP de base (BaseHttpService)
- **Fichier**: `src/app/core/services/base-http.service.ts`
- **Rôle**: Fournit des méthodes HTTP réutilisables avec gestion d'erreurs
- **Fonctionnalités**:
  - GET, POST, PUT, PATCH, DELETE
  - Pagination automatique
  - Upload de fichiers
  - Gestion des timeouts

### 3. Intercepteurs HTTP

#### AuthInterceptor
- **Fichier**: `src/app/core/interceptors/auth.interceptor.ts`
- **Rôle**: Ajoute automatiquement le token JWT aux requêtes

#### HttpErrorInterceptor
- **Fichier**: `src/app/core/interceptors/http-error.interceptor.ts`
- **Rôle**: Gère les erreurs HTTP globalement
- **Fonctionnalités**:
  - Retry automatique
  - Messages d'erreur personnalisés
  - Redirection en cas d'erreur d'authentification

#### LoadingInterceptor
- **Fichier**: `src/app/core/interceptors/loading.interceptor.ts`
- **Rôle**: Gère automatiquement les états de chargement

### 4. Services utilitaires

#### NotificationService
- **Fichier**: `src/app/core/services/notification.service.ts`
- **Rôle**: Gère les notifications utilisateur (toasts)

#### LoadingService
- **Fichier**: `src/app/core/services/loading.service.ts`
- **Rôle**: Gère les états de chargement globaux

## Migration des services

### Avant (données simulées)
```typescript
getAllEntrainements(): Observable<Entrainement[]> {
  return of(this.mockData).pipe(delay(500));
}
```

### Après (appels HTTP réels)
```typescript
getAllEntrainements(): Observable<Entrainement[]> {
  return this.baseHttpService.get<Entrainement[]>(ApiConfig.ENTRAINEMENTS.BASE).pipe(
    tap(entrainements => this.entrainementsSubject.next(entrainements)),
    catchError(error => {
      this.notificationService.error('Erreur lors du chargement des entraînements');
      console.error('Erreur:', error);
      throw error;
    })
  );
}
```

## Services à migrer

### ✅ Complété
- [x] EntrainementService (migration complète)
- [x] ParticipationService (migration complète)
- [x] PerformanceService (migration complète)
- [x] AbsenceService (migration complète)
- [x] ObjectifService (migration complète)
- [x] StatistiqueService (migration complète)
- [x] ChartDataService (intégration backend)

### 📋 Étapes pour chaque service

1. **Importer les dépendances**:
   ```typescript
   import { BaseHttpService } from '../core/services/base-http.service';
   import { ApiConfig } from '../core/config/api.config';
   import { NotificationService } from '../core/services/notification.service';
   ```

2. **Injecter les services**:
   ```typescript
   constructor(
     private http: HttpClient,
     private baseHttpService: BaseHttpService,
     private notificationService: NotificationService
   ) {}
   ```

3. **Remplacer les appels HTTP**:
   - Utiliser `baseHttpService` au lieu de `http`
   - Utiliser les URLs de `ApiConfig`
   - Ajouter la gestion d'erreurs avec `catchError`
   - Ajouter les notifications appropriées

4. **Tester les endpoints**:
   - Vérifier que le backend est démarré
   - Tester chaque endpoint avec des données réelles
   - Valider la gestion d'erreurs

## Configuration des environnements

### Développement
```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8082/api',
  authServiceUrl: 'http://localhost:8081/api',
  enableLogging: true,
  enableMockData: false
};
```

### Production
```typescript
// src/environments/environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.sprintbot.com/planning-performance',
  authServiceUrl: 'https://api.sprintbot.com/auth',
  enableLogging: false,
  enableMockData: false
};
```

## Gestion des erreurs

### Types d'erreurs gérées
- **400**: Erreurs de validation
- **401**: Non authentifié (redirection vers login)
- **403**: Accès interdit
- **404**: Ressource non trouvée
- **500**: Erreur serveur
- **0**: Erreur réseau

### Notifications automatiques
- Erreurs affichées via toasts
- Messages personnalisés selon le type d'erreur
- Possibilité de désactiver pour certaines erreurs

## Tests et validation

### Script de test automatique
Un script de test d'intégration est disponible pour vérifier tous les endpoints :

```bash
# Exécuter le test d'intégration
cd microservices/planning-performance-service/frontend
node test-integration.js
```

### Service de validation intégré
Le service `IntegrationValidatorService` permet de tester l'intégration depuis l'application :

```typescript
// Injection du service
constructor(private integrationValidator: IntegrationValidatorService) {}

// Test complet
this.integrationValidator.validateIntegration().subscribe(report => {
  console.log('Rapport d\'intégration:', report);
});

// Test d'un service spécifique
this.integrationValidator.validateService('ENTRAINEMENTS').subscribe(results => {
  console.log('Résultats pour les entraînements:', results);
});
```

### Vérifications à effectuer
1. **Backend démarré**: `http://localhost:8082/actuator/health`
2. **Endpoints fonctionnels**: Utiliser le script de test
3. **CORS configuré**: Vérifier les headers CORS
4. **Authentification**: Tester avec et sans token
5. **Données réelles**: Vérifier que les services utilisent les vraies données

### Commandes utiles
```bash
# Démarrer le backend
cd microservices/planning-performance-service/backend
mvn spring-boot:run

# Démarrer le frontend
cd microservices/planning-performance-service/frontend
npm start

# Tester tous les endpoints
node test-integration.js

# Tester un endpoint spécifique
curl -X GET http://localhost:8082/api/entrainements

# Vérifier la santé du service
curl -X GET http://localhost:8082/actuator/health
```

## ✅ Intégration terminée

### Fonctionnalités implémentées
1. ✅ **Migration complète de tous les services**
2. ✅ **ChartDataService utilise les vraies données backend**
3. ✅ **Infrastructure HTTP complète** (intercepteurs, gestion d'erreurs, loading)
4. ✅ **Configuration centralisée** (ApiConfig, IntegrationConfig)
5. ✅ **Services de validation et test** automatiques
6. ✅ **Gestion des notifications** utilisateur
7. ✅ **Fallback vers données simulées** en cas d'erreur

### Prochaines optimisations possibles
1. **Cache intelligent** pour réduire les appels API
2. **Lazy loading** des composants lourds
3. **Tests d'intégration automatisés** dans CI/CD
4. **Monitoring** des performances en temps réel
5. **Retry intelligent** avec backoff exponentiel

## Dépannage

### Problèmes courants
- **CORS**: Vérifier la configuration dans le backend
- **401 Unauthorized**: Vérifier le token JWT
- **404 Not Found**: Vérifier les URLs des endpoints
- **Timeout**: Augmenter les timeouts dans ApiConfig

### Logs utiles
- Console du navigateur pour les erreurs frontend
- Logs Spring Boot pour les erreurs backend
- Network tab pour analyser les requêtes HTTP
