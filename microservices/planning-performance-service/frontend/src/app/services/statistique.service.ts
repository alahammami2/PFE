import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import {
  StatistiqueEntrainement,
  CreateStatistiqueDto,
  UpdateStatistiqueDto,
  StatistiqueFilters,
  StatistiqueGlobale,
  StatistiqueJoueur,
  StatistiqueEquipe,
  TendanceStatistique,
  ComparaisonStatistique
} from '../models/statistique.model';
import { BaseHttpService, PageResponse } from '../core/services/base-http.service';
import { ApiConfig } from '../core/config/api.config';

@Injectable({
  providedIn: 'root'
})
export class StatistiqueService {
  private statistiquesSubject = new BehaviorSubject<StatistiqueEntrainement[]>([]);
  public statistiques$ = this.statistiquesSubject.asObservable();

  constructor(
    private http: HttpClient,
    private baseHttpService: BaseHttpService
  ) {}

  // CRUD Operations
  getAllStatistiques(): Observable<StatistiqueEntrainement[]> {
    return this.baseHttpService.get<StatistiqueEntrainement[]>(ApiConfig.STATISTIQUES.BASE).pipe(
      tap(statistiques => this.statistiquesSubject.next(statistiques)),
      catchError(error => {
        console.error('Erreur lors du chargement des statistiques:', error);
        throw error;
      })
    );
  }

  getStatistiqueById(id: number): Observable<StatistiqueEntrainement> {
    return this.http.get<StatistiqueEntrainement>(`${this.baseUrl}/${id}`);
  }

  createStatistique(statistique: CreateStatistiqueDto): Observable<StatistiqueEntrainement> {
    return this.http.post<StatistiqueEntrainement>(this.baseUrl, statistique).pipe(
      tap(() => this.refreshStatistiques())
    );
  }

  updateStatistique(id: number, statistique: UpdateStatistiqueDto): Observable<StatistiqueEntrainement> {
    return this.http.put<StatistiqueEntrainement>(`${this.baseUrl}/${id}`, statistique).pipe(
      tap(() => this.refreshStatistiques())
    );
  }

