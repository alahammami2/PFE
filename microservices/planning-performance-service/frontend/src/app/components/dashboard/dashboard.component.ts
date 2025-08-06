import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { EntrainementService } from '../../services/entrainement.service';
import { 
  Entrainement, 
  EntrainementStats,
  StatutEntrainement,
  TypeEntrainement,
  StatutEntrainementLabels,
  TypeEntrainementLabels,
  StatutEntrainementColors,
  TypeEntrainementColors
} from '../../models/entrainement.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="dashboard-container fade-in">
      <div class="dashboard-header">
        <h1 class="page-title">
          <i class="material-icons me-2">dashboard</i>
          Tableau de bord - Planning & Performance
        </h1>
        <p class="page-subtitle">Vue d'ensemble des entraînements et performances</p>
      </div>

      <!-- Statistiques rapides -->
      <div class="row mb-4">
        <div class="col-md-3 col-sm-6 mb-3">
          <div class="stat-card stat-primary">
            <div class="stat-number">{{ stats?.totalEntrainements || 0 }}</div>
            <div class="stat-label">Total Entraînements</div>
            <i class="material-icons stat-icon">fitness_center</i>
          </div>
        </div>
        <div class="col-md-3 col-sm-6 mb-3">
          <div class="stat-card stat-success">
            <div class="stat-number">{{ stats?.entrainementsTermines || 0 }}</div>
            <div class="stat-label">Terminés</div>
            <i class="material-icons stat-icon">check_circle</i>
          </div>
        </div>
        <div class="col-md-3 col-sm-6 mb-3">
          <div class="stat-card stat-warning">
            <div class="stat-number">{{ stats?.entrainementsPlanifies || 0 }}</div>
            <div class="stat-label">Planifiés</div>
            <i class="material-icons stat-icon">schedule</i>
          </div>
        </div>
        <div class="col-md-3 col-sm-6 mb-3">
          <div class="stat-card stat-info">
            <div class="stat-number">{{ formatPercentage(stats?.tauxParticipation) }}</div>
            <div class="stat-label">Taux Participation</div>
            <i class="material-icons stat-icon">people</i>
          </div>
        </div>
      </div>

      <div class="row">
        <!-- Entraînements prochains -->
        <div class="col-lg-8 mb-4">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="material-icons me-2">upcoming</i>
                Prochains Entraînements
              </h5>
              <a routerLink="/entrainements" class="btn btn-sm btn-outline-light">
                Voir tout
              </a>
            </div>
            <div class="card-body">
              <div *ngIf="prochainsEntrainements.length === 0" class="text-center py-4">
                <i class="material-icons text-muted" style="font-size: 3rem;">event_note</i>
                <p class="text-muted mt-2">Aucun entraînement planifié</p>
                <a routerLink="/entrainements/nouveau" class="btn btn-primary">
                  <i class="material-icons me-1">add</i>
                  Planifier un entraînement
                </a>
              </div>
              
              <div *ngFor="let entrainement of prochainsEntrainements" class="entrainement-item">
                <div class="entrainement-info">
                  <div class="entrainement-header">
                    <h6 class="entrainement-title">{{ entrainement.titre }}</h6>
                    <div class="entrainement-badges">
                      <span 
                        class="badge badge-type"
                        [style.background-color]="getTypeColor(entrainement.type)"
                      >
                        {{ getTypeLabel(entrainement.type) }}
                      </span>
                      <span 
                        class="badge badge-status"
                        [style.background-color]="getStatutColor(entrainement.statut!)"
                      >
                        {{ getStatutLabel(entrainement.statut!) }}
                      </span>
                    </div>
                  </div>
                  
                  <div class="entrainement-details">
                    <div class="detail-item">
                      <i class="material-icons">schedule</i>
                      <span>{{ formatDate(entrainement.date) }} à {{ entrainement.heureDebut }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="material-icons">location_on</i>
                      <span>{{ entrainement.lieu }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="material-icons">people</i>
                      <span>{{ entrainement.nombreInscrits || 0 }}/{{ entrainement.nombreMaxParticipants }} participants</span>
                    </div>
                  </div>
                </div>
                
                <div class="entrainement-actions">
                  <a 
                    [routerLink]="['/entrainements', entrainement.id]" 
                    class="btn btn-sm btn-outline-primary"
                  >
                    <i class="material-icons">visibility</i>
                  </a>
                  <button 
                    *ngIf="canStartEntrainement(entrainement)"
                    (click)="startEntrainement(entrainement.id!)"
                    class="btn btn-sm btn-success"
                  >
                    <i class="material-icons">play_arrow</i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Actions rapides -->
        <div class="col-lg-4 mb-4">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="material-icons me-2">flash_on</i>
                Actions Rapides
              </h5>
            </div>
            <div class="card-body">
              <div class="quick-actions">
                <a routerLink="/entrainements/nouveau" class="quick-action-btn">
                  <i class="material-icons">add_circle</i>
                  <span>Nouvel Entraînement</span>
                </a>
                
                <a routerLink="/participations" class="quick-action-btn">
                  <i class="material-icons">how_to_reg</i>
                  <span>Gérer Présences</span>
                </a>
                
                <a routerLink="/performances" class="quick-action-btn">
                  <i class="material-icons">assessment</i>
                  <span>Évaluer Performances</span>
                </a>
                
                <a routerLink="/absences/declarer" class="quick-action-btn">
                  <i class="material-icons">report_problem</i>
                  <span>Déclarer Absence</span>
                </a>
                
                <a routerLink="/objectifs/nouveau" class="quick-action-btn">
                  <i class="material-icons">flag</i>
                  <span>Nouvel Objectif</span>
                </a>
                
                <a routerLink="/calendrier" class="quick-action-btn">
                  <i class="material-icons">calendar_today</i>
                  <span>Voir Calendrier</span>
                </a>
              </div>
            </div>
          </div>

          <!-- Alertes et notifications -->
          <div class="card mt-4">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="material-icons me-2">notifications</i>
                Alertes
              </h5>
            </div>
            <div class="card-body">
              <div class="alert alert-warning">
                <i class="material-icons me-2">warning</i>
                <strong>3 entraînements</strong> nécessitent votre attention
              </div>
              
              <div class="alert alert-info">
                <i class="material-icons me-2">info</i>
                <strong>5 objectifs</strong> arrivent à échéance cette semaine
              </div>
              
              <div class="alert alert-success">
                <i class="material-icons me-2">trending_up</i>
                Taux de présence en <strong>amélioration</strong> ce mois
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      max-width: 1200px;
      margin: 0 auto;
    }

    .dashboard-header {
      margin-bottom: 2rem;
      text-align: center;
      
      .page-title {
        color: var(--primary-color);
        font-weight: 600;
        margin-bottom: 0.5rem;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      
      .page-subtitle {
        color: var(--secondary-color);
        font-size: 1.1rem;
        margin: 0;
      }
    }

    .stat-card {
      position: relative;
      overflow: hidden;
      
      .stat-icon {
        position: absolute;
        top: 1rem;
        right: 1rem;
        font-size: 2rem;
        opacity: 0.3;
      }
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .entrainement-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem;
      border: 1px solid #eee;
      border-radius: var(--border-radius);
      margin-bottom: 1rem;
      transition: var(--transition);
      
      &:hover {
        box-shadow: var(--box-shadow);
        transform: translateY(-2px);
      }
      
      &:last-child {
        margin-bottom: 0;
      }
    }

    .entrainement-info {
      flex: 1;
    }

    .entrainement-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 0.5rem;
      
      .entrainement-title {
        margin: 0;
        color: var(--primary-color);
        font-weight: 500;
      }
      
      .entrainement-badges {
        display: flex;
        gap: 0.5rem;
      }
    }

    .entrainement-details {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
      
      .detail-item {
        display: flex;
        align-items: center;
        font-size: 0.9rem;
        color: var(--secondary-color);
        
        .material-icons {
          font-size: 1rem;
          margin-right: 0.5rem;
          color: #999;
        }
      }
    }

    .entrainement-actions {
      display: flex;
      gap: 0.5rem;
      margin-left: 1rem;
    }

    .quick-actions {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 0.75rem;
    }

    .quick-action-btn {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 1rem;
      background: white;
      border: 2px solid #eee;
      border-radius: var(--border-radius);
      text-decoration: none;
      color: var(--secondary-color);
      transition: var(--transition);
      
      &:hover {
        border-color: var(--primary-color);
        color: var(--primary-color);
        transform: translateY(-2px);
        box-shadow: var(--box-shadow);
      }
      
      .material-icons {
        font-size: 1.5rem;
        margin-bottom: 0.5rem;
      }
      
      span {
        font-size: 0.8rem;
        text-align: center;
        font-weight: 500;
      }
    }

    .alert {
      display: flex;
      align-items: center;
      margin-bottom: 0.75rem;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .material-icons {
        font-size: 1.2rem;
      }
    }

    @media (max-width: 768px) {
      .entrainement-item {
        flex-direction: column;
        align-items: flex-start;
        
        .entrainement-actions {
          margin-left: 0;
          margin-top: 1rem;
          width: 100%;
          justify-content: flex-end;
        }
      }
      
      .entrainement-header {
        flex-direction: column;
        align-items: flex-start;
        
        .entrainement-badges {
          margin-top: 0.5rem;
        }
      }
      
      .quick-actions {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class DashboardComponent implements OnInit {
  stats: EntrainementStats | null = null;
  prochainsEntrainements: Entrainement[] = [];
  loading = true;

  constructor(private entrainementService: EntrainementService) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private loadDashboardData(): void {
    // Charger les statistiques
    this.entrainementService.getStatistiques().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des statistiques:', error);
      }
    });

    // Charger les prochains entraînements
    this.entrainementService.getEntrainementsProchains(5).subscribe({
      next: (entrainements) => {
        this.prochainsEntrainements = entrainements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des prochains entraînements:', error);
        this.loading = false;
      }
    });
  }

  startEntrainement(id: number): void {
    this.entrainementService.demarrerEntrainement(id).subscribe({
      next: () => {
        this.loadDashboardData(); // Recharger les données
      },
      error: (error) => {
        console.error('Erreur lors du démarrage de l\'entraînement:', error);
      }
    });
  }

  canStartEntrainement(entrainement: Entrainement): boolean {
    return this.entrainementService.canDemarrerEntrainement(entrainement);
  }

  // Méthodes utilitaires
  getTypeLabel(type: TypeEntrainement): string {
    return TypeEntrainementLabels[type];
  }

  getStatutLabel(statut: StatutEntrainement): string {
    return StatutEntrainementLabels[statut];
  }

  getTypeColor(type: TypeEntrainement): string {
    return TypeEntrainementColors[type];
  }

  getStatutColor(statut: StatutEntrainement): string {
    return StatutEntrainementColors[statut];
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      weekday: 'short',
      day: 'numeric',
      month: 'short'
    });
  }

  formatPercentage(value: number | undefined): string {
    return value ? `${Math.round(value)}%` : '0%';
  }
}
