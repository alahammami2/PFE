import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private loadingCountSubject = new BehaviorSubject<number>(0);
  
  public loading$ = this.loadingSubject.asObservable();
  public loadingCount$ = this.loadingCountSubject.asObservable();

  constructor() {}

  /**
   * Démarre le chargement
   */
  startLoading(): void {
    const currentCount = this.loadingCountSubject.value;
    this.loadingCountSubject.next(currentCount + 1);
    this.loadingSubject.next(true);
  }

  /**
   * Arrête le chargement
   */
  stopLoading(): void {
    const currentCount = this.loadingCountSubject.value;
    const newCount = Math.max(0, currentCount - 1);
    this.loadingCountSubject.next(newCount);
    
    if (newCount === 0) {
      this.loadingSubject.next(false);
    }
  }

  /**
   * Force l'arrêt du chargement
   */
  forceStopLoading(): void {
    this.loadingCountSubject.next(0);
    this.loadingSubject.next(false);
  }

  /**
   * Vérifie si le chargement est en cours
   */
  isLoading(): boolean {
    return this.loadingSubject.value;
  }

  /**
   * Obtient le nombre de chargements en cours
   */
  getLoadingCount(): number {
    return this.loadingCountSubject.value;
  }
}
