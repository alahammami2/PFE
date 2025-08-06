import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { map, catchError, timeout } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ApiConfig } from '../config/api.config';
import { INTEGRATION_CONFIG, SERVICE_ENDPOINTS } from '../config/integration.config';

export interface ValidationResult {
  service: string;
  endpoint: string;
  status: 'success' | 'error' | 'timeout';
  statusCode?: number;
  message: string;
  responseTime: number;
}

export interface IntegrationReport {
  timestamp: Date;
  totalTests: number;
  successCount: number;
  errorCount: number;
  timeoutCount: number;
  averageResponseTime: number;
  results: ValidationResult[];
  overallStatus: 'healthy' | 'degraded' | 'unhealthy';
}

@Injectable({
  providedIn: 'root'
})
export class IntegrationValidatorService {

  constructor(private http: HttpClient) {}

  /**
   * Valide l'intégration complète backend-frontend
   */
  validateIntegration(): Observable<IntegrationReport> {
    const startTime = Date.now();
    
    const validationTests = this.createValidationTests();
    
    return forkJoin(validationTests).pipe(
      map(results => this.generateReport(results, startTime)),
      catchError(error => {
        console.error('Erreur lors de la validation d\'intégration:', error);
        return of(this.generateErrorReport(startTime));
      })
    );
  }

  /**
   * Valide un service spécifique
   */
  validateService(serviceName: string): Observable<ValidationResult[]> {
    const serviceEndpoints = this.getServiceTestEndpoints(serviceName);
    const tests = serviceEndpoints.map(endpoint => this.validateEndpoint(serviceName, endpoint));
    
    return forkJoin(tests).pipe(
      catchError(error => {
        console.error(`Erreur lors de la validation du service ${serviceName}:`, error);
        return of([]);
      })
    );
  }

  /**
   * Valide un endpoint spécifique
   */
  validateEndpoint(service: string, endpoint: string): Observable<ValidationResult> {
    const startTime = Date.now();
    const url = this.buildEndpointUrl(endpoint);
    
    return this.http.get(url, { observe: 'response' }).pipe(
      timeout(INTEGRATION_CONFIG.timeoutMs),
      map(response => ({
        service,
        endpoint,
        status: 'success' as const,
        statusCode: response.status,
        message: 'Endpoint accessible',
        responseTime: Date.now() - startTime
      })),
      catchError(error => {
        const responseTime = Date.now() - startTime;
        
        if (error.name === 'TimeoutError') {
          return of({
            service,
            endpoint,
            status: 'timeout' as const,
            message: 'Timeout de la requête',
            responseTime
          });
        }
        
        return of({
          service,
          endpoint,
          status: 'error' as const,
          statusCode: error.status,
          message: this.getErrorMessage(error),
          responseTime
        });
      })
    );
  }

  /**
   * Teste la connectivité de base
   */
  testBasicConnectivity(): Observable<boolean> {
    const healthUrl = `${environment.apiUrl.replace('/api', '')}/actuator/health`;
    
    return this.http.get(healthUrl).pipe(
      timeout(5000),
      map(() => true),
      catchError(() => of(false))
    );
  }

  /**
   * Teste l'authentification
   */
  testAuthentication(): Observable<boolean> {
    // Test avec un endpoint qui nécessite une authentification
    return this.http.get(ApiConfig.ENTRAINEMENTS.BASE).pipe(
      timeout(5000),
      map(response => true),
      catchError(error => {
        // Si l'erreur est 401, l'endpoint fonctionne mais l'auth est requise
        if (error.status === 401) {
          return of(true);
        }
        return of(false);
      })
    );
  }

  /**
   * Crée la liste des tests de validation
   */
  private createValidationTests(): Observable<ValidationResult>[] {
    const tests: Observable<ValidationResult>[] = [];
    
    Object.entries(SERVICE_ENDPOINTS).forEach(([serviceName, config]) => {
      if (config.enabled) {
        config.endpoints.forEach(endpoint => {
          // Ne tester que les endpoints GET pour la validation
          if (endpoint.startsWith('GET ')) {
            const cleanEndpoint = endpoint.replace('GET ', '');
            // Remplacer les paramètres par des valeurs de test
            const testEndpoint = this.replacePathParameters(cleanEndpoint);
            tests.push(this.validateEndpoint(serviceName, testEndpoint));
          }
        });
      }
    });
    
    return tests;
  }

  /**
   * Obtient les endpoints de test pour un service
   */
  private getServiceTestEndpoints(serviceName: string): string[] {
    const serviceConfig = SERVICE_ENDPOINTS[serviceName as keyof typeof SERVICE_ENDPOINTS];
    if (!serviceConfig || !serviceConfig.enabled) {
      return [];
    }
    
    return serviceConfig.endpoints
      .filter(endpoint => endpoint.startsWith('GET '))
      .map(endpoint => this.replacePathParameters(endpoint.replace('GET ', '')));
  }

  /**
   * Remplace les paramètres de chemin par des valeurs de test
   */
  private replacePathParameters(endpoint: string): string {
    return endpoint
      .replace('{id}', '1')
      .replace('{joueurId}', '1')
      .replace('{coachId}', '1')
      .replace('{entrainementId}', '1');
  }

  /**
   * Construit l'URL complète pour un endpoint
   */
  private buildEndpointUrl(endpoint: string): string {
    if (endpoint.startsWith('/api/')) {
      return `${environment.apiUrl}${endpoint.substring(4)}`;
    }
    return `${environment.apiUrl}${endpoint}`;
  }

  /**
   * Génère le rapport de validation
   */
  private generateReport(results: ValidationResult[], startTime: number): IntegrationReport {
    const successCount = results.filter(r => r.status === 'success').length;
    const errorCount = results.filter(r => r.status === 'error').length;
    const timeoutCount = results.filter(r => r.status === 'timeout').length;
    
    const averageResponseTime = results.reduce((sum, r) => sum + r.responseTime, 0) / results.length;
    
    let overallStatus: 'healthy' | 'degraded' | 'unhealthy';
    const successRate = successCount / results.length;
    
    if (successRate >= 0.9) {
      overallStatus = 'healthy';
    } else if (successRate >= 0.7) {
      overallStatus = 'degraded';
    } else {
      overallStatus = 'unhealthy';
    }
    
    return {
      timestamp: new Date(),
      totalTests: results.length,
      successCount,
      errorCount,
      timeoutCount,
      averageResponseTime,
      results,
      overallStatus
    };
  }

  /**
   * Génère un rapport d'erreur
   */
  private generateErrorReport(startTime: number): IntegrationReport {
    return {
      timestamp: new Date(),
      totalTests: 0,
      successCount: 0,
      errorCount: 1,
      timeoutCount: 0,
      averageResponseTime: Date.now() - startTime,
      results: [],
      overallStatus: 'unhealthy'
    };
  }

  /**
   * Obtient le message d'erreur approprié
   */
  private getErrorMessage(error: any): string {
    if (error.status === 0) {
      return 'Impossible de se connecter au serveur';
    } else if (error.status === 404) {
      return 'Endpoint non trouvé';
    } else if (error.status === 401) {
      return 'Authentification requise';
    } else if (error.status === 403) {
      return 'Accès interdit';
    } else if (error.status >= 500) {
      return 'Erreur du serveur';
    } else {
      return error.message || 'Erreur inconnue';
    }
  }
}
