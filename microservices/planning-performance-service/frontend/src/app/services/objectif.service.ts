import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import {
  ObjectifIndividuel,
  CreateObjectifDto,
  UpdateObjectifDto,
  ObjectifFilters,
  ObjectifStats,
  TypeObjectif,
  StatutObjectif,
  PrioriteObjectif
} from '../models/objectif.model';
import { BaseHttpService, PageResponse } from '../core/services/base-http.service';
import { ApiConfig } from '../core/config/api.config';

@Injectable({
  providedIn: 'root'
})
export class ObjectifService {
  private objectifsSubject = new BehaviorSubject<ObjectifIndividuel[]>([]);
  public objectifs$ = this.objectifsSubject.asObservable();

  constructor(
    private http: HttpClient,
    private baseHttpService: BaseHttpService
  ) {}

  // CRUD Operations
  getAllObjectifs(): Observable<ObjectifIndividuel[]> {
    return this.baseHttpService.get<ObjectifIndividuel[]>(ApiConfig.OBJECTIFS.BASE).pipe(
      tap(objectifs => this.objectifsSubject.next(objectifs)),
      catchError(error => {
        console.error('Erreur lors du chargement des objectifs:', error);
        throw error;
      })
    );
  }

  getObjectifById(id: number): Observable<ObjectifIndividuel> {
    return this.baseHttpService.get<ObjectifIndividuel>(ApiConfig.OBJECTIFS.BY_ID(id)).pipe(
      catchError(error => {
        console.error(`Erreur lors du chargement de l'objectif ${id}:`, error);
        throw error;
      })
    );
  }

  createObjectif(objectif: CreateObjectifDto): Observable<ObjectifIndividuel> {
    return this.http.post<ObjectifIndividuel>(this.baseUrl, objectif).pipe(
      tap(() => this.refreshObjectifs())
    );
  }

  updateObjectif(id: number, objectif: UpdateObjectifDto): Observable<ObjectifIndividuel> {
    return this.http.put<ObjectifIndividuel>(`${this.baseUrl}/${id}`, objectif).pipe(
      tap(() => this.refreshObjectifs())
    );
  }