  deleteStatistique(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => this.refreshStatistiques())
    );
  }

  // Recherche et filtrage
  searchStatistiques(filters: StatistiqueFilters): Observable<StatistiqueEntrainement[]> {
    let params = new HttpParams();
    
    if (filters.entrainementId) {
      params = params.set('entrainementId', filters.entrainementId.toString());
    }
    if (filters.joueurId) {
      params = params.set('joueurId', filters.joueurId.toString());
    }
    if (filters.dateDebut) {
      params = params.set('dateDebut', filters.dateDebut);
    }
    if (filters.dateFin) {
      params = params.set('dateFin', filters.dateFin);
    }
    if (filters.dureeMin !== undefined) {
      params = params.set('dureeMin', filters.dureeMin.toString());
    }
    if (filters.dureeMax !== undefined) {
      params = params.set('dureeMax', filters.dureeMax.toString());
    }
    if (filters.intensiteMin !== undefined) {
      params = params.set('intensiteMin', filters.intensiteMin.toString());
    }
    if (filters.intensiteMax !== undefined) {
      params = params.set('intensiteMax', filters.intensiteMax.toString());
    }

    return this.http.get<StatistiqueEntrainement[]>(`${this.baseUrl}/search`, { params });
  }

  getStatistiquesByEntrainement(entrainementId: number): Observable<StatistiqueEntrainement[]> {
    return this.http.get<StatistiqueEntrainement[]>(`${this.baseUrl}/entrainement/${entrainementId}`);
  }

  getStatistiquesByJoueur(joueurId: number): Observable<StatistiqueEntrainement[]> {
    return this.http.get<StatistiqueEntrainement[]>(`${this.baseUrl}/joueur/${joueurId}`);
  }

  // Statistiques globales
  getStatistiquesGlobales(): Observable<StatistiqueGlobale> {
    return this.http.get<StatistiqueGlobale>(`${this.baseUrl}/globales`);
  }

  getStatistiquesGlobalesPeriode(dateDebut: string, dateFin: string): Observable<StatistiqueGlobale> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    
    return this.http.get<StatistiqueGlobale>(`${this.baseUrl}/globales/periode`, { params });
  }

  // Statistiques par joueur
  getStatistiquesJoueur(joueurId: number): Observable<StatistiqueJoueur> {
    return this.http.get<StatistiqueJoueur>(`${this.baseUrl}/joueur/${joueurId}/resume`);
  }

  getStatistiquesJoueurPeriode(joueurId: number, dateDebut: string, dateFin: string): Observable<StatistiqueJoueur> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    
    return this.http.get<StatistiqueJoueur>(`${this.baseUrl}/joueur/${joueurId}/periode`, { params });
  }

  getComparaisonJoueurs(joueurIds: number[], dateDebut?: string, dateFin?: string): Observable<ComparaisonStatistique[]> {
    let params = new HttpParams();
    joueurIds.forEach(id => params = params.append('joueurIds', id.toString()));
    
    if (dateDebut) params = params.set('dateDebut', dateDebut);
    if (dateFin) params = params.set('dateFin', dateFin);
    
    return this.http.get<ComparaisonStatistique[]>(`${this.baseUrl}/comparaison/joueurs`, { params });
  }

  // Statistiques d'équipe
  getStatistiquesEquipe(): Observable<StatistiqueEquipe> {
    return this.http.get<StatistiqueEquipe>(`${this.baseUrl}/equipe`);
  }

  getStatistiquesEquipePeriode(dateDebut: string, dateFin: string): Observable<StatistiqueEquipe> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    
    return this.http.get<StatistiqueEquipe>(`${this.baseUrl}/equipe/periode`, { params });
  }

  // Tendances et évolutions
  getTendanceJoueur(joueurId: number, metrique: string, periode: 'semaine' | 'mois' | 'trimestre'): Observable<TendanceStatistique[]> {
    const params = new HttpParams()
      .set('metrique', metrique)
      .set('periode', periode);
    
    return this.http.get<TendanceStatistique[]>(`${this.baseUrl}/tendance/joueur/${joueurId}`, { params });
  }

  getTendanceEquipe(metrique: string, periode: 'semaine' | 'mois' | 'trimestre'): Observable<TendanceStatistique[]> {
    const params = new HttpParams()
      .set('metrique', metrique)
      .set('periode', periode);
    
    return this.http.get<TendanceStatistique[]>(`${this.baseUrl}/tendance/equipe`, { params });
  }

  getEvolutionPerformance(joueurId: number): Observable<{ date: string; performance: number }[]> {
    return this.http.get<{ date: string; performance: number }[]>(`${this.baseUrl}/evolution/performance/${joueurId}`);
  }

  // Analyses avancées
  getAnalyseIntensiteEntrainement(entrainementId: number): Observable<{
    intensiteMoyenne: number;
    intensiteMax: number;
    intensiteMin: number;
    repartitionIntensites: { [niveau: string]: number };
    recommandations: string[];
  }> {
    return this.http.get<any>(`${this.baseUrl}/analyse/intensite/${entrainementId}`);
  }

  getAnalyseChargeEntrainement(joueurId: number, periode: number): Observable<{
    chargeTotale: number;
    chargeMoyenne: number;
    tendance: 'croissante' | 'decroissante' | 'stable';
    risqueSurmenage: 'faible' | 'moyen' | 'eleve';
    recommandations: string[];
  }> {
    const params = new HttpParams().set('periode', periode.toString());
    return this.http.get<any>(`${this.baseUrl}/analyse/charge/${joueurId}`, { params });
  }

  getAnalyseProgressionJoueur(joueurId: number): Observable<{
    progressionGlobale: number;
    domainesAmelioration: string[];
    domainesRegression: string[];
    objectifsRecommandes: string[];
    prochainePlanification: string[];
  }> {
    return this.http.get<any>(`${this.baseUrl}/analyse/progression/${joueurId}`);
  }

  // Rapports et exports
  genererRapportJoueur(joueurId: number, dateDebut: string, dateFin: string): Observable<Blob> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    
    return this.http.get(`${this.baseUrl}/rapport/joueur/${joueurId}`, {
      params,
      responseType: 'blob'
    });
  }

  genererRapportEquipe(dateDebut: string, dateFin: string): Observable<Blob> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    
    return this.http.get(`${this.baseUrl}/rapport/equipe`, {
      params,
      responseType: 'blob'
    });
  }

  exporterStatistiques(format: 'excel' | 'csv' | 'pdf', filters?: StatistiqueFilters): Observable<Blob> {
    let params = new HttpParams().set('format', format);
    
    if (filters) {
      if (filters.entrainementId) params = params.set('entrainementId', filters.entrainementId.toString());
      if (filters.joueurId) params = params.set('joueurId', filters.joueurId.toString());
      if (filters.dateDebut) params = params.set('dateDebut', filters.dateDebut);
      if (filters.dateFin) params = params.set('dateFin', filters.dateFin);
    }

    return this.http.get(`${this.baseUrl}/export`, {
      params,
      responseType: 'blob'
    });
  }

  // Calculs et analyses côté client
  calculateMoyenneIntensiteJoueur(statistiques: StatistiqueEntrainement[]): number {
    if (statistiques.length === 0) return 0;
    const total = statistiques.reduce((sum, stat) => sum + (stat.intensiteMoyenne || 0), 0);
    return Math.round((total / statistiques.length) * 10) / 10;
  }

  calculateDureeTotaleEntrainement(statistiques: StatistiqueEntrainement[]): number {
    return statistiques.reduce((sum, stat) => sum + (stat.dureeEffective || 0), 0);
  }

  calculateChargeEntrainementJoueur(statistiques: StatistiqueEntrainement[]): number {
    return statistiques.reduce((sum, stat) => {
      const duree = stat.dureeEffective || 0;
      const intensite = stat.intensiteMoyenne || 0;
      return sum + (duree * intensite);
    }, 0);
  }

  calculateTendanceProgression(statistiques: StatistiqueEntrainement[]): 'croissante' | 'decroissante' | 'stable' {
    if (statistiques.length < 2) return 'stable';
    
    const sortedStats = statistiques.sort((a, b) => 
      new Date(a.dateEntrainement || '').getTime() - new Date(b.dateEntrainement || '').getTime()
    );
    
    const premieresMoitie = sortedStats.slice(0, Math.floor(sortedStats.length / 2));
    const derniereMoitie = sortedStats.slice(Math.floor(sortedStats.length / 2));
    
    const moyennePremiere = this.calculateMoyenneIntensiteJoueur(premieresMoitie);
    const moyenneDerniere = this.calculateMoyenneIntensiteJoueur(derniereMoitie);
    
    const difference = moyenneDerniere - moyennePremiere;
    
    if (Math.abs(difference) < 0.5) return 'stable';
    return difference > 0 ? 'croissante' : 'decroissante';
  }

  detecterAnomalies(statistiques: StatistiqueEntrainement[]): {
    type: 'intensite_anormale' | 'duree_anormale' | 'absence_donnees';
    description: string;
    valeur: number;
    seuil: number;
  }[] {
    const anomalies: any[] = [];
    
    if (statistiques.length === 0) {
      anomalies.push({
        type: 'absence_donnees',
        description: 'Aucune donnée statistique disponible',
        valeur: 0,
        seuil: 1
      });
      return anomalies;
    }
    
    const intensiteMoyenne = this.calculateMoyenneIntensiteJoueur(statistiques);
    const dureesMoyenne = statistiques.reduce((sum, stat) => sum + (stat.dureeEffective || 0), 0) / statistiques.length;
    
    statistiques.forEach(stat => {
      // Détection d'intensité anormale (écart > 30% de la moyenne)
      if (stat.intensiteMoyenne && Math.abs(stat.intensiteMoyenne - intensiteMoyenne) > intensiteMoyenne * 0.3) {
        anomalies.push({
          type: 'intensite_anormale',
          description: `Intensité anormalement ${stat.intensiteMoyenne > intensiteMoyenne ? 'élevée' : 'faible'}`,
          valeur: stat.intensiteMoyenne,
          seuil: intensiteMoyenne
        });
      }
      
      // Détection de durée anormale (écart > 50% de la moyenne)
      if (stat.dureeEffective && Math.abs(stat.dureeEffective - dureesMoyenne) > dureesMoyenne * 0.5) {
        anomalies.push({
          type: 'duree_anormale',
          description: `Durée anormalement ${stat.dureeEffective > dureesMoyenne ? 'longue' : 'courte'}`,
          valeur: stat.dureeEffective,
          seuil: dureesMoyenne
        });
      }
    });
    
    return anomalies;
  }

  genererRecommandations(statistiques: StatistiqueEntrainement[]): string[] {
    const recommandations: string[] = [];
    
    if (statistiques.length === 0) {
      recommandations.push('Commencer à enregistrer des statistiques d\'entraînement');
      return recommandations;
    }
    
    const intensiteMoyenne = this.calculateMoyenneIntensiteJoueur(statistiques);
    const tendance = this.calculateTendanceProgression(statistiques);
    const anomalies = this.detecterAnomalies(statistiques);
    
    // Recommandations basées sur l'intensité
    if (intensiteMoyenne < 5) {
      recommandations.push('Augmenter l\'intensité des entraînements pour améliorer les performances');
    } else if (intensiteMoyenne > 8) {
      recommandations.push('Attention au risque de surmenage - prévoir des périodes de récupération');
    }
    
    // Recommandations basées sur la tendance
    if (tendance === 'decroissante') {
      recommandations.push('La performance semble décliner - analyser les causes et ajuster le programme');
    } else if (tendance === 'stable') {
      recommandations.push('Introduire de la variété dans les entraînements pour stimuler la progression');
    }
    
    // Recommandations basées sur les anomalies
    if (anomalies.length > 0) {
      recommandations.push('Des anomalies ont été détectées - vérifier la cohérence des données');
    }
    
    return recommandations;
  }

  // Méthodes utilitaires
  groupStatistiquesByMonth(statistiques: StatistiqueEntrainement[]): { [month: string]: StatistiqueEntrainement[] } {
    return statistiques.reduce((acc, stat) => {
      if (stat.dateEntrainement) {
        const month = new Date(stat.dateEntrainement).toISOString().slice(0, 7); // YYYY-MM
        if (!acc[month]) {
          acc[month] = [];
        }
        acc[month].push(stat);
      }
      return acc;
    }, {} as { [month: string]: StatistiqueEntrainement[] });
  }

  groupStatistiquesByJoueur(statistiques: StatistiqueEntrainement[]): { [joueurId: string]: StatistiqueEntrainement[] } {
    return statistiques.reduce((acc, stat) => {
      const joueurId = stat.joueurId?.toString() || 'unknown';
      if (!acc[joueurId]) {
        acc[joueurId] = [];
      }
      acc[joueurId].push(stat);
      return acc;
    }, {} as { [joueurId: string]: StatistiqueEntrainement[] });
  }

  calculatePerformanceScore(statistique: StatistiqueEntrainement): number {
    // Score basé sur l'intensité, la durée et d'autres métriques
    const intensiteScore = (statistique.intensiteMoyenne || 0) * 10;
    const dureeScore = Math.min((statistique.dureeEffective || 0) / 120, 1) * 20; // Max 2h = 20 points
    const caloriesScore = Math.min((statistique.caloriesBrulees || 0) / 500, 1) * 10; // Max 500 cal = 10 points
    
    return Math.round(intensiteScore + dureeScore + caloriesScore);
  }

  formatDuree(minutes: number): string {
    const heures = Math.floor(minutes / 60);
    const mins = minutes % 60;
    
    if (heures > 0) {
      return `${heures}h${mins.toString().padStart(2, '0')}`;
    }
    return `${mins}min`;
  }

  formatIntensiteLabel(intensite: number): string {
    if (intensite >= 8) return 'Très élevée';
    if (intensite >= 6) return 'Élevée';
    if (intensite >= 4) return 'Modérée';
    if (intensite >= 2) return 'Faible';
    return 'Très faible';
  }

  getIntensiteColor(intensite: number): string {
    if (intensite >= 8) return '#f44336';
    if (intensite >= 6) return '#ff9800';
    if (intensite >= 4) return '#ffeb3b';
    if (intensite >= 2) return '#8bc34a';
    return '#4caf50';
  }

  // Méthodes de cache
  private refreshStatistiques(): void {
    this.getAllStatistiques().subscribe();
  }

  clearCache(): void {
    this.statistiquesSubject.next([]);
  }

  getCurrentStatistiques(): StatistiqueEntrainement[] {
    return this.statistiquesSubject.value;
  }

  // Validation
  validateStatistique(statistique: CreateStatistiqueDto | UpdateStatistiqueDto): string[] {
    const errors: string[] = [];

    if ('entrainementId' in statistique) {
      if (!statistique.entrainementId || statistique.entrainementId <= 0) {
        errors.push('L\'ID de l\'entraînement est requis');
      }
    }

    if ('joueurId' in statistique) {
      if (!statistique.joueurId || statistique.joueurId <= 0) {
        errors.push('L\'ID du joueur est requis');
      }
    }

    if ('dureeEffective' in statistique && statistique.dureeEffective !== undefined) {
      if (statistique.dureeEffective < 0 || statistique.dureeEffective > 480) { // Max 8h
        errors.push('La durée doit être comprise entre 0 et 480 minutes');
      }
    }

    if ('intensiteMoyenne' in statistique && statistique.intensiteMoyenne !== undefined) {
      if (statistique.intensiteMoyenne < 0 || statistique.intensiteMoyenne > 10) {
        errors.push('L\'intensité doit être comprise entre 0 et 10');
      }
    }

    if ('caloriesBrulees' in statistique && statistique.caloriesBrulees !== undefined) {
      if (statistique.caloriesBrulees < 0 || statistique.caloriesBrulees > 2000) {
        errors.push('Les calories brûlées doivent être comprises entre 0 et 2000');
      }
    }

    return errors;
  }
}
