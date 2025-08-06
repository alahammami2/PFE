import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import {
  Participation,
  CreateParticipationDto,
  UpdateParticipationDto,
  ParticipationFilters,
  ParticipationStats,
  StatutParticipation
} from '../models/participation.model';
import { BaseHttpService, PageResponse } from '../core/services/base-http.service';
import { ApiConfig } from '../core/config/api.config';

@Injectable({
  providedIn: 'root'
})
export class ParticipationService {
  private participationsSubject = new BehaviorSubject<Participation[]>([]);
  public participations$ = this.participationsSubject.asObservable();

  constructor(
    private http: HttpClient,
    private baseHttpService: BaseHttpService
  ) {}

  // CRUD Operations
  getAllParticipations(): Observable<Participation[]> {
    return this.baseHttpService.get<Participation[]>(ApiConfig.PARTICIPATIONS.BASE).pipe(
      tap(participations => this.participationsSubject.next(participations)),
      catchError(error => {
        console.error('Erreur lors du chargement des participations:', error);
        throw error;
      })
    );
  }

  getParticipationById(id: number): Observable<Participation> {
    return this.baseHttpService.get<Participation>(ApiConfig.PARTICIPATIONS.BY_ID(id)).pipe(
      catchError(error => {
        console.error(`Erreur lors du chargement de la participation ${id}:`, error);
        throw error;
      })
    );
  }

  createParticipation(participation: CreateParticipationDto): Observable<Participation> {
    return this.baseHttpService.post<Participation>(ApiConfig.PARTICIPATIONS.BASE, participation).pipe(
      tap(() => this.refreshParticipations()),
      catchError(error => {
        console.error('Erreur lors de la création de la participation:', error);
        throw error;
      })
    );
  }

  updateParticipation(id: number, participation: UpdateParticipationDto): Observable<Participation> {
    return this.baseHttpService.put<Participation>(ApiConfig.PARTICIPATIONS.BY_ID(id), participation).pipe(
      tap(() => this.refreshParticipations()),
      catchError(error => {
        console.error(`Erreur lors de la mise à jour de la participation ${id}:`, error);
        throw error;
      })
    );
  }

  deleteParticipation(id: number): Observable<void> {
    return this.baseHttpService.delete<void>(ApiConfig.PARTICIPATIONS.BY_ID(id)).pipe(
      tap(() => this.refreshParticipations()),
      catchError(error => {
        console.error(`Erreur lors de la suppression de la participation ${id}:`, error);
        throw error;
      })
    );
  }

  // Gestion des présences
  marquerPresence(participationId: number): Observable<Participation> {
    return this.baseHttpService.patch<Participation>(ApiConfig.PARTICIPATIONS.PRESENCE(participationId), {}).pipe(
      tap(() => this.refreshParticipations()),
      catchError(error => {
        console.error(`Erreur lors du marquage de présence ${participationId}:`, error);
        throw error;
      })
    );
  }

  marquerAbsence(participationId: number): Observable<Participation> {
    return this.http.patch<Participation>(`${this.baseUrl}/${participationId}/absence`, {}).pipe(
      tap(() => this.refreshParticipations())
    );
  }

  marquerPresenceMultiple(participationIds: number[]): Observable<Participation[]> {
    return this.http.patch<Participation[]>(`${this.baseUrl}/presence/multiple`, { participationIds }).pipe(
      tap(() => this.refreshParticipations())
    );
  }

  marquerAbsenceMultiple(participationIds: number[]): Observable<Participation[]> {
    return this.http.patch<Participation[]>(`${this.baseUrl}/absence/multiple`, { participationIds }).pipe(
      tap(() => this.refreshParticipations())
    );
  }

  // Recherche et filtrage
  searchParticipations(filters: ParticipationFilters): Observable<Participation[]> {
    let params = new HttpParams();
    
    if (filters.entrainementId) {
      params = params.set('entrainementId', filters.entrainementId.toString());
    }
    if (filters.joueurId) {
      params = params.set('joueurId', filters.joueurId.toString());
    }
    if (filters.statut) {
      params = params.set('statut', filters.statut);
    }
    if (filters.dateDebut) {
      params = params.set('dateDebut', filters.dateDebut);
    }
    if (filters.dateFin) {
      params = params.set('dateFin', filters.dateFin);
    }

    return this.http.get<Participation[]>(`${this.baseUrl}/search`, { params });
  }

  getParticipationsByEntrainement(entrainementId: number): Observable<Participation[]> {
    return this.http.get<Participation[]>(`${this.baseUrl}/entrainement/${entrainementId}`);
  }

  getParticipationsByJoueur(joueurId: number): Observable<Participation[]> {
    return this.http.get<Participation[]>(`${this.baseUrl}/joueur/${joueurId}`);
  }

  // Statistiques
  getStatistiques(): Observable<ParticipationStats> {
    return this.http.get<ParticipationStats>(`${this.baseUrl}/statistiques`);
  }

  getStatistiquesJoueur(joueurId: number): Observable<ParticipationStats> {
    return this.http.get<ParticipationStats>(`${this.baseUrl}/statistiques/joueur/${joueurId}`);
  }

