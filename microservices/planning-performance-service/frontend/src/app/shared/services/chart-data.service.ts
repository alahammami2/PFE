import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { ChartData, ChartOptions } from '../components/chart/chart.component';
import { StatistiqueService } from '../../services/statistique.service';
import { PerformanceService } from '../../services/performance.service';
import { ParticipationService } from '../../services/participation.service';
import { AbsenceService } from '../../services/absence.service';

@Injectable({
  providedIn: 'root'
})
export class ChartDataService {

  constructor(
    private statistiqueService: StatistiqueService,
    private performanceService: PerformanceService,
    private participationService: ParticipationService,
    private absenceService: AbsenceService
  ) {}

  // Couleurs prédéfinies pour les graphiques
  private readonly colors = {
    primary: '#007bff',
    success: '#28a745',
    warning: '#ffc107',
    danger: '#dc3545',
    info: '#17a2b8',
    secondary: '#6c757d',
    light: '#f8f9fa',
    dark: '#343a40'
  };

  private readonly gradientColors = [
    'rgba(0, 123, 255, 0.8)',
    'rgba(40, 167, 69, 0.8)',
    'rgba(255, 193, 7, 0.8)',
    'rgba(220, 53, 69, 0.8)',
    'rgba(23, 162, 184, 0.8)',
    'rgba(108, 117, 125, 0.8)'
  ];

  constructor(
    private statistiqueService: StatistiqueService,
    private performanceService: PerformanceService,
    private participationService: ParticipationService,
    private absenceService: AbsenceService
  ) { }

  /**
   * Génère les données pour un graphique d'évolution des performances
   */
  generatePerformanceEvolutionData(performances: any[]): ChartData {
    const labels = performances.map(p => new Date(p.date).toLocaleDateString('fr-FR'));
    
    return {
      labels,
      datasets: [
        {
          label: 'Technique',
          data: performances.map(p => p.technique || 0),
          borderColor: this.colors.primary,
          backgroundColor: 'rgba(0, 123, 255, 0.1)',
          fill: true,
          tension: 0.4
        },
        {
          label: 'Physique',
          data: performances.map(p => p.physique || 0),
          borderColor: this.colors.success,
          backgroundColor: 'rgba(40, 167, 69, 0.1)',
          fill: true,
          tension: 0.4
        },
        {
          label: 'Tactique',
          data: performances.map(p => p.tactique || 0),
          borderColor: this.colors.warning,
          backgroundColor: 'rgba(255, 193, 7, 0.1)',
          fill: true,
          tension: 0.4
        },
        {
          label: 'Mental',
          data: performances.map(p => p.mental || 0),
          borderColor: this.colors.info,
          backgroundColor: 'rgba(23, 162, 184, 0.1)',
          fill: true,
          tension: 0.4
        }
      ]
    };
  }

  /**
   * Génère les données pour un graphique radar des performances
   */
  generatePerformanceRadarData(performance: any): ChartData {
    return {
      labels: ['Technique', 'Physique', 'Tactique', 'Mental'],
      datasets: [
        {
          label: 'Performance actuelle',
          data: [
            performance.technique || 0,
            performance.physique || 0,
            performance.tactique || 0,
            performance.mental || 0
          ],
          backgroundColor: 'rgba(0, 123, 255, 0.2)',
          borderColor: this.colors.primary,
          borderWidth: 2
        }
      ]
    };
  }

  /**
   * Génère les données pour un graphique de présence mensuelle
   */
  generatePresenceData(presences: any[]): ChartData {
    const labels = presences.map(p => p.mois);
    
    return {
      labels,
      datasets: [
        {
          label: 'Taux de présence (%)',
          data: presences.map(p => p.tauxPresence),
          backgroundColor: this.gradientColors[0],
          borderColor: this.colors.primary,
          borderWidth: 2
        }
      ]
    };
  }

