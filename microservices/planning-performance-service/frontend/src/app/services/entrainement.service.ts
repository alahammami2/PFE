import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import {
  Entrainement,
  EntrainementRequest,
  EntrainementFilters,
  EntrainementStats,
  StatutEntrainement
} from '../models/entrainement.model';
import { BaseHttpService, PageResponse } from '../core/services/base-http.service';
import { ApiConfig } from '../core/config/api.config';

@Injectable({
  providedIn: 'root'
})
export class EntrainementService {
  private entrainementsSubject = new BehaviorSubject<Entrainement[]>([]);
  public entrainements$ = this.entrainementsSubject.asObservable();

  constructor(
    private http: HttpClient,
    private baseHttpService: BaseHttpService
  ) {}

  // CRUD Operations
  getAllEntrainements(): Observable<Entrainement[]> {
    return this.baseHttpService.get<Entrainement[]>(ApiConfig.ENTRAINEMENTS.BASE).pipe(
      tap(entrainements => this.entrainementsSubject.next(entrainements)),
      catchError(error => {
        console.error('Erreur lors du chargement des entraînements:', error);
        throw error;
      })
    );
  }

  getEntrainementById(id: number): Observable<Entrainement> {
    return this.baseHttpService.get<Entrainement>(ApiConfig.ENTRAINEMENTS.BY_ID(id)).pipe(
      catchError(error => {
        console.error(`Erreur lors du chargement de l'entraînement ${id}:`, error);
        throw error;
      })
    );
  }

  createEntrainement(entrainement: EntrainementRequest): Observable<Entrainement> {
    return this.baseHttpService.post<Entrainement>(ApiConfig.ENTRAINEMENTS.BASE, entrainement).pipe(
      tap(() => this.refreshEntrainements()),
      catchError(error => {
        console.error('Erreur lors de la création de l\'entraînement:', error);
        throw error;
      })
    );
  }

  updateEntrainement(id: number, entrainement: EntrainementRequest): Observable<Entrainement> {
    return this.baseHttpService.put<Entrainement>(ApiConfig.ENTRAINEMENTS.BY_ID(id), entrainement).pipe(
      tap(() => this.refreshEntrainements()),
      catchError(error => {
        console.error(`Erreur lors de la mise à jour de l'entraînement ${id}:`, error);
        throw error;
      })
    );
  }

  deleteEntrainement(id: number): Observable<void> {
    return this.baseHttpService.delete<void>(ApiConfig.ENTRAINEMENTS.BY_ID(id)).pipe(
      tap(() => this.refreshEntrainements()),
      catchError(error => {
        console.error(`Erreur lors de la suppression de l'entraînement ${id}:`, error);
        throw error;
      })
    );
  }

  // Gestion du statut
  demarrerEntrainement(id: number): Observable<Entrainement> {
    return this.baseHttpService.patch<Entrainement>(`${ApiConfig.ENTRAINEMENTS.BY_ID(id)}/demarrer`, {}).pipe(
      tap(() => this.refreshEntrainements()),
      catchError(error => {
        console.error(`Erreur lors du démarrage de l'entraînement ${id}:`, error);
        throw error;
      })
    );
  }

  terminerEntrainement(id: number): Observable<Entrainement> {
    return this.baseHttpService.patch<Entrainement>(`${ApiConfig.ENTRAINEMENTS.BY_ID(id)}/terminer`, {}).pipe(
      tap(() => this.refreshEntrainements()),
      catchError(error => {
        console.error(`Erreur lors de la fin de l'entraînement ${id}:`, error);
        throw error;
      })
    );
  }

  annulerEntrainement(id: number, raison?: string): Observable<Entrainement> {
    const body = raison ? { raison } : {};
    return this.baseHttpService.patch<Entrainement>(`${ApiConfig.ENTRAINEMENTS.BY_ID(id)}/annuler`, body).pipe(
      tap(() => this.refreshEntrainements()),
      catchError(error => {
        console.error(`Erreur lors de l'annulation de l'entraînement ${id}:`, error);
        throw error;
      })
    );
  }

  // Recherche et filtrage
  searchEntrainements(filters: EntrainementFilters): Observable<Entrainement[]> {
    const params: Record<string, any> = {};

    if (filters.coachId) params.coachId = filters.coachId;
    if (filters.type) params.type = filters.type;
    if (filters.statut) params.statut = filters.statut;
    if (filters.dateDebut) params.dateDebut = filters.dateDebut;
    if (filters.dateFin) params.dateFin = filters.dateFin;
    if (filters.lieu) params.lieu = filters.lieu;

    return this.baseHttpService.get<Entrainement[]>(ApiConfig.ENTRAINEMENTS.SEARCH, params).pipe(
      catchError(error => {
        console.error('Erreur lors de la recherche d\'entraînements:', error);
        throw error;
      })
    );
  }

  getEntrainementsByCoach(coachId: number): Observable<Entrainement[]> {
    return this.baseHttpService.get<Entrainement[]>(ApiConfig.ENTRAINEMENTS.BY_COACH(coachId)).pipe(
      catchError(error => {
        console.error(`Erreur lors du chargement des entraînements du coach ${coachId}:`, error);
        throw error;
      })
    );
  }

  getEntrainementsByDateRange(dateDebut: string, dateFin: string): Observable<Entrainement[]> {
    const params = { dateDebut, dateFin };

    return this.baseHttpService.get<Entrainement[]>(ApiConfig.ENTRAINEMENTS.BY_DATE_RANGE, params).pipe(
      catchError(error => {
        console.error('Erreur lors du chargement des entraînements par période:', error);
        throw error;
      })
    );
  }

