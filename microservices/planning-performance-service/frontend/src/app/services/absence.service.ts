import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import {
  Absence,
  CreateAbsenceDto,
  UpdateAbsenceDto,
  AbsenceFilters,
  AbsenceStats,
  TypeAbsence,
  StatutAbsence
} from '../models/absence.model';
import { BaseHttpService, PageResponse } from '../core/services/base-http.service';
import { ApiConfig } from '../core/config/api.config';

@Injectable({
  providedIn: 'root'
})
export class AbsenceService {
  private absencesSubject = new BehaviorSubject<Absence[]>([]);
  public absences$ = this.absencesSubject.asObservable();

  constructor(
    private http: HttpClient,
    private baseHttpService: BaseHttpService
  ) {}

  // CRUD Operations
  getAllAbsences(): Observable<Absence[]> {
    return this.baseHttpService.get<Absence[]>(ApiConfig.ABSENCES.BASE).pipe(
      tap(absences => this.absencesSubject.next(absences)),
      catchError(error => {
        console.error('Erreur lors du chargement des absences:', error);
        throw error;
      })
    );
  }

  getAbsenceById(id: number): Observable<Absence> {
    return this.baseHttpService.get<Absence>(ApiConfig.ABSENCES.BY_ID(id)).pipe(
      catchError(error => {
        console.error(`Erreur lors du chargement de l'absence ${id}:`, error);
        throw error;
      })
    );
  }

  createAbsence(absence: CreateAbsenceDto): Observable<Absence> {
    return this.http.post<Absence>(this.baseUrl, absence).pipe(
      tap(() => this.refreshAbsences())
    );
  }

  updateAbsence(id: number, absence: UpdateAbsenceDto): Observable<Absence> {
    return this.http.put<Absence>(`${this.baseUrl}/${id}`, absence).pipe(
      tap(() => this.refreshAbsences())
    );
  }