  /**
   * Génère les données pour un graphique de répartition des absences
   */
  generateAbsenceDistributionData(absences: any[]): ChartData {
    const types = ['Maladie', 'Blessure', 'Urgence familiale', 'Obligations prof.', 'Congés', 'Autre'];
    const data = types.map(type => 
      absences.filter(a => a.type === type.toUpperCase().replace(/[^A-Z]/g, '_')).length
    );

    return {
      labels: types,
      datasets: [
        {
          label: 'Nombre d\'absences',
          data,
          backgroundColor: [
            'rgba(220, 53, 69, 0.8)',
            'rgba(255, 193, 7, 0.8)',
            'rgba(23, 162, 184, 0.8)',
            'rgba(108, 117, 125, 0.8)',
            'rgba(40, 167, 69, 0.8)',
            'rgba(0, 123, 255, 0.8)'
          ],
          borderWidth: 1
        }
      ]
    };
  }

  /**
   * Génère les données pour un graphique de progression des objectifs
   */
  generateObjectifsProgressData(objectifs: any[]): ChartData {
    const labels = objectifs.map(o => o.titre.substring(0, 20) + '...');
    
    return {
      labels,
      datasets: [
        {
          label: 'Progression (%)',
          data: objectifs.map(o => o.progression),
          backgroundColor: objectifs.map(o => {
            if (o.progression >= 80) return 'rgba(40, 167, 69, 0.8)';
            if (o.progression >= 60) return 'rgba(0, 123, 255, 0.8)';
            if (o.progression >= 40) return 'rgba(255, 193, 7, 0.8)';
            return 'rgba(220, 53, 69, 0.8)';
          }),
          borderWidth: 1
        }
      ]
    };
  }

  /**
   * Génère les données pour un graphique de statistiques d'équipe
   */
  generateTeamStatsData(stats: any): ChartData {
    return {
      labels: ['Technique', 'Physique', 'Tactique', 'Mental'],
      datasets: [
        {
          label: 'Moyenne équipe',
          data: [
            stats.technique?.moyenne || 0,
            stats.physique?.moyenne || 0,
            stats.tactique?.moyenne || 0,
            stats.mental?.moyenne || 0
          ],
          backgroundColor: 'rgba(0, 123, 255, 0.6)',
          borderColor: this.colors.primary,
          borderWidth: 2
        },
        {
          label: 'Meilleur joueur',
          data: [
            stats.technique?.max || 0,
            stats.physique?.max || 0,
            stats.tactique?.max || 0,
            stats.mental?.max || 0
          ],
          backgroundColor: 'rgba(40, 167, 69, 0.6)',
          borderColor: this.colors.success,
          borderWidth: 2
        }
      ]
    };
  }

  /**
   * Génère les données pour un graphique de charge d'entraînement
   */
  generateChargeEntrainementData(charges: any[]): ChartData {
    const labels = charges.map(c => new Date(c.date).toLocaleDateString('fr-FR'));
    
    return {
      labels,
      datasets: [
        {
          label: 'Charge d\'entraînement',
          data: charges.map(c => c.charge),
          backgroundColor: 'rgba(255, 193, 7, 0.6)',
          borderColor: this.colors.warning,
          borderWidth: 2,
          fill: true,
          tension: 0.4
        },
        {
          label: 'Seuil recommandé',
          data: charges.map(() => 75), // Seuil fixe à 75%
          borderColor: this.colors.danger,
          borderWidth: 2,
          borderDash: [5, 5],
          fill: false
        }
      ]
    };
  }

