import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { EntrainementService } from '../../../services/entrainement.service';
import { 
  Entrainement,
  StatutEntrainement,
  TypeEntrainement,
  TypeEntrainementLabels,
  StatutEntrainementLabels,
  TypeEntrainementColors,
  StatutEntrainementColors
} from '../../../models/entrainement.model';

@Component({
  selector: 'app-entrainement-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="entrainement-detail-container fade-in" *ngIf="entrainement">
      <div class="page-header">
        <div class="header-content">
          <div class="header-main">
            <h1 class="page-title">{{ entrainement.titre }}</h1>
            <div class="header-badges">
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
          <p class="page-subtitle" *ngIf="entrainement.description">
            {{ entrainement.description }}
          </p>
        </div>
        <div class="header-actions">
          <button type="button" class="btn btn-outline-secondary" (click)="goBack()">
            <i class="material-icons me-1">arrow_back</i>
            Retour
          </button>
          
          <button 
            *ngIf="canStartEntrainement()"
            (click)="startEntrainement()"
            class="btn btn-success"
          >
            <i class="material-icons me-1">play_arrow</i>
            Démarrer
          </button>
          
          <button 
            *ngIf="canFinishEntrainement()"
            (click)="finishEntrainement()"
            class="btn btn-warning"
          >
            <i class="material-icons me-1">stop</i>
            Terminer
          </button>
          
          <a 
            *ngIf="canEditEntrainement()"
            [routerLink]="['/entrainements', entrainement.id, 'modifier']" 
            class="btn btn-primary"
          >
            <i class="material-icons me-1">edit</i>
            Modifier
          </a>
        </div>
      </div>

      <div class="row">
        <!-- Informations principales -->
        <div class="col-lg-8">
          <div class="card mb-4">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="material-icons me-2">info</i>
                Détails de l'entraînement
              </h5>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-6">
                  <div class="info-group">
                    <label class="info-label">
                      <i class="material-icons">schedule</i>
                      Date et heure
                    </label>
                    <div class="info-value">
                      <div class="date-time">
                        <span class="date">{{ formatDate(entrainement.date) }}</span>
                        <span class="time">{{ entrainement.heureDebut }} - {{ entrainement.heureFin }}</span>
                      </div>
                      <small class="text-muted">Durée: {{ formatDuree() }}</small>
                    </div>
                  </div>
                </div>

                <div class="col-md-6">
                  <div class="info-group">
                    <label class="info-label">
                      <i class="material-icons">location_on</i>
                      Lieu
                    </label>
                    <div class="info-value">{{ entrainement.lieu }}</div>
                  </div>
                </div>

                <div class="col-md-6">
                  <div class="info-group">
                    <label class="info-label">
                      <i class="material-icons">person</i>
                      Coach responsable
                    </label>
                    <div class="info-value">Coach #{{ entrainement.coachId }}</div>
                  </div>
                </div>

                <div class="col-md-6">
                  <div class="info-group">
                    <label class="info-label">
                      <i class="material-icons">people</i>
                      Participants
                    </label>
                    <div class="info-value">
                      <div class="participants-info">
                        <span class="participants-count">
                          {{ entrainement.nombreInscrits || 0 }} / {{ entrainement.nombreMaxParticipants }}
                        </span>
                        <div class="progress mt-1">
                          <div 
                            class="progress-bar" 
                            [style.width.%]="getParticipationPercentage()"
                            [class.bg-success]="getParticipationPercentage() >= 80"
                            [class.bg-warning]="getParticipationPercentage() >= 50 && getParticipationPercentage() < 80"
                            [class.bg-danger]="getParticipationPercentage() < 50"
                          ></div>
                        </div>
                      </div>
                      <small class="text-muted">
                        {{ entrainement.placesDisponibles ? 'Places disponibles' : 'Complet' }}
                      </small>
                    </div>
                  </div>
                </div>
              </div>

              <div *ngIf="entrainement.notes" class="info-group mt-3">
                <label class="info-label">
                  <i class="material-icons">note</i>
                  Notes
                </label>
                <div class="info-value">
                  <div class="notes-content">{{ entrainement.notes }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Statistiques de participation -->
          <div class="card mb-4" *ngIf="entrainement.statut === 'TERMINE'">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="material-icons me-2">analytics</i>
                Statistiques de participation
              </h5>
            </div>
            <div class="card-body">
              <div class="row text-center">
                <div class="col-md-4">
                  <div class="stat-item">
                    <div class="stat-number text-success">{{ entrainement.nombrePresents || 0 }}</div>
                    <div class="stat-label">Présents</div>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="stat-item">
                    <div class="stat-number text-danger">{{ entrainement.nombreAbsents || 0 }}</div>
                    <div class="stat-label">Absents</div>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="stat-item">
                    <div class="stat-number text-primary">{{ calculatePresenceRate() }}%</div>
                    <div class="stat-label">Taux de présence</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Actions et informations complémentaires -->
        <div class="col-lg-4">
          <!-- Actions rapides -->
          <div class="card mb-4">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="material-icons me-2">flash_on</i>
                Actions rapides
              </h5>
            </div>
            <div class="card-body">
              <div class="quick-actions">
                <a 
                  [routerLink]="['/participations/presence', entrainement.id]" 
                  class="quick-action-btn"
                  *ngIf="entrainement.statut === 'EN_COURS' || entrainement.statut === 'TERMINE'"
                >
                  <i class="material-icons">how_to_reg</i>
                  <span>Gérer les présences</span>
                </a>
                
                <a 
                  [routerLink]="['/performances/evaluer', entrainement.id]" 
                  class="quick-action-btn"
                  *ngIf="entrainement.statut === 'EN_COURS' || entrainement.statut === 'TERMINE'"
                >
                  <i class="material-icons">assessment</i>
                  <span>Évaluer performances</span>
                </a>
                
                <button 
                  class="quick-action-btn"
                  (click)="exportEntrainement()"
                >
                  <i class="material-icons">download</i>
                  <span>Exporter les données</span>
                </button>
                
                <button 
                  *ngIf="canCancelEntrainement()"
                  class="quick-action-btn text-danger"
                  (click)="cancelEntrainement()"
                >
                  <i class="material-icons">cancel</i>
                  <span>Annuler l'entraînement</span>
                </button>
              </div>
            </div>
          </div>

          <!-- Informations système -->
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="material-icons me-2">info_outline</i>
                Informations système
              </h5>
            </div>
            <div class="card-body">
              <div class="system-info">
                <div class="system-item">
                  <label>ID de l'entraînement</label>
                  <span>#{{ entrainement.id }}</span>
                </div>
                
                <div class="system-item">
                  <label>Statut actuel</label>
                  <span 
                    class="badge"
                    [style.background-color]="getStatutColor(entrainement.statut!)"
                  >
                    {{ getStatutLabel(entrainement.statut!) }}
                  </span>
                </div>
                
                <div class="system-item">
                  <label>Type d'entraînement</label>
                  <span 
                    class="badge"
                    [style.background-color]="getTypeColor(entrainement.type)"
                  >
                    {{ getTypeLabel(entrainement.type) }}
                  </span>
                </div>
                
                <div class="system-item">
                  <label>Places restantes</label>
                  <span class="text-primary">
                    {{ (entrainement.nombreMaxParticipants - (entrainement.nombreInscrits || 0)) }}
                  </span>
                </div>
              </div>

              <div class="alert alert-info mt-3">
                <i class="material-icons me-2">info</i>
                <small>
                  Les modifications ne sont possibles que pour les entraînements planifiés.
                </small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading state -->
    <div *ngIf="!entrainement && loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-3 text-muted">Chargement des détails...</p>
    </div>

    <!-- Error state -->
    <div *ngIf="!entrainement && !loading" class="text-center py-5">
      <i class="material-icons text-muted" style="font-size: 4rem;">error_outline</i>
      <h5 class="text-muted mt-3">Entraînement non trouvé</h5>
      <p class="text-muted">L'entraînement demandé n'existe pas ou a été supprimé.</p>
      <button class="btn btn-primary" (click)="goBack()">
        <i class="material-icons me-1">arrow_back</i>
        Retour à la liste
      </button>
    </div>
  `,
  styles: [`
    .entrainement-detail-container {
      max-width: 1200px;
      margin: 0 auto;
    }

    .page-header {
      margin-bottom: 2rem;
      
      .header-content {
        margin-bottom: 1rem;
        
        .header-main {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 0.5rem;
          
          .page-title {
            color: var(--primary-color);
            font-weight: 600;
            margin: 0;
            flex: 1;
          }
          
          .header-badges {
            display: flex;
            gap: 0.5rem;
            margin-left: 1rem;
          }
        }
        
        .page-subtitle {
          color: var(--secondary-color);
          margin: 0;
          font-style: italic;
        }
      }
      
      .header-actions {
        display: flex;
        gap: 0.75rem;
        flex-wrap: wrap;
      }
    }

    .info-group {
      margin-bottom: 1.5rem;
      
      .info-label {
        display: flex;
        align-items: center;
        font-weight: 500;
        color: var(--secondary-color);
        margin-bottom: 0.5rem;
        font-size: 0.9rem;
        
        .material-icons {
          font-size: 1.1rem;
          margin-right: 0.5rem;
          color: #999;
        }
      }
      
      .info-value {
        color: var(--primary-color);
        font-weight: 500;
        
        .date-time {
          .date {
            display: block;
            font-size: 1.1rem;
          }
          
          .time {
            color: var(--secondary-color);
            font-size: 0.9rem;
          }
        }
        
        .participants-info {
          .participants-count {
            font-size: 1.1rem;
            font-weight: 600;
          }
          
          .progress {
            height: 6px;
            background-color: #e9ecef;
          }
        }
        
        .notes-content {
          background: #f8f9fa;
          padding: 1rem;
          border-radius: var(--border-radius);
          border-left: 4px solid var(--primary-color);
          line-height: 1.6;
        }
      }
    }

    .stat-item {
      .stat-number {
        font-size: 2rem;
        font-weight: 700;
        line-height: 1;
      }
      
      .stat-label {
        color: var(--secondary-color);
        font-size: 0.9rem;
        margin-top: 0.25rem;
      }
    }

    .quick-actions {
      display: grid;
      grid-template-columns: 1fr;
      gap: 0.75rem;
    }

    .quick-action-btn {
      display: flex;
      align-items: center;
      padding: 0.75rem 1rem;
      background: white;
      border: 2px solid #eee;
      border-radius: var(--border-radius);
      text-decoration: none;
      color: var(--secondary-color);
      transition: var(--transition);
      cursor: pointer;
      width: 100%;
      
      &:hover {
        border-color: var(--primary-color);
        color: var(--primary-color);
        transform: translateY(-2px);
        box-shadow: var(--box-shadow);
      }
      
      &.text-danger:hover {
        border-color: var(--danger-color);
        color: var(--danger-color);
      }
      
      .material-icons {
        margin-right: 0.75rem;
        font-size: 1.3rem;
      }
      
      span {
        font-weight: 500;
      }
    }

    .system-info {
      .system-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.5rem 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        label {
          font-size: 0.9rem;
          color: var(--secondary-color);
          margin: 0;
        }
        
        span {
          font-weight: 500;
        }
      }
    }

    .badge {
      font-size: 0.75rem;
      padding: 0.35rem 0.65rem;
      border-radius: 0.375rem;
    }

    @media (max-width: 768px) {
      .page-header {
        .header-content .header-main {
          flex-direction: column;
          align-items: flex-start;
          
          .header-badges {
            margin-left: 0;
            margin-top: 0.5rem;
          }
        }
        
        .header-actions {
          width: 100%;
          justify-content: stretch;
          
          .btn {
            flex: 1;
          }
        }
      }
      
      .quick-actions {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class EntrainementDetailComponent implements OnInit {
  entrainement: Entrainement | null = null;
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private entrainementService: EntrainementService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      if (id) {
        this.loadEntrainement(id);
      }
    });
  }

  private loadEntrainement(id: number): void {
    this.loading = true;
    this.entrainementService.getEntrainementById(id).subscribe({
      next: (entrainement) => {
        this.entrainement = entrainement;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement:', error);
        this.loading = false;
      }
    });
  }

  startEntrainement(): void {
    if (!this.entrainement?.id) return;
    
    this.entrainementService.demarrerEntrainement(this.entrainement.id).subscribe({
      next: () => {
        this.loadEntrainement(this.entrainement!.id!);
      },
      error: (error) => {
        console.error('Erreur lors du démarrage:', error);
      }
    });
  }

  finishEntrainement(): void {
    if (!this.entrainement?.id) return;
    
    this.entrainementService.terminerEntrainement(this.entrainement.id).subscribe({
      next: () => {
        this.loadEntrainement(this.entrainement!.id!);
      },
      error: (error) => {
        console.error('Erreur lors de la fin:', error);
      }
    });
  }

  cancelEntrainement(): void {
    if (!this.entrainement?.id) return;
    
    if (confirm('Êtes-vous sûr de vouloir annuler cet entraînement ?')) {
      this.entrainementService.annulerEntrainement(this.entrainement.id).subscribe({
        next: () => {
          this.loadEntrainement(this.entrainement!.id!);
        },
        error: (error) => {
          console.error('Erreur lors de l\'annulation:', error);
        }
      });
    }
  }

  exportEntrainement(): void {
    if (!this.entrainement?.id) return;
    
    // Logique d'export à implémenter
    console.log('Export de l\'entraînement', this.entrainement.id);
  }

  goBack(): void {
    this.router.navigate(['/entrainements']);
  }

  // Méthodes de validation
  canStartEntrainement(): boolean {
    return this.entrainement ? this.entrainementService.canDemarrerEntrainement(this.entrainement) : false;
  }

  canFinishEntrainement(): boolean {
    return this.entrainement ? this.entrainementService.canTerminerEntrainement(this.entrainement) : false;
  }

  canEditEntrainement(): boolean {
    return this.entrainement ? this.entrainementService.isEntrainementModifiable(this.entrainement) : false;
  }

  canCancelEntrainement(): boolean {
    return this.entrainement ? this.entrainementService.isEntrainementAnnulable(this.entrainement) : false;
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
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  formatDuree(): string {
    if (!this.entrainement) return '';
    return this.entrainementService.formatDuree(this.entrainement.heureDebut, this.entrainement.heureFin);
  }

  getParticipationPercentage(): number {
    if (!this.entrainement?.nombreInscrits || !this.entrainement?.nombreMaxParticipants) return 0;
    return (this.entrainement.nombreInscrits / this.entrainement.nombreMaxParticipants) * 100;
  }

  calculatePresenceRate(): number {
    if (!this.entrainement?.nombrePresents || !this.entrainement?.nombreInscrits) return 0;
    return Math.round((this.entrainement.nombrePresents / this.entrainement.nombreInscrits) * 100);
  }
}