  deleteAbsence(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => this.refreshAbsences())
    );
  }

  // Recherche et filtrage
  searchAbsences(filters: AbsenceFilters): Observable<Absence[]> {
    let params = new HttpParams();
    
    if (filters.joueurId) {
      params = params.set('joueurId', filters.joueurId.toString());
    }
    if (filters.entrainementId) {
      params = params.set('entrainementId', filters.entrainementId.toString());
    }
    if (filters.type) {
      params = params.set('type', filters.type);
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
    if (filters.justifiee !== undefined) {
      params = params.set('justifiee', filters.justifiee.toString());
    }

    return this.http.get<Absence[]>(`${this.baseUrl}/search`, { params });
  }

  getAbsencesByJoueur(joueurId: number): Observable<Absence[]> {
    return this.http.get<Absence[]>(`${this.baseUrl}/joueur/${joueurId}`);
  }

  getAbsencesByEntrainement(entrainementId: number): Observable<Absence[]> {
    return this.http.get<Absence[]>(`${this.baseUrl}/entrainement/${entrainementId}`);
  }

  getAbsencesByType(type: TypeAbsence): Observable<Absence[]> {
    return this.http.get<Absence[]>(`${this.baseUrl}/type/${type}`);
  }

  getAbsencesByStatut(statut: StatutAbsence): Observable<Absence[]> {
    return this.http.get<Absence[]>(`${this.baseUrl}/statut/${statut}`);
  }

  // Gestion des statuts
  approuverAbsence(id: number, commentaireApprobation?: string): Observable<Absence> {
    return this.http.patch<Absence>(`${this.baseUrl}/${id}/approuver`, {
      commentaireApprobation
    }).pipe(
      tap(() => this.refreshAbsences())
    );
  }

  rejeterAbsence(id: number, commentaireRejet: string): Observable<Absence> {
    return this.http.patch<Absence>(`${this.baseUrl}/${id}/rejeter`, {
      commentaireRejet
    }).pipe(
      tap(() => this.refreshAbsences())
    );
  }

  marquerAbsenceTraitee(id: number): Observable<Absence> {
    return this.http.patch<Absence>(`${this.baseUrl}/${id}/traiter`, {}).pipe(
      tap(() => this.refreshAbsences())
    );
  }

  // Statistiques
  getStatistiques(): Observable<AbsenceStats> {
    return this.http.get<AbsenceStats>(`${this.baseUrl}/statistiques`);
  }

  getStatistiquesJoueur(joueurId: number): Observable<AbsenceStats> {
    return this.http.get<AbsenceStats>(`${this.baseUrl}/statistiques/joueur/${joueurId}`);
  }

  getStatistiquesPeriode(dateDebut: string, dateFin: string): Observable<AbsenceStats> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    
    return this.http.get<AbsenceStats>(`${this.baseUrl}/statistiques/periode`, { params });
  }

  getTauxAbsenteisme(): Observable<{ [joueurId: string]: number }> {
    return this.http.get<{ [joueurId: string]: number }>(`${this.baseUrl}/taux-absenteisme`);
  }

  getTendanceAbsences(): Observable<{ date: string; nombreAbsences: number }[]> {
    return this.http.get<{ date: string; nombreAbsences: number }[]>(`${this.baseUrl}/tendance`);
  }

  // Notifications et alertes
  getAbsencesEnAttente(): Observable<Absence[]> {
    return this.http.get<Absence[]>(`${this.baseUrl}/en-attente`);
  }

  getAbsencesRecurrentes(joueurId?: number): Observable<{ joueurId: number; nombreAbsences: number; periode: string }[]> {
    let params = new HttpParams();
    if (joueurId) {
      params = params.set('joueurId', joueurId.toString());
    }
    
    return this.http.get<{ joueurId: number; nombreAbsences: number; periode: string }[]>(
      `${this.baseUrl}/recurrentes`, { params }
    );
  }

  getAlertesAbsenteisme(): Observable<{ joueurId: number; tauxAbsenteisme: number; seuil: number }[]> {
    return this.http.get<{ joueurId: number; tauxAbsenteisme: number; seuil: number }[]>(
      `${this.baseUrl}/alertes/absenteisme`
    );
  }

  // Justificatifs
  uploadJustificatif(absenceId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post(`${this.baseUrl}/${absenceId}/justificatif`, formData).pipe(
      tap(() => this.refreshAbsences())
    );
  }

  downloadJustificatif(absenceId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${absenceId}/justificatif`, {
      responseType: 'blob'
    });
  }

  deleteJustificatif(absenceId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${absenceId}/justificatif`).pipe(
      tap(() => this.refreshAbsences())
    );
  }

  // Export
  exporterAbsences(format: 'excel' | 'pdf', filters?: AbsenceFilters): Observable<Blob> {
    let params = new HttpParams().set('format', format);
    
    if (filters) {
      if (filters.joueurId) params = params.set('joueurId', filters.joueurId.toString());
      if (filters.entrainementId) params = params.set('entrainementId', filters.entrainementId.toString());
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

  // Déclaration d'absence préventive
  declarerAbsencePreventive(absence: CreateAbsenceDto): Observable<Absence> {
    return this.http.post<Absence>(`${this.baseUrl}/preventive`, absence).pipe(
      tap(() => this.refreshAbsences())
    );
  }

  // Absence de dernière minute
  declarerAbsenceDerniereMinute(entrainementId: number, joueurId: number, motif: string): Observable<Absence> {
    return this.http.post<Absence>(`${this.baseUrl}/derniere-minute`, {
      entrainementId,
      joueurId,
      motif,
      type: TypeAbsence.DERNIERE_MINUTE
    }).pipe(
      tap(() => this.refreshAbsences())
    );
  }

  // Validation et utilitaires
  validateAbsence(absence: CreateAbsenceDto | UpdateAbsenceDto): string[] {
    const errors: string[] = [];

    if ('entrainementId' in absence) {
      if (!absence.entrainementId || absence.entrainementId <= 0) {
        errors.push('L\'ID de l\'entraînement est requis');
      }
    }

    if ('joueurId' in absence) {
      if (!absence.joueurId || absence.joueurId <= 0) {
        errors.push('L\'ID du joueur est requis');
      }
    }

    if ('type' in absence && absence.type) {
      if (!Object.values(TypeAbsence).includes(absence.type)) {
        errors.push('Type d\'absence invalide');
      }
    }

    if (absence.motif && absence.motif.length > 500) {
      errors.push('Le motif ne peut pas dépasser 500 caractères');
    }

    return errors;
  }

  calculateTauxAbsenteisme(absences: Absence[], totalEntrainements: number): number {
    if (totalEntrainements === 0) return 0;
    return Math.round((absences.length / totalEntrainements) * 100);
  }

  groupAbsencesByType(absences: Absence[]): { [type: string]: Absence[] } {
    return absences.reduce((acc, absence) => {
      if (!acc[absence.type]) {
        acc[absence.type] = [];
      }
      acc[absence.type].push(absence);
      return acc;
    }, {} as { [type: string]: Absence[] });
  }

  groupAbsencesByStatut(absences: Absence[]): { [statut: string]: Absence[] } {
    return absences.reduce((acc, absence) => {
      if (!acc[absence.statut]) {
        acc[absence.statut] = [];
      }
      acc[absence.statut].push(absence);
      return acc;
    }, {} as { [statut: string]: Absence[] });
  }

  getAbsencesByMonth(absences: Absence[]): { [month: string]: number } {
    return absences.reduce((acc, absence) => {
      const month = new Date(absence.dateAbsence).toISOString().slice(0, 7); // YYYY-MM
      acc[month] = (acc[month] || 0) + 1;
      return acc;
    }, {} as { [month: string]: number });
  }

  isAbsenceJustifiable(absence: Absence): boolean {
    return absence.type === TypeAbsence.MALADIE || 
           absence.type === TypeAbsence.BLESSURE || 
           absence.type === TypeAbsence.URGENCE_FAMILIALE;
  }

  isAbsenceModifiable(absence: Absence): boolean {
    return absence.statut === StatutAbsence.EN_ATTENTE;
  }

  canApproveAbsence(absence: Absence): boolean {
    return absence.statut === StatutAbsence.EN_ATTENTE;
  }

  getAbsencePriorityLevel(absence: Absence): 'high' | 'medium' | 'low' {
    if (absence.type === TypeAbsence.DERNIERE_MINUTE) return 'high';
    if (absence.type === TypeAbsence.MALADIE || absence.type === TypeAbsence.BLESSURE) return 'medium';
    return 'low';
  }

  // Méthodes de cache
  private refreshAbsences(): void {
    this.getAllAbsences().subscribe();
  }

  clearCache(): void {
    this.absencesSubject.next([]);
  }

  getCurrentAbsences(): Absence[] {
    return this.absencesSubject.value;
  }

  // Méthodes utilitaires pour l'affichage
  formatDateAbsence(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  formatDateDeclaration(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getTypeIcon(type: TypeAbsence): string {
    switch (type) {
      case TypeAbsence.MALADIE: return 'sick';
      case TypeAbsence.BLESSURE: return 'healing';
      case TypeAbsence.VACANCES: return 'beach_access';
      case TypeAbsence.TRAVAIL: return 'work';
      case TypeAbsence.URGENCE_FAMILIALE: return 'family_restroom';
      case TypeAbsence.DERNIERE_MINUTE: return 'schedule';
      case TypeAbsence.AUTRE: return 'help_outline';
      default: return 'event_busy';
    }
  }

  getTypeColor(type: TypeAbsence): string {
    switch (type) {
      case TypeAbsence.MALADIE: return '#f44336';
      case TypeAbsence.BLESSURE: return '#ff5722';
      case TypeAbsence.VACANCES: return '#4caf50';
      case TypeAbsence.TRAVAIL: return '#2196f3';
      case TypeAbsence.URGENCE_FAMILIALE: return '#ff9800';
      case TypeAbsence.DERNIERE_MINUTE: return '#9c27b0';
      case TypeAbsence.AUTRE: return '#607d8b';
      default: return '#9e9e9e';
    }
  }

  getStatutIcon(statut: StatutAbsence): string {
    switch (statut) {
      case StatutAbsence.EN_ATTENTE: return 'schedule';
      case StatutAbsence.APPROUVEE: return 'check_circle';
      case StatutAbsence.REJETEE: return 'cancel';
      case StatutAbsence.TRAITEE: return 'done_all';
      default: return 'help_outline';
    }
  }

  getStatutColor(statut: StatutAbsence): string {
    switch (statut) {
      case StatutAbsence.EN_ATTENTE: return '#ff9800';
      case StatutAbsence.APPROUVEE: return '#4caf50';
      case StatutAbsence.REJETEE: return '#f44336';
      case StatutAbsence.TRAITEE: return '#2196f3';
      default: return '#9e9e9e';
    }
  }
}
