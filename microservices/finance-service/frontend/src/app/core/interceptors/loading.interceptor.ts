import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { NgxLoadingService } from 'ngx-loading';

/**
 * Interceptor pour gérer l'affichage du loading global
 */
@Injectable()
export class LoadingInterceptor implements HttpInterceptor {
  
  private activeRequests = 0;

  constructor(private loadingService: NgxLoadingService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    
    // Ignore les requêtes qui ne doivent pas déclencher le loading
    if (this.shouldIgnoreRequest(req)) {
      return next.handle(req);
    }
    
    // Incrémente le compteur de requêtes actives
    this.activeRequests++;
    
    // Affiche le loading si c'est la première requête
    if (this.activeRequests === 1) {
      this.loadingService.start();
    }
    
    return next.handle(req).pipe(
      finalize(() => {
        // Décrémente le compteur de requêtes actives
        this.activeRequests--;
        
        // Cache le loading si plus aucune requête active
        if (this.activeRequests === 0) {
          this.loadingService.stop();
        }
      })
    );
  }

  /**
   * Détermine si une requête doit être ignorée pour le loading
   */
  private shouldIgnoreRequest(req: HttpRequest<any>): boolean {
    // Ignore les requêtes avec le header 'X-Skip-Loading'
    if (req.headers.has('X-Skip-Loading')) {
      return true;
    }
    
    // Ignore certaines URLs (par exemple les requêtes de polling)
    const ignoredUrls = [
      '/api/health',
      '/api/ping',
      '/api/notifications/count'
    ];
    
    return ignoredUrls.some(url => req.url.includes(url));
  }
}