  deleteObjectif(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => this.refreshObjectifs())
    );
  }

  // Recherche et filtrage
  searchObjectifs(filters: ObjectifFilters): Observable<ObjectifIndividuel[]> {
    let params = new HttpParams();
    
    if (filters.joueurId) {
      params = params.set('joueurId', filters.joueurId.toString());
    }
    if (filters.type) {
      params = params.set('type', filters.type);
    }
    if (filters.statut) {
      params = params.set('statut', filters.statut);
    }
    if (filters.priorite) {
      params = params.set('priorite', filters.priorite);
    }
    if (filters.dateDebutMin) {
      params = params.set('dateDebutMin', filters.dateDebutMin);
    }
    if (filters.dateDebutMax) {
      params = params.set('dateDebutMax', filters.dateDebutMax);
    }
    if (filters.dateFinMin) {
      params = params.set('dateFinMin', filters.dateFinMin);
    }
    if (filters.dateFinMax) {
      params = params.set('dateFinMax', filters.dateFinMax);
    }
    if (filters.progressionMin !== undefined) {
      params = params.set('progressionMin', filters.progressionMin.toString());
    }
    if (filters.progressionMax !== undefined) {
      params = params.set('progressionMax', filters.progressionMax.toString());
    }

    return this.http.get<ObjectifIndividuel[]>(`${this.baseUrl}/search`, { params });
  }

  getObjectifsByJoueur(joueurId: number): Observable<ObjectifIndividuel[]> {
    return this.http.get<ObjectifIndividuel[]>(`${this.baseUrl}/joueur/${joueurId}`);
  }

  getObjectifsByType(type: TypeObjectif): Observable<ObjectifIndividuel[]> {
    return this.http.get<ObjectifIndividuel[]>(`${this.baseUrl}/type/${type}`);
  }

  getObjectifsByStatut(statut: StatutObjectif): Observable<ObjectifIndividuel[]> {
    return this.http.get<ObjectifIndividuel[]>(`${this.baseUrl}/statut/${statut}`);
  }

  getObjectifsByPriorite(priorite: PrioriteObjectif): Observable<ObjectifIndividuel[]> {
    return this.http.get<ObjectifIndividuel[]>(`${this.baseUrl}/priorite/${priorite}`);
  }

  // Gestion de la progression
  updateProgression(id: number, progression: number, commentaire?: string): Observable<ObjectifIndividuel> {
    return this.http.patch<ObjectifIndividuel>(`${this.baseUrl}/${id}/progression`, {
      progression,
      commentaire
    }).pipe(
      tap(() => this.refreshObjectifs())
    );
  }

  marquerObjectifAtteint(id: number, commentaire?: string): Observable<ObjectifIndividuel> {
    return this.http.patch<ObjectifIndividuel>(`${this.baseUrl}/${id}/atteint`, {
      commentaire
    }).pipe(
      tap(() => this.refreshObjectifs())
    );
  }

  marquerObjectifEchoue(id: number, raisonEchec: string): Observable<ObjectifIndividuel> {
    return this.http.patch<ObjectifIndividuel>(`${this.baseUrl}/${id}/echoue`, {
      raisonEchec
    }).pipe(
      tap(() => this.refreshObjectifs())
    );
  }

  prolongerObjectif(id: number, nouvelleDateFin: string, justification: string): Observable<ObjectifIndividuel> {
    return this.http.patch<ObjectifIndividuel>(`${this.baseUrl}/${id}/prolonger`, {
      nouvelleDateFin,
      justification
    }).pipe(
      tap(() => this.refreshObjectifs())
    );
  }

  // Statistiques
  getStatistiques(): Observable<ObjectifStats> {
    return this.http.get<ObjectifStats>(`${this.baseUrl}/statistiques`);
  }

  getStatistiquesJoueur(joueurId: number): Observable<ObjectifStats> {
    return this.http.get<ObjectifStats>(`${this.baseUrl}/statistiques/joueur/${joueurId}`);
  }

  getStatistiquesPeriode(dateDebut: string, dateFin: string): Observable<ObjectifStats> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    
    return this.http.get<ObjectifStats>(`${this.baseUrl}/statistiques/periode`, { params });
  }

  getTauxReussite(): Observable<{ [joueurId: string]: number }> {
    return this.http.get<{ [joueurId: string]: number }>(`${this.baseUrl}/taux-reussite`);
  }

  getTendanceObjectifs(): Observable<{ date: string; nombreObjectifs: number; tauxReussite: number }[]> {
    return this.http.get<{ date: string; nombreObjectifs: number; tauxReussite: number }[]>(`${this.baseUrl}/tendance`);
  }

  // Objectifs en retard ou à risque
  getObjectifsEnRetard(): Observable<ObjectifIndividuel[]> {
    return this.http.get<ObjectifIndividuel[]>(`${this.baseUrl}/en-retard`);
  }

  getObjectifsARisque(): Observable<ObjectifIndividuel[]> {
    return this.http.get<ObjectifIndividuel[]>(`${this.baseUrl}/a-risque`);
  }

  getObjectifsEcheanceProche(jours: number = 7): Observable<ObjectifIndividuel[]> {
    const params = new HttpParams().set('jours', jours.toString());
    return this.http.get<ObjectifIndividuel[]>(`${this.baseUrl}/echeance-proche`, { params });
  }

  // Recommandations et suggestions
  getRecommandationsJoueur(joueurId: number): Observable<{ type: string; description: string; priorite: string }[]> {
    return this.http.get<{ type: string; description: string; priorite: string }[]>(
      `${this.baseUrl}/recommandations/joueur/${joueurId}`
    );
  }

  getSuggestionsObjectifs(joueurId: number): Observable<{ type: TypeObjectif; titre: string; description: string }[]> {
    return this.http.get<{ type: TypeObjectif; titre: string; description: string }[]>(
      `${this.baseUrl}/suggestions/joueur/${joueurId}`
    );
  }

  // Templates d'objectifs
  getTemplatesObjectifs(): Observable<{ type: TypeObjectif; titre: string; description: string; dureeEstimee: number }[]> {
    return this.http.get<{ type: TypeObjectif; titre: string; description: string; dureeEstimee: number }[]>(
      `${this.baseUrl}/templates`
    );
  }

  creerObjectifDepuisTemplate(templateId: number, joueurId: number, personnalisation?: any): Observable<ObjectifIndividuel> {
    return this.http.post<ObjectifIndividuel>(`${this.baseUrl}/templates/${templateId}/creer`, {
      joueurId,
      personnalisation
    }).pipe(
      tap(() => this.refreshObjectifs())
    );
  }

  // Export et rapports
  exporterObjectifs(format: 'excel' | 'pdf', filters?: ObjectifFilters): Observable<Blob> {
    let params = new HttpParams().set('format', format);
    
    if (filters) {
      if (filters.joueurId) params = params.set('joueurId', filters.joueurId.toString());
      if (filters.type) params = params.set('type', filters.type);
      if (filters.statut) params = params.set('statut', filters.statut);
      if (filters.priorite) params = params.set('priorite', filters.priorite);
      if (filters.dateDebutMin) params = params.set('dateDebutMin', filters.dateDebutMin);
      if (filters.dateDebutMax) params = params.set('dateDebutMax', filters.dateDebutMax);
    }

    return this.http.get(`${this.baseUrl}/export`, {
      params,
      responseType: 'blob'
    });
  }

  genererRapportProgression(joueurId: number, periode?: { debut: string; fin: string }): Observable<Blob> {
    let params = new HttpParams().set('joueurId', joueurId.toString());
    
    if (periode) {
      params = params.set('dateDebut', periode.debut);
      params = params.set('dateFin', periode.fin);
    }

    return this.http.get(`${this.baseUrl}/rapport/progression`, {
      params,
      responseType: 'blob'
    });
  }

  // Validation et utilitaires
  validateObjectif(objectif: CreateObjectifDto | UpdateObjectifDto): string[] {
    const errors: string[] = [];

    if ('joueurId' in objectif) {
      if (!objectif.joueurId || objectif.joueurId <= 0) {
        errors.push('L\'ID du joueur est requis');
      }
    }

    if ('titre' in objectif && objectif.titre) {
      if (objectif.titre.length < 5) {
        errors.push('Le titre doit contenir au moins 5 caractères');
      }
      if (objectif.titre.length > 100) {
        errors.push('Le titre ne peut pas dépasser 100 caractères');
      }
    }

    if ('description' in objectif && objectif.description) {
      if (objectif.description.length > 1000) {
        errors.push('La description ne peut pas dépasser 1000 caractères');
      }
    }

    if ('dateDebut' in objectif && 'dateFin' in objectif && objectif.dateDebut && objectif.dateFin) {
      const dateDebut = new Date(objectif.dateDebut);
      const dateFin = new Date(objectif.dateFin);
      
      if (dateFin <= dateDebut) {
        errors.push('La date de fin doit être postérieure à la date de début');
      }
    }

    if ('progression' in objectif && objectif.progression !== undefined) {
      if (objectif.progression < 0 || objectif.progression > 100) {
        errors.push('La progression doit être comprise entre 0 et 100');
      }
    }

    return errors;
  }

  calculateProgression(objectif: ObjectifIndividuel): number {
    if (objectif.statut === StatutObjectif.ATTEINT) return 100;
    if (objectif.statut === StatutObjectif.ECHOUE) return objectif.progression || 0;
    
    // Calcul basé sur le temps écoulé et la progression manuelle
    const maintenant = new Date();
    const debut = new Date(objectif.dateDebut);
    const fin = new Date(objectif.dateFin);
    
    const dureeTotal = fin.getTime() - debut.getTime();
    const dureeEcoulee = maintenant.getTime() - debut.getTime();
    
    const progressionTemporelle = Math.min(100, Math.max(0, (dureeEcoulee / dureeTotal) * 100));
    
    // Moyenne entre progression temporelle et progression manuelle
    return Math.round((progressionTemporelle + (objectif.progression || 0)) / 2);
  }

  isObjectifEnRetard(objectif: ObjectifIndividuel): boolean {
    const maintenant = new Date();
    const fin = new Date(objectif.dateFin);
    const progressionAttendue = this.calculateProgression(objectif);
    
    return maintenant > fin && objectif.statut === StatutObjectif.EN_COURS && 
           (objectif.progression || 0) < progressionAttendue;
  }

  isObjectifARisque(objectif: ObjectifIndividuel): boolean {
    if (objectif.statut !== StatutObjectif.EN_COURS) return false;
    
    const maintenant = new Date();
    const debut = new Date(objectif.dateDebut);
    const fin = new Date(objectif.dateFin);
    
    const dureeTotal = fin.getTime() - debut.getTime();
    const dureeEcoulee = maintenant.getTime() - debut.getTime();
    const progressionTemporelle = (dureeEcoulee / dureeTotal) * 100;
    
    // Objectif à risque si la progression réelle est inférieure de 20% à la progression temporelle
    return (objectif.progression || 0) < (progressionTemporelle - 20);
  }

  getObjectifPriorityLevel(objectif: ObjectifIndividuel): 'urgent' | 'high' | 'medium' | 'low' {
    if (this.isObjectifEnRetard(objectif)) return 'urgent';
    if (objectif.priorite === PrioriteObjectif.HAUTE) return 'high';
    if (this.isObjectifARisque(objectif)) return 'high';
    if (objectif.priorite === PrioriteObjectif.MOYENNE) return 'medium';
    return 'low';
  }

  getJoursRestants(objectif: ObjectifIndividuel): number {
    const maintenant = new Date();
    const fin = new Date(objectif.dateFin);
    const diffTime = fin.getTime() - maintenant.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  getDureeObjectif(objectif: ObjectifIndividuel): number {
    const debut = new Date(objectif.dateDebut);
    const fin = new Date(objectif.dateFin);
    const diffTime = fin.getTime() - debut.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  groupObjectifsByType(objectifs: ObjectifIndividuel[]): { [type: string]: ObjectifIndividuel[] } {
    return objectifs.reduce((acc, objectif) => {
      if (!acc[objectif.type]) {
        acc[objectif.type] = [];
      }
      acc[objectif.type].push(objectif);
      return acc;
    }, {} as { [type: string]: ObjectifIndividuel[] });
  }

  groupObjectifsByStatut(objectifs: ObjectifIndividuel[]): { [statut: string]: ObjectifIndividuel[] } {
    return objectifs.reduce((acc, objectif) => {
      if (!acc[objectif.statut]) {
        acc[objectif.statut] = [];
      }
      acc[objectif.statut].push(objectif);
      return acc;
    }, {} as { [statut: string]: ObjectifIndividuel[] });
  }

  // Méthodes de cache
  private refreshObjectifs(): void {
    this.getAllObjectifs().subscribe();
  }

  clearCache(): void {
    this.objectifsSubject.next([]);
  }

  getCurrentObjectifs(): ObjectifIndividuel[] {
    return this.objectifsSubject.value;
  }

  // Méthodes utilitaires pour l'affichage
  formatDateObjectif(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  formatDateComplete(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  getTypeIcon(type: TypeObjectif): string {
    switch (type) {
      case TypeObjectif.TECHNIQUE: return 'sports_volleyball';
      case TypeObjectif.PHYSIQUE: return 'fitness_center';
      case TypeObjectif.TACTIQUE: return 'psychology';
      case TypeObjectif.MENTAL: return 'psychology';
      case TypeObjectif.PERSONNEL: return 'person';
      default: return 'flag';
    }
  }

  getTypeColor(type: TypeObjectif): string {
    switch (type) {
      case TypeObjectif.TECHNIQUE: return '#2196f3';
      case TypeObjectif.PHYSIQUE: return '#4caf50';
      case TypeObjectif.TACTIQUE: return '#ff9800';
      case TypeObjectif.MENTAL: return '#9c27b0';
      case TypeObjectif.PERSONNEL: return '#607d8b';
      default: return '#9e9e9e';
    }
  }

  getStatutIcon(statut: StatutObjectif): string {
    switch (statut) {
      case StatutObjectif.EN_COURS: return 'play_arrow';
      case StatutObjectif.ATTEINT: return 'check_circle';
      case StatutObjectif.ECHOUE: return 'cancel';
      case StatutObjectif.REPORTE: return 'schedule';
      default: return 'help_outline';
    }
  }

  getStatutColor(statut: StatutObjectif): string {
    switch (statut) {
      case StatutObjectif.EN_COURS: return '#2196f3';
      case StatutObjectif.ATTEINT: return '#4caf50';
      case StatutObjectif.ECHOUE: return '#f44336';
      case StatutObjectif.REPORTE: return '#ff9800';
      default: return '#9e9e9e';
    }
  }

  getPrioriteIcon(priorite: PrioriteObjectif): string {
    switch (priorite) {
      case PrioriteObjectif.HAUTE: return 'priority_high';
      case PrioriteObjectif.MOYENNE: return 'remove';
      case PrioriteObjectif.BASSE: return 'keyboard_arrow_down';
      default: return 'help_outline';
    }
  }

  getPrioriteColor(priorite: PrioriteObjectif): string {
    switch (priorite) {
      case PrioriteObjectif.HAUTE: return '#f44336';
      case PrioriteObjectif.MOYENNE: return '#ff9800';
      case PrioriteObjectif.BASSE: return '#4caf50';
      default: return '#9e9e9e';
    }
  }

  getProgressionColor(progression: number): string {
    if (progression >= 80) return '#4caf50';
    if (progression >= 60) return '#8bc34a';
    if (progression >= 40) return '#ff9800';
    if (progression >= 20) return '#ff5722';
    return '#f44336';
  }
}
