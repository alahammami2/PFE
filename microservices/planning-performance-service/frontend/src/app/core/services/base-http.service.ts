import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map, timeout } from 'rxjs/operators';
import { ApiConfig } from '../config/api.config';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  errors?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class BaseHttpService {

  constructor(private http: HttpClient) {}

  /**
   * Effectue une requête GET
   */
  get<T>(url: string, params?: Record<string, any>, timeoutMs?: number): Observable<T> {
    const httpParams = this.buildHttpParams(params);
    const options = {
      params: httpParams,
      headers: new HttpHeaders(ApiConfig.DEFAULT_HEADERS)
    };

    return this.http.get<T>(url, options).pipe(
      timeout(timeoutMs || ApiConfig.TIMEOUT.DEFAULT),
      catchError(this.handleError)
    );
  }

  /**
   * Effectue une requête GET avec pagination
   */
  getPage<T>(url: string, page: number = 0, size: number = 20, params?: Record<string, any>): Observable<PageResponse<T>> {
    const paginationParams = ApiConfig.buildPaginationParams(page, size);
    const allParams = { ...paginationParams, ...params };
    
    return this.get<PageResponse<T>>(url, allParams);
  }

  /**
   * Effectue une requête POST
   */
  post<T>(url: string, body: any, timeoutMs?: number): Observable<T> {
    const options = {
      headers: new HttpHeaders(ApiConfig.DEFAULT_HEADERS)
    };

    return this.http.post<T>(url, body, options).pipe(
      timeout(timeoutMs || ApiConfig.TIMEOUT.DEFAULT),
      catchError(this.handleError)
    );
  }

  /**
   * Effectue une requête PUT
   */
  put<T>(url: string, body: any, timeoutMs?: number): Observable<T> {
    const options = {
      headers: new HttpHeaders(ApiConfig.DEFAULT_HEADERS)
    };

    return this.http.put<T>(url, body, options).pipe(
      timeout(timeoutMs || ApiConfig.TIMEOUT.DEFAULT),
      catchError(this.handleError)
    );
  }

  /**
   * Effectue une requête PATCH
   */
  patch<T>(url: string, body: any, timeoutMs?: number): Observable<T> {
    const options = {
      headers: new HttpHeaders(ApiConfig.DEFAULT_HEADERS)
    };

    return this.http.patch<T>(url, body, options).pipe(
      timeout(timeoutMs || ApiConfig.TIMEOUT.DEFAULT),
      catchError(this.handleError)
    );
  }

  /**
   * Effectue une requête DELETE
   */
  delete<T>(url: string, timeoutMs?: number): Observable<T> {
    const options = {
      headers: new HttpHeaders(ApiConfig.DEFAULT_HEADERS)
    };

    return this.http.delete<T>(url, options).pipe(
      timeout(timeoutMs || ApiConfig.TIMEOUT.DEFAULT),
      catchError(this.handleError)
    );
  }

  /**
   * Upload de fichier
   */
  uploadFile<T>(url: string, file: File, additionalData?: Record<string, any>): Observable<T> {
    const formData = new FormData();
    formData.append('file', file);
    
    if (additionalData) {
      Object.keys(additionalData).forEach(key => {
        formData.append(key, additionalData[key]);
      });
    }

    // Ne pas définir Content-Type pour FormData (le navigateur le fait automatiquement)
    const options = {
      headers: new HttpHeaders({
        'Accept': 'application/json'
      })
    };

    return this.http.post<T>(url, formData, options).pipe(
      timeout(ApiConfig.TIMEOUT.UPLOAD),
      catchError(this.handleError)
    );
  }

  /**
   * Construit les paramètres HTTP
   */
  private buildHttpParams(params?: Record<string, any>): HttpParams {
    let httpParams = new HttpParams();
    
    if (params) {
      Object.keys(params).forEach(key => {
        const value = params[key];
        if (value !== null && value !== undefined) {
          if (Array.isArray(value)) {
            value.forEach(item => {
              httpParams = httpParams.append(key, item.toString());
            });
          } else {
            httpParams = httpParams.set(key, value.toString());
          }
        }
      });
    }
    
    return httpParams;
  }

  /**
   * Gestion des erreurs
   */
  private handleError = (error: any): Observable<never> => {
    console.error('Erreur HTTP:', error);
    return throwError(() => error);
  };

  /**
   * Utilitaires pour construire des URLs avec paramètres
   */
  buildUrlWithParams(baseUrl: string, params: Record<string, any>): string {
    return ApiConfig.buildUrlWithParams(baseUrl, params);
  }

  /**
   * Utilitaires pour les filtres de date
   */
  buildDateRangeParams(startDate?: Date, endDate?: Date): Record<string, any> {
    return ApiConfig.buildDateRangeParams(startDate, endDate);
  }

  /**
   * Convertit une réponse API en données utilisables
   */
  extractApiData<T>(response: ApiResponse<T>): T {
    if (response.success) {
      return response.data;
    } else {
      throw new Error(response.message || 'Erreur API');
    }
  }
}