  /**
   * Génère les options par défaut pour les graphiques de performance
   */
  getPerformanceChartOptions(): ChartOptions {
    return {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'top'
        },
        title: {
          display: true,
          text: 'Évolution des performances'
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          max: 5,
          title: {
            display: true,
            text: 'Note (sur 5)'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Période'
          }
        }
      }
    };
  }

  /**
   * Génère les options pour les graphiques radar
   */
  getRadarChartOptions(): ChartOptions {
    return {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'top'
        }
      },
      scales: {
        r: {
          beginAtZero: true,
          max: 5,
          ticks: {
            stepSize: 1
          }
        }
      }
    };
  }

  /**
   * Génère les options pour les graphiques en secteurs
   */
  getPieChartOptions(): ChartOptions {
    return {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'right'
        }
      }
    };
  }

  /**
   * Génère les options pour les graphiques en barres
   */
  getBarChartOptions(title: string = ''): ChartOptions {
    return {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'top'
        },
        title: {
          display: !!title,
          text: title
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: 'Valeur'
          }
        }
      }
    };
  }

  /**
   * Génère des données de démonstration pour les tests
   */
  generateDemoData(): any {
    return {
      performances: [
        { date: '2024-01-01', technique: 3.5, physique: 3.2, tactique: 3.8, mental: 4.0 },
        { date: '2024-02-01', technique: 3.7, physique: 3.4, tactique: 3.9, mental: 4.1 },
        { date: '2024-03-01', technique: 3.9, physique: 3.6, tactique: 4.0, mental: 4.2 },
        { date: '2024-04-01', technique: 4.1, physique: 3.8, tactique: 4.1, mental: 4.3 },
        { date: '2024-05-01', technique: 4.3, physique: 4.0, tactique: 4.2, mental: 4.4 }
      ],
      presences: [
        { mois: 'Jan', tauxPresence: 85 },
        { mois: 'Fév', tauxPresence: 88 },
        { mois: 'Mar', tauxPresence: 92 },
        { mois: 'Avr', tauxPresence: 87 },
        { mois: 'Mai', tauxPresence: 90 }
      ],
      charges: [
        { date: '2024-05-01', charge: 65 },
        { date: '2024-05-08', charge: 70 },
        { date: '2024-05-15', charge: 68 },
        { date: '2024-05-22', charge: 72 },
        { date: '2024-05-29', charge: 69 }
      ]
    };
  }

  /**
   * Récupère les données de performance depuis le backend
   */
  getPerformanceData(joueurId?: number): Observable<any[]> {
    if (joueurId) {
      return this.performanceService.getPerformancesByJoueur(joueurId).pipe(
        map(performances => performances.map(p => ({
          date: p.dateEvaluation,
          technique: p.categorie === 'TECHNIQUE' ? p.note : 0,
          physique: p.categorie === 'PHYSIQUE' ? p.note : 0,
          tactique: p.categorie === 'TACTIQUE' ? p.note : 0,
          mental: p.categorie === 'MENTAL' ? p.note : 0
        }))),
        catchError(error => {
          console.error('Erreur lors du chargement des performances:', error);
          return of([]);
        })
      );
    } else {
      return this.performanceService.getAllPerformances().pipe(
        map(performances => performances.map(p => ({
          date: p.dateEvaluation,
          technique: p.categorie === 'TECHNIQUE' ? p.note : 0,
          physique: p.categorie === 'PHYSIQUE' ? p.note : 0,
          tactique: p.categorie === 'TACTIQUE' ? p.note : 0,
          mental: p.categorie === 'MENTAL' ? p.note : 0
        }))),
        catchError(error => {
          console.error('Erreur lors du chargement des performances:', error);
          return of([]);
        })
      );
    }
  }

  /**
   * Récupère les données de présence depuis le backend
   */
  getPresenceData(): Observable<any[]> {
    return this.participationService.getStatistiques().pipe(
      map(stats => [
        { date: new Date().toISOString().split('T')[0], presents: stats.totalPresents || 0, absents: stats.totalAbsents || 0 }
      ]),
      catchError(error => {
        console.error('Erreur lors du chargement des données de présence:', error);
        return of([]);
      })
    );
  }

  /**
   * Récupère les données d'absence depuis le backend
   */
  getAbsenceData(): Observable<any> {
    return this.absenceService.getStatistiques().pipe(
      map(stats => ({
        justifiees: stats.justifiees || 0,
        injustifiees: stats.injustifiees || 0,
        enAttente: stats.enAttente || 0
      })),
      catchError(error => {
        console.error('Erreur lors du chargement des données d\'absence:', error);
        return of({ justifiees: 0, injustifiees: 0, enAttente: 0 });
      })
    );
  }
}