  getEntrainementsProchains(limite: number = 10): Observable<Entrainement[]> {
    const params = { limite: limite.toString() };
    return this.baseHttpService.get<Entrainement[]>(`${ApiConfig.ENTRAINEMENTS.BASE}/prochains`, params).pipe(
      catchError(error => {
        console.error('Erreur lors du chargement des prochains entraînements:', error);
        throw error;
      })
    );
  }

  // Statistiques
  getStatistiques(): Observable<EntrainementStats> {
    return this.baseHttpService.get<EntrainementStats>(ApiConfig.ENTRAINEMENTS.STATISTICS).pipe(
      catchError(error => {
        console.error('Erreur lors du chargement des statistiques d\'entraînements:', error);
        throw error;
      })
    );
  }

  getStatistiquesCoach(coachId: number): Observable<EntrainementStats> {
    return this.baseHttpService.get<EntrainementStats>(`${ApiConfig.ENTRAINEMENTS.STATISTICS}/coach/${coachId}`).pipe(
      catchError(error => {
        console.error(`Erreur lors du chargement des statistiques du coach ${coachId}:`, error);
        throw error;
      })
    );
  }

  getStatistiquesPeriode(dateDebut: string, dateFin: string): Observable<EntrainementStats> {
    const params = { dateDebut, dateFin };

    return this.baseHttpService.get<EntrainementStats>(`${ApiConfig.ENTRAINEMENTS.STATISTICS}/periode`, params).pipe(
      catchError(error => {
        console.error('Erreur lors du chargement des statistiques par période:', error);
        throw error;
      })
    );
  }

  // Validation et vérifications
  verifierDisponibilite(date: string, heureDebut: string, heureFin: string, lieu: string): Observable<boolean> {
    const params = { date, heureDebut, heureFin, lieu };

    return this.baseHttpService.get<{disponible: boolean}>(`${ApiConfig.ENTRAINEMENTS.BASE}/disponibilite`, params).pipe(
      map(response => response.disponible),
      catchError(error => {
        console.error('Erreur lors de la vérification de disponibilité:', error);
        throw error;
      })
    );
  }

  verifierConflitHoraire(coachId: number, date: string, heureDebut: string, heureFin: string): Observable<boolean> {
    const params = new HttpParams()
      .set('coachId', coachId.toString())
      .set('date', date)
      .set('heureDebut', heureDebut)
      .set('heureFin', heureFin);
    
    return this.http.get<{conflit: boolean}>(`${this.baseUrl}/conflit-horaire`, { params })
      .pipe(map(response => response.conflit));
  }

  // Calendrier
  getCalendrierMois(annee: number, mois: number): Observable<Entrainement[]> {
    const params = new HttpParams()
      .set('annee', annee.toString())
      .set('mois', mois.toString());
    
    return this.http.get<Entrainement[]>(`${this.baseUrl}/calendrier`, { params });
  }

  // Gestion des places
  verifierPlacesDisponibles(id: number): Observable<{disponibles: number, total: number}> {
    return this.http.get<{disponibles: number, total: number}>(`${this.baseUrl}/${id}/places`);
  }

  // Export et rapports
  exporterEntrainements(format: 'pdf' | 'excel', filters?: EntrainementFilters): Observable<Blob> {
    let params = new HttpParams().set('format', format);
    
    if (filters) {
      if (filters.coachId) params = params.set('coachId', filters.coachId.toString());
      if (filters.type) params = params.set('type', filters.type);
      if (filters.statut) params = params.set('statut', filters.statut);
      if (filters.dateDebut) params = params.set('dateDebut', filters.dateDebut);
      if (filters.dateFin) params = params.set('dateFin', filters.dateFin);
    }

    return this.http.get(`${this.baseUrl}/export`, { 
      params, 
      responseType: 'blob' 
    });
  }

  // Méthodes utilitaires
  private refreshEntrainements(): void {
    this.getAllEntrainements().subscribe();
  }

  // Méthodes de formatage et validation côté client
  isEntrainementModifiable(entrainement: Entrainement): boolean {
    return entrainement.statut === StatutEntrainement.PLANIFIE;
  }

  isEntrainementAnnulable(entrainement: Entrainement): boolean {
    return entrainement.statut === StatutEntrainement.PLANIFIE || 
           entrainement.statut === StatutEntrainement.EN_COURS;
  }

  canDemarrerEntrainement(entrainement: Entrainement): boolean {
    if (entrainement.statut !== StatutEntrainement.PLANIFIE) return false;
    
    const maintenant = new Date();
    const dateEntrainement = new Date(`${entrainement.date}T${entrainement.heureDebut}`);
    
    // Peut démarrer 15 minutes avant l'heure prévue
    const quinzeMinutesAvant = new Date(dateEntrainement.getTime() - 15 * 60 * 1000);
    
    return maintenant >= quinzeMinutesAvant;
  }

  canTerminerEntrainement(entrainement: Entrainement): boolean {
    return entrainement.statut === StatutEntrainement.EN_COURS;
  }

  formatDuree(heureDebut: string, heureFin: string): string {
    const debut = new Date(`2000-01-01T${heureDebut}`);
    const fin = new Date(`2000-01-01T${heureFin}`);
    const dureeMs = fin.getTime() - debut.getTime();
    const dureeMinutes = dureeMs / (1000 * 60);

    const heures = Math.floor(dureeMinutes / 60);
    const minutes = dureeMinutes % 60;

    if (heures > 0) {
      return minutes > 0 ? `${heures}h${minutes.toString().padStart(2, '0')}` : `${heures}h`;
    }
    return `${minutes}min`;
  }

  // Méthodes de cache local
  clearCache(): void {
    this.entrainementsSubject.next([]);
  }

  getCurrentEntrainements(): Entrainement[] {
    return this.entrainementsSubject.value;
  }
}