  getStatistiquesEntrainement(entrainementId: number): Observable<ParticipationStats> {
    return this.http.get<ParticipationStats>(`${this.baseUrl}/statistiques/entrainement/${entrainementId}`);
  }

  getTauxPresenceJoueur(joueurId: number, dateDebut?: string, dateFin?: string): Observable<number> {
    let params = new HttpParams();
    if (dateDebut) params = params.set('dateDebut', dateDebut);
    if (dateFin) params = params.set('dateFin', dateFin);

    return this.http.get<{ tauxPresence: number }>(`${this.baseUrl}/joueur/${joueurId}/taux-presence`, { params })
      .pipe(map(response => response.tauxPresence));
  }

  // Inscriptions
  inscrireJoueur(entrainementId: number, joueurId: number): Observable<Participation> {
    return this.http.post<Participation>(`${this.baseUrl}/inscription`, {
      entrainementId,
      joueurId
    }).pipe(
      tap(() => this.refreshParticipations())
    );
  }

  desinscrireJoueur(entrainementId: number, joueurId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/inscription/${entrainementId}/${joueurId}`).pipe(
      tap(() => this.refreshParticipations())
    );
  }

  // Export
  exporterParticipations(format: 'excel' | 'pdf', filters?: ParticipationFilters): Observable<Blob> {
    let params = new HttpParams().set('format', format);
    
    if (filters) {
      if (filters.entrainementId) params = params.set('entrainementId', filters.entrainementId.toString());
      if (filters.joueurId) params = params.set('joueurId', filters.joueurId.toString());
      if (filters.statut) params = params.set('statut', filters.statut);
      if (filters.dateDebut) params = params.set('dateDebut', filters.dateDebut);
      if (filters.dateFin) params = params.set('dateFin', filters.dateFin);
    }

    return this.http.get(`${this.baseUrl}/export`, {
      params,
      responseType: 'blob'
    });
  }

  // Validation et utilitaires
  canMarquerPresence(participation: Participation): boolean {
    return participation.statut === StatutParticipation.INSCRIT;
  }

  canMarquerAbsence(participation: Participation): boolean {
    return participation.statut === StatutParticipation.INSCRIT || 
           participation.statut === StatutParticipation.PRESENT;
  }

  canDesinscrire(participation: Participation): boolean {
    return participation.statut === StatutParticipation.INSCRIT;
  }

  isParticipationModifiable(participation: Participation): boolean {
    // Logique métier pour déterminer si une participation peut être modifiée
    const now = new Date();
    const heureInscription = new Date(participation.heureInscription);
    const diffHours = (now.getTime() - heureInscription.getTime()) / (1000 * 60 * 60);
    
    // Modifiable dans les 24h suivant l'inscription
    return diffHours < 24 && participation.statut === StatutParticipation.INSCRIT;
  }

  calculatePresenceRate(participations: Participation[]): number {
    if (participations.length === 0) return 0;
    
    const presences = participations.filter(p => p.statut === StatutParticipation.PRESENT).length;
    return (presences / participations.length) * 100;
  }

  getParticipationTrend(participations: Participation[]): 'up' | 'down' | 'stable' {
    if (participations.length < 2) return 'stable';
    
    // Calculer la tendance sur les 5 dernières participations
    const recent = participations.slice(-5);
    const older = participations.slice(-10, -5);
    
    const recentRate = this.calculatePresenceRate(recent);
    const olderRate = this.calculatePresenceRate(older);
    
    const diff = recentRate - olderRate;
    
    if (diff > 5) return 'up';
    if (diff < -5) return 'down';
    return 'stable';
  }

  // Méthodes de cache
  private refreshParticipations(): void {
    this.getAllParticipations().subscribe();
  }

  clearCache(): void {
    this.participationsSubject.next([]);
  }

  getCurrentParticipations(): Participation[] {
    return this.participationsSubject.value;
  }

  // Méthodes utilitaires pour l'affichage
  formatHeureInscription(heureInscription: string): string {
    return new Date(heureInscription).toLocaleString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getDelaiInscription(heureInscription: string): string {
    const now = new Date();
    const inscription = new Date(heureInscription);
    const diffMs = now.getTime() - inscription.getTime();
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffDays = Math.floor(diffHours / 24);
    
    if (diffDays > 0) {
      return `Il y a ${diffDays} jour${diffDays > 1 ? 's' : ''}`;
    } else if (diffHours > 0) {
      return `Il y a ${diffHours} heure${diffHours > 1 ? 's' : ''}`;
    } else {
      const diffMinutes = Math.floor(diffMs / (1000 * 60));
      return `Il y a ${diffMinutes} minute${diffMinutes > 1 ? 's' : ''}`;
    }
  }

  // Notifications et alertes
  getParticipationsRequiringAttention(): Observable<Participation[]> {
    return this.http.get<Participation[]>(`${this.baseUrl}/attention`);
  }

  getJoueursAbsentsFrequents(seuil: number = 30): Observable<{ joueurId: number; tauxAbsence: number }[]> {
    return this.http.get<{ joueurId: number; tauxAbsence: number }[]>(
      `${this.baseUrl}/absents-frequents?seuil=${seuil}`
    );
  }
}
