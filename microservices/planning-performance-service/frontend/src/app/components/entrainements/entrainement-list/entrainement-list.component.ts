import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { EntrainementService } from '../../../services/entrainement.service';
import { 
  Entrainement, 
  EntrainementFilters,
  TypeEntrainement,
  StatutEntrainement,
  TypeEntrainementLabels,
  StatutEntrainementLabels,
  TypeEntrainementColors,
  StatutEntrainementColors
} from '../../../models/entrainement.model';

@Component({
  selector: 'app-entrainement-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="entrainement-list-container fade-in">
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <i class="material-icons me-2">fitness_center</i>
            Gestion des Entraînements
          </h1>
          <p class="page-subtitle">Planifiez et gérez vos séances d'entraînement</p>
        </div>
        <div class="header-actions">
          <a routerLink="/entrainements/nouveau" class="btn btn-primary">
            <i class="material-icons me-1">add</i>
            Nouvel Entraînement
          </a>
        </div>
      </div>

      <!-- Filtres -->
      <div class="filter-section">
        <div class="row">
          <div class="col-md-3 mb-3">
            <label class="form-label">Type d'entraînement</label>
            <select class="form-select" [(ngModel)]="filters.type" (change)="applyFilters()">
              <option value="">Tous les types</option>
              <option *ngFor="let type of typeOptions" [value]="type.value">
                {{ type.label }}
              </option>
            </select>
          </div>
          
          <div class="col-md-3 mb-3">
            <label class="form-label">Statut</label>
            <select class="form-select" [(ngModel)]="filters.statut" (change)="applyFilters()">
              <option value="">Tous les statuts</option>
              <option *ngFor="let statut of statutOptions" [value]="statut.value">
                {{ statut.label }}
              </option>
            </select>
          </div>
          
          <div class="col-md-3 mb-3">
            <label class="form-label">Date début</label>
            <input 
              type="date" 
              class="form-control" 
              [(ngModel)]="filters.dateDebut"
              (change)="applyFilters()"
            >
          </div>
          
          <div class="col-md-3 mb-3">
            <label class="form-label">Date fin</label>
            <input 
              type="date" 
              class="form-control" 
              [(ngModel)]="filters.dateFin"
              (change)="applyFilters()"
            >
          </div>
        </div>
        
        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">Lieu</label>
            <input 
              type="text" 
              class="form-control" 
              placeholder="Rechercher par lieu..."
              [(ngModel)]="filters.lieu"
              (input)="applyFilters()"
            >
          </div>
          
          <div class="col-md-6 mb-3 d-flex align-items-end">
            <button class="btn btn-outline-secondary me-2" (click)="clearFilters()">
              <i class="material-icons me-1">clear</i>
              Effacer les filtres
            </button>
            <button class="btn btn-outline-primary" (click)="exportEntrainements()">
              <i class="material-icons me-1">download</i>
              Exporter
            </button>
          </div>
        </div>
      </div>

      <!-- Liste des entraînements -->
      <div class="card">
        <div class="card-header">
          <h5 class="mb-0">
            <i class="material-icons me-2">list</i>
            Entraînements ({{ filteredEntrainements.length }})
          </h5>
          <div class="view-options">
            <button 
              class="btn btn-sm"
              [class.btn-primary]="viewMode === 'card'"
              [class.btn-outline-primary]="viewMode !== 'card'"
              (click)="viewMode = 'card'"
            >
              <i class="material-icons">view_module</i>
            </button>
            <button 
              class="btn btn-sm"
              [class.btn-primary]="viewMode === 'table'"
              [class.btn-outline-primary]="viewMode !== 'table'"
              (click)="viewMode = 'table'"
            >
              <i class="material-icons">view_list</i>
            </button>
          </div>
        </div>
        
        <div class="card-body">
          <!-- Vue en cartes -->
          <div *ngIf="viewMode === 'card'" class="row">
            <div *ngFor="let entrainement of filteredEntrainements" class="col-lg-6 col-xl-4 mb-4">
              <div class="entrainement-card">
                <div class="card-header-custom">
                  <h6 class="card-title">{{ entrainement.titre }}</h6>
                  <div class="card-badges">
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
                
                <div class="card-content">
                  <div class="entrainement-details">
                    <div class="detail-row">
                      <i class="material-icons">schedule</i>
                      <span>{{ formatDateTime(entrainement.date, entrainement.heureDebut) }}</span>
                    </div>
                    <div class="detail-row">
                      <i class="material-icons">location_on</i>
                      <span>{{ entrainement.lieu }}</span>
                    </div>
                    <div class="detail-row">
                      <i class="material-icons">people</i>
                      <span>{{ entrainement.nombreInscrits || 0 }}/{{ entrainement.nombreMaxParticipants }}</span>
                    </div>
                    <div class="detail-row">
                      <i class="material-icons">access_time</i>
                      <span>{{ formatDuree(entrainement.heureDebut, entrainement.heureFin) }}</span>
                    </div>
                  </div>
                  
                  <div *ngIf="entrainement.description" class="entrainement-description">
                    {{ entrainement.description }}
                  </div>
                </div>
                
                <div class="card-actions">
                  <a 
                    [routerLink]="['/entrainements', entrainement.id]" 
                    class="btn btn-sm btn-outline-primary"
                  >
                    <i class="material-icons">visibility</i>
                    Voir
                  </a>
                  
                  <button 
                    *ngIf="canStartEntrainement(entrainement)"
                    (click)="startEntrainement(entrainement.id!)"
                    class="btn btn-sm btn-success"
                  >
                    <i class="material-icons">play_arrow</i>
                    Démarrer
                  </button>
                  
                  <button 
                    *ngIf="canFinishEntrainement(entrainement)"
                    (click)="finishEntrainement(entrainement.id!)"
                    class="btn btn-sm btn-warning"
                  >
                    <i class="material-icons">stop</i>
                    Terminer
                  </button>
                  
                  <a 
                    *ngIf="canEditEntrainement(entrainement)"
                    [routerLink]="['/entrainements', entrainement.id, 'modifier']" 
                    class="btn btn-sm btn-outline-secondary"
                  >
                    <i class="material-icons">edit</i>
                  </a>
                  
                  <button 
                    *ngIf="canCancelEntrainement(entrainement)"
                    (click)="cancelEntrainement(entrainement.id!)"
                    class="btn btn-sm btn-outline-danger"
                  >
                    <i class="material-icons">cancel</i>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Vue en tableau -->
          <div *ngIf="viewMode === 'table'" class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>Titre</th>
                  <th>Date & Heure</th>
                  <th>Type</th>
                  <th>Lieu</th>
                  <th>Participants</th>
                  <th>Statut</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let entrainement of filteredEntrainements">
                  <td>
                    <strong>{{ entrainement.titre }}</strong>
                    <div *ngIf="entrainement.description" class="text-muted small">
                      {{ entrainement.description | slice:0:50 }}{{ entrainement.description!.length > 50 ? '...' : '' }}
                    </div>
                  </td>
                  <td>
                    <div>{{ formatDate(entrainement.date) }}</div>
                    <div class="text-muted small">{{ entrainement.heureDebut }} - {{ entrainement.heureFin }}</div>
                  </td>
                  <td>
                    <span 
                      class="badge"
                      [style.background-color]="getTypeColor(entrainement.type)"
                    >
                      {{ getTypeLabel(entrainement.type) }}
                    </span>
                  </td>
                  <td>{{ entrainement.lieu }}</td>
                  <td>
                    <span class="participants-count">
                      {{ entrainement.nombreInscrits || 0 }}/{{ entrainement.nombreMaxParticipants }}
                    </span>
                    <div class="progress mt-1" style="height: 4px;">
                      <div 
                        class="progress-bar" 
                        [style.width.%]="getParticipationPercentage(entrainement)"
                        [class.bg-success]="getParticipationPercentage(entrainement) >= 80"
                        [class.bg-warning]="getParticipationPercentage(entrainement) >= 50 && getParticipationPercentage(entrainement) < 80"
                        [class.bg-danger]="getParticipationPercentage(entrainement) < 50"
                      ></div>
                    </div>
                  </td>
                  <td>
                    <span 
                      class="badge"
                      [style.background-color]="getStatutColor(entrainement.statut!)"
                    >
                      {{ getStatutLabel(entrainement.statut!) }}
                    </span>
                  </td>
                  <td>
                    <div class="btn-group btn-group-sm">
                      <a 
                        [routerLink]="['/entrainements', entrainement.id]" 
                        class="btn btn-outline-primary"
                        title="Voir détails"
                      >
                        <i class="material-icons">visibility</i>
                      </a>
                      
                      <button 
                        *ngIf="canStartEntrainement(entrainement)"
                        (click)="startEntrainement(entrainement.id!)"
                        class="btn btn-success"
                        title="Démarrer"
                      >
                        <i class="material-icons">play_arrow</i>
                      </button>
                      
                      <button 
                        *ngIf="canFinishEntrainement(entrainement)"
                        (click)="finishEntrainement(entrainement.id!)"
                        class="btn btn-warning"
                        title="Terminer"
                      >
                        <i class="material-icons">stop</i>
                      </button>
                      
                      <a 
                        *ngIf="canEditEntrainement(entrainement)"
                        [routerLink]="['/entrainements', entrainement.id, 'modifier']" 
                        class="btn btn-outline-secondary"
                        title="Modifier"
                      >
                        <i class="material-icons">edit</i>
                      </a>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Message si aucun entraînement -->
          <div *ngIf="filteredEntrainements.length === 0" class="text-center py-5">
            <i class="material-icons text-muted" style="font-size: 4rem;">event_note</i>
            <h5 class="text-muted mt-3">Aucun entraînement trouvé</h5>
            <p class="text-muted">Essayez de modifier vos critères de recherche ou créez un nouvel entraînement.</p>
            <a routerLink="/entrainements/nouveau" class="btn btn-primary">
              <i class="material-icons me-1">add</i>
              Créer un entraînement
            </a>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .entrainement-list-container {
      max-width: 1400px;
      margin: 0 auto;
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 2rem;
      
      .header-content {
        .page-title {
          color: var(--primary-color);
          font-weight: 600;
          margin-bottom: 0.5rem;
          display: flex;
          align-items: center;
        }
        
        .page-subtitle {
          color: var(--secondary-color);
          margin: 0;
        }
      }
    }

    .filter-section {
      background: white;
      border-radius: var(--border-radius);
      padding: 1.5rem;
      margin-bottom: 1.5rem;
      box-shadow: var(--box-shadow);
    }

    .view-options {
      display: flex;
      gap: 0.25rem;
    }

    .entrainement-card {
      background: white;
      border-radius: var(--border-radius);
      box-shadow: var(--box-shadow);
      transition: var(--transition);
      height: 100%;
      display: flex;
      flex-direction: column;
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 25px rgba(0,0,0,0.15);
      }
    }

    .card-header-custom {
      padding: 1rem;
      border-bottom: 1px solid #eee;
      
      .card-title {
        margin: 0 0 0.5rem 0;
        color: var(--primary-color);
        font-weight: 500;
      }
      
      .card-badges {
        display: flex;
        gap: 0.5rem;
        flex-wrap: wrap;
      }
    }

    .card-content {
      padding: 1rem;
      flex: 1;
    }

    .entrainement-details {
      margin-bottom: 1rem;
      
      .detail-row {
        display: flex;
        align-items: center;
        margin-bottom: 0.5rem;
        font-size: 0.9rem;
        color: var(--secondary-color);
        
        .material-icons {
          font-size: 1rem;
          margin-right: 0.5rem;
          color: #999;
          width: 20px;
        }
      }
    }

    .entrainement-description {
      font-size: 0.85rem;
      color: #666;
      line-height: 1.4;
      max-height: 3.6em;
      overflow: hidden;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
    }

    .card-actions {
      padding: 1rem;
      border-top: 1px solid #eee;
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
    }

    .participants-count {
      font-weight: 500;
    }

    .badge {
      font-size: 0.75rem;
      padding: 0.35rem 0.65rem;
      border-radius: 0.375rem;
    }

    @media (max-width: 768px) {
      .page-header {
        flex-direction: column;
        gap: 1rem;
        
        .header-actions {
          width: 100%;
        }
      }
      
      .filter-section .row > div {
        margin-bottom: 1rem;
      }
      
      .view-options {
        margin-top: 1rem;
      }
    }
  `]
})
export class EntrainementListComponent implements OnInit {
  entrainements: Entrainement[] = [];
  filteredEntrainements: Entrainement[] = [];
  filters: EntrainementFilters = {};
  viewMode: 'card' | 'table' = 'card';
  loading = true;

  typeOptions = Object.values(TypeEntrainement).map(type => ({
    value: type,
    label: TypeEntrainementLabels[type]
  }));

  statutOptions = Object.values(StatutEntrainement).map(statut => ({
    value: statut,
    label: StatutEntrainementLabels[statut]
  }));

  constructor(private entrainementService: EntrainementService) {}

  ngOnInit(): void {
    this.loadEntrainements();
  }

  private loadEntrainements(): void {
    this.entrainementService.getAllEntrainements().subscribe({
      next: (entrainements) => {
        this.entrainements = entrainements;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des entraînements:', error);
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredEntrainements = this.entrainements.filter(entrainement => {
      if (this.filters.type && entrainement.type !== this.filters.type) return false;
      if (this.filters.statut && entrainement.statut !== this.filters.statut) return false;
      if (this.filters.dateDebut && entrainement.date < this.filters.dateDebut) return false;
      if (this.filters.dateFin && entrainement.date > this.filters.dateFin) return false;
      if (this.filters.lieu && !entrainement.lieu.toLowerCase().includes(this.filters.lieu.toLowerCase())) return false;
      
      return true;
    });
  }

  clearFilters(): void {
    this.filters = {};
    this.applyFilters();
  }

  exportEntrainements(): void {
    this.entrainementService.exporterEntrainements('excel', this.filters).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `entrainements_${new Date().toISOString().split('T')[0]}.xlsx`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Erreur lors de l\'export:', error);
      }
    });
  }

  startEntrainement(id: number): void {
    this.entrainementService.demarrerEntrainement(id).subscribe({
      next: () => {
        this.loadEntrainements();
      },
      error: (error) => {
        console.error('Erreur lors du démarrage:', error);
      }
    });
  }

  finishEntrainement(id: number): void {
    this.entrainementService.terminerEntrainement(id).subscribe({
      next: () => {
        this.loadEntrainements();
      },
      error: (error) => {
        console.error('Erreur lors de la fin:', error);
      }
    });
  }

  cancelEntrainement(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir annuler cet entraînement ?')) {
      this.entrainementService.annulerEntrainement(id).subscribe({
        next: () => {
          this.loadEntrainements();
        },
        error: (error) => {
          console.error('Erreur lors de l\'annulation:', error);
        }
      });
    }
  }

  // Méthodes de validation
  canStartEntrainement(entrainement: Entrainement): boolean {
    return this.entrainementService.canDemarrerEntrainement(entrainement);
  }

  canFinishEntrainement(entrainement: Entrainement): boolean {
    return this.entrainementService.canTerminerEntrainement(entrainement);
  }

  canEditEntrainement(entrainement: Entrainement): boolean {
    return this.entrainementService.isEntrainementModifiable(entrainement);
  }

  canCancelEntrainement(entrainement: Entrainement): boolean {
    return this.entrainementService.isEntrainementAnnulable(entrainement);
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
    return new Date(date).toLocaleDateString('fr-FR');
  }

  formatDateTime(date: string, heure: string): string {
    const dateObj = new Date(date);
    return `${dateObj.toLocaleDateString('fr-FR')} à ${heure}`;
  }

  formatDuree(heureDebut: string, heureFin: string): string {
    return this.entrainementService.formatDuree(heureDebut, heureFin);
  }

  getParticipationPercentage(entrainement: Entrainement): number {
    if (!entrainement.nombreInscrits || !entrainement.nombreMaxParticipants) return 0;
    return (entrainement.nombreInscrits / entrainement.nombreMaxParticipants) * 100;
  }
}
