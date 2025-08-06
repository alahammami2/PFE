import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import {
  Performance,
  CreatePerformanceDto,
  UpdatePerformanceDto,
  PerformanceFilters,
  PerformanceStats,
  PerformanceAnalytics,
  CategoriePerformance
} from '../models/performance.model';
import { BaseHttpService, PageResponse } from '../core/services/base-http.service';
import { ApiConfig } from '../core/config/api.config';
import { NotificationService } from '../core/services/notification.service';

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {
  private performancesSubject = new BehaviorSubject<Performance[]>([]);
  public performances$ = this.performancesSubject.asObservable();

  constructor(
    private http: HttpClient,
    private baseHttpService: BaseHttpService,
    private notificationService: NotificationService
  ) {}

  // CRUD Operations
  getAllPerformances(): Observable<Performance[]> {
    return this.baseHttpService.get<Performance[]>(ApiConfig.PERFORMANCES.BASE).pipe(
      tap(performances => this.performancesSubject.next(performances)),
      catchError(error => {
        this.notificationService.error('Erreur lors du chargement des performances');
        console.error('Erreur lors du chargement des performances:', error);
        throw error;
      })
    );
  }

  getPerformanceById(id: number): Observable<Performance> {
    return this.http.get<Performance>(`${this.baseUrl}/${id}`);
  }

  createPerformance(performance: CreatePerformanceDto): Observable<Performance> {
    return this.http.post<Performance>(this.baseUrl, performance).pipe(
      tap(() => this.refreshPerformances())
    );
  }

  updatePerformance(id: number, performance: UpdatePerformanceDto): Observable<Performance> {
    return this.http.put<Performance>(`${this.baseUrl}/${id}`, performance).pipe(
      tap(() => this.refreshPerformances())
    );
  }

  deletePerformance(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => this.refreshPerformances())
    );
  }

  // Recherche et filtrage
  searchPerformances(filters: PerformanceFilters): Observable<Performance[]> {
    let params = new HttpParams();
    
    if (filters.entrainementId) {
      params = params.set('entrainementId', filters.entrainementId.toString());
    }
    if (filters.joueurId) {
      params = params.set('joueurId', filters.joueurId.toString());
    }
    if (filters.categorie) {
      params = params.set('categorie', filters.categorie);
    }
    if (filters.noteMin) {
      params = params.set('noteMin', filters.noteMin.toString());
    }
    if (filters.noteMax) {
      params = params.set('noteMax', filters.noteMax.toString());
    }
    if (filters.dateDebut) {
      params = params.set('dateDebut', filters.dateDebut);
    }
    if (filters.dateFin) {
      params = params.set('dateFin', filters.dateFin);
    }

    return this.http.get<Performance[]>(`${this.baseUrl}/search`, { params });
  }

  getPerformancesByEntrainement(entrainementId: number): Observable<Performance[]> {
    return this.http.get<Performance[]>(`${this.baseUrl}/entrainement/${entrainementId}`);
  }

  getPerformancesByJoueur(joueurId: number): Observable<Performance[]> {
    return this.http.get<Performance[]>(`${this.baseUrl}/joueur/${joueurId}`);
  }

  getPerformancesByCategorie(categorie: CategoriePerformance): Observable<Performance[]> {
    return this.http.get<Performance[]>(`${this.baseUrl}/categorie/${categorie}`);
  }

  // Statistiques et analytics
  getStatistiques(): Observable<PerformanceStats> {
    return this.http.get<PerformanceStats>(`${this.baseUrl}/statistiques`);
  }

  getStatistiquesJoueur(joueurId: number): Observable<PerformanceStats> {
    return this.http.get<PerformanceStats>(`${this.baseUrl}/statistiques/joueur/${joueurId}`);
  }

  getStatistiquesEntrainement(entrainementId: number): Observable<PerformanceStats> {
    return this.http.get<PerformanceStats>(`${this.baseUrl}/statistiques/entrainement/${entrainementId}`);
  }

  getAnalyticsJoueur(joueurId: number, dateDebut?: string, dateFin?: string): Observable<PerformanceAnalytics> {
    let params = new HttpParams();
    if (dateDebut) params = params.set('dateDebut', dateDebut);
    if (dateFin) params = params.set('dateFin', dateFin);

    return this.http.get<PerformanceAnalytics>(`${this.baseUrl}/analytics/joueur/${joueurId}`, { params });
  }

  getAnalyticsEquipe(dateDebut?: string, dateFin?: string): Observable<PerformanceAnalytics> {
    let params = new HttpParams();
    if (dateDebut) params = params.set('dateDebut', dateDebut);
    if (dateFin) params = params.set('dateFin', dateFin);

    return this.http.get<PerformanceAnalytics>(`${this.baseUrl}/analytics/equipe`, { params });
  }

  getEvolutionJoueur(joueurId: number, categorie?: CategoriePerformance): Observable<{ date: string; note: number }[]> {
    let params = new HttpParams();
    if (categorie) params = params.set('categorie', categorie);

    return this.http.get<{ date: string; note: number }[]>(`${this.baseUrl}/evolution/joueur/${joueurId}`, { params });
  }

  getComparaisonJoueurs(joueurIds: number[], categorie?: CategoriePerformance): Observable<any> {
    let params = new HttpParams();
    joueurIds.forEach(id => params = params.append('joueurIds', id.toString()));
    if (categorie) params = params.set('categorie', categorie);

    return this.http.get(`${this.baseUrl}/comparaison`, { params });
  }

  // Évaluations en lot
  createPerformancesEnLot(entrainementId: number, performances: CreatePerformanceDto[]): Observable<Performance[]> {
    return this.http.post<Performance[]>(`${this.baseUrl}/lot`, {
      entrainementId,
      performances
    }).pipe(
      tap(() => this.refreshPerformances())
    );
  }

  // Export
  exporterPerformances(format: 'excel' | 'pdf', filters?: PerformanceFilters): Observable<Blob> {
    let params = new HttpParams().set('format', format);
    
    if (filters) {
      if (filters.entrainementId) params = params.set('entrainementId', filters.entrainementId.toString());
      if (filters.joueurId) params = params.set('joueurId', filters.joueurId.toString());
      if (filters.categorie) params = params.set('categorie', filters.categorie);
      if (filters.dateDebut) params = params.set('dateDebut', filters.dateDebut);
      if (filters.dateFin) params = params.set('dateFin', filters.dateFin);
    }

    return this.http.get(`${this.baseUrl}/export`, {
      params,
      responseType: 'blob'
    });
  }

  // Validation et utilitaires
  validateNote(note: number): boolean {
    return note >= 0 && note <= 10;
  }

  calculateMoyenne(performances: Performance[]): number {
    if (performances.length === 0) return 0;
    const total = performances.reduce((sum, perf) => sum + perf.note, 0);
    return Math.round((total / performances.length) * 100) / 100;
  }

  calculateMoyenneParCategorie(performances: Performance[]): { [categorie: string]: number } {
    const groupes = this.groupByCategorie(performances);
    const moyennes: { [categorie: string]: number } = {};
    
    Object.keys(groupes).forEach(categorie => {
      moyennes[categorie] = this.calculateMoyenne(groupes[categorie]);
    });
    
    return moyennes;
  }

  groupByCategorie(performances: Performance[]): { [categorie: string]: Performance[] } {
    return performances.reduce((acc, performance) => {
      if (!acc[performance.categorie]) {
        acc[performance.categorie] = [];
      }
      acc[performance.categorie].push(performance);
      return acc;
    }, {} as { [categorie: string]: Performance[] });
  }

  getPerformanceTrend(performances: Performance[]): 'up' | 'down' | 'stable' {
    if (performances.length < 2) return 'stable';
    
    // Trier par date
    const sorted = [...performances].sort((a, b) => 
      new Date(a.dateEvaluation).getTime() - new Date(b.dateEvaluation).getTime()
    );
    
    // Calculer la tendance sur les 5 dernières évaluations
    const recent = sorted.slice(-5);
    const older = sorted.slice(-10, -5);
    
    const recentAvg = this.calculateMoyenne(recent);
    const olderAvg = this.calculateMoyenne(older);
    
    const diff = recentAvg - olderAvg;
    
    if (diff > 0.5) return 'up';
    if (diff < -0.5) return 'down';
    return 'stable';
  }

  getPerformanceLevel(note: number): 'excellent' | 'bon' | 'moyen' | 'faible' {
    if (note >= 8) return 'excellent';
    if (note >= 6) return 'bon';
    if (note >= 4) return 'moyen';
    return 'faible';
  }

  getPerformanceLevelColor(level: string): string {
    switch (level) {
      case 'excellent': return '#4caf50';
      case 'bon': return '#8bc34a';
      case 'moyen': return '#ff9800';
      case 'faible': return '#f44336';
      default: return '#9e9e9e';
    }
  }

  // Recommandations
  getRecommandations(joueurId: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/recommandations/joueur/${joueurId}`);
  }

  getPointsAmelioration(joueurId: number): Observable<{ categorie: string; priorite: number; suggestion: string }[]> {
    return this.http.get<{ categorie: string; priorite: number; suggestion: string }[]>(
      `${this.baseUrl}/ameliorations/joueur/${joueurId}`
    );
  }

  // Objectifs de performance
  definirObjectifPerformance(joueurId: number, categorie: CategoriePerformance, objectif: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/objectifs`, {
      joueurId,
      categorie,
      objectif
    });
  }

  getObjectifsJoueur(joueurId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/objectifs/joueur/${joueurId}`);
  }

  // Méthodes de cache
  private refreshPerformances(): void {
    this.getAllPerformances().subscribe();
  }

  clearCache(): void {
    this.performancesSubject.next([]);
  }

  getCurrentPerformances(): Performance[] {
    return this.performancesSubject.value;
  }

  // Méthodes utilitaires pour l'affichage
  formatNote(note: number): string {
    return note.toFixed(1);
  }

  formatDateEvaluation(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getProgressionIcon(trend: 'up' | 'down' | 'stable'): string {
    switch (trend) {
      case 'up': return 'trending_up';
      case 'down': return 'trending_down';
      case 'stable': return 'trending_flat';
      default: return 'help';
    }
  }

  getProgressionColor(trend: 'up' | 'down' | 'stable'): string {
    switch (trend) {
      case 'up': return '#4caf50';
      case 'down': return '#f44336';
      case 'stable': return '#ff9800';
      default: return '#9e9e9e';
    }
  }

  // Notifications et alertes
  getPerformancesRequiringAttention(): Observable<Performance[]> {
    return this.http.get<Performance[]>(`${this.baseUrl}/attention`);
  }

  getJoueursPerformanceFaible(seuil: number = 4): Observable<{ joueurId: number; moyenneGenerale: number }[]> {
    return this.http.get<{ joueurId: number; moyenneGenerale: number }[]>(
      `${this.baseUrl}/performance-faible?seuil=${seuil}`
    );
  }

  getAlertesBaissePerformance(): Observable<{ joueurId: number; categorie: string; baisse: number }[]> {
    return this.http.get<{ joueurId: number; categorie: string; baisse: number }[]>(
      `${this.baseUrl}/alertes/baisse-performance`
    );
  }
}
