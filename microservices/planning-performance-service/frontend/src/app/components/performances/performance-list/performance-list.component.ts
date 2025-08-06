import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PerformanceService } from '../../../services/performance.service';
import { EntrainementService } from '../../../services/entrainement.service';
import { 
  Performance,
  CategoriePerformance,
  CategoriePerformanceLabels,
  CategoriePerformanceColors,
  CategoriePerformanceIcons,
  PerformanceFilters
} from '../../../models/performance.model';
import { Entrainement } from '../../../models/entrainement.model';

@Component({
  selector: 'app-performance-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="performance-list-container fade-in">
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <i class="material-icons me-2">assessment</i>
            Évaluations de Performance
          </h1>
          <p class="page-subtitle">Suivi et analyse des performances des joueurs</p>
        </div>
        <div class="header-actions">
          <a routerLink="/performances/evaluer" class="btn btn-primary">
            <i class="material-icons me-1">add</i>
            Nouvelle Évaluation
          </a>
        </div>
      </div>

      <!-- Statistiques rapides -->
      <div class="stats-section">
        <div class="row">
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card stat-primary">
              <div class="stat-number">{{ stats.totalEvaluations }}</div>
              <div class="stat-label">Total Évaluations</div>
              <i class="material-icons stat-icon">assessment</i>
            </div>
          </div>
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card stat-success">
              <div class="stat-number">{{ formatNote(stats.moyenneGenerale) }}</div>
              <div class="stat-label">Moyenne Générale</div>
              <i class="material-icons stat-icon">trending_up</i>
            </div>
          </div>
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card stat-info">
              <div class="stat-number">{{ stats.meilleureNote }}</div>
              <div class="stat-label">Meilleure Note</div>
              <i class="material-icons stat-icon">star</i>
            </div>
          </div>
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card" [class]="'stat-' + getTendanceClass(stats.tendanceGenerale)">
              <div class="stat-number">
                <i class="material-icons">{{ getTendanceIcon(stats.tendanceGenerale) }}</i>
              </div>
              <div class="stat-label">{{ getTendanceLabel(stats.tendanceGenerale) }}</div>
              <i class="material-icons stat-icon">timeline</i>
            </div>
          </div>
        </div>
      </div>

      <!-- Filtres -->
      <div class="filter-section">
        <div class="row">
          <div class="col-md-3 mb-3">
            <label class="form-label">Joueur</label>
            <select class="form-select" [(ngModel)]="filters.joueurId" (change)="applyFilters()">
              <option value="">Tous les joueurs</option>
              <option *ngFor="let joueur of joueurs" [value]="joueur.id">
                {{ joueur.nom }}
              </option>
            </select>
          </div>
          
          <div class="col-md-3 mb-3">
            <label class="form-label">Entraînement</label>
            <select class="form-select" [(ngModel)]="filters.entrainementId" (change)="applyFilters()">
              <option value="">Tous les entraînements</option>
              <option *ngFor="let entrainement of entrainements" [value]="entrainement.id">
                {{ entrainement.titre }} - {{ formatDate(entrainement.date) }}
              </option>
            </select>
          </div>
          
          <div class="col-md-2 mb-3">
            <label class="form-label">Catégorie</label>
            <select class="form-select" [(ngModel)]="filters.categorie" (change)="applyFilters()">
              <option value="">Toutes</option>
              <option *ngFor="let categorie of categorieOptions" [value]="categorie.value">
                {{ categorie.label }}
              </option>
            </select>
          </div>
          
          <div class="col-md-2 mb-3">
            <label class="form-label">Note min</label>
            <input 
              type="number" 
              class="form-control" 
              min="0" 
              max="10" 
              step="0.1"
              [(ngModel)]="filters.noteMin"
              (input)="applyFilters()"
              placeholder="0"
            >
          </div>
          
          <div class="col-md-2 mb-3 d-flex align-items-end">
            <button class="btn btn-outline-secondary w-100" (click)="clearFilters()">
              <i class="material-icons me-1">clear</i>
              Effacer
            </button>
          </div>
        </div>
      </div>

      <!-- Vue par catégorie -->
      <div class="categories-overview">
        <div class="row">
          <div class="col-md-3 col-sm-6 mb-3" *ngFor="let categorie of categorieOptions">
            <div class="category-card" [style.border-left-color]="getCategorieColor(categorie.value)">
              <div class="category-header">
                <i class="material-icons" [style.color]="getCategorieColor(categorie.value)">
                  {{ getCategorieIcon(categorie.value) }}
                </i>
                <h6>{{ categorie.label }}</h6>
              </div>
              <div class="category-stats">
                <div class="category-average">
                  {{ formatNote(stats.moyenneParCategorie?.[categorie.value] || 0) }}
                </div>
                <div class="category-count">
                  {{ getEvaluationsCountByCategorie(categorie.value) }} évaluations
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Liste des performances -->
      <div class="card">
        <div class="card-header">
          <h5 class="mb-0">
            <i class="material-icons me-2">list</i>
            Évaluations ({{ filteredPerformances.length }})
          </h5>
          <div class="header-actions">
            <div class="btn-group btn-group-sm me-2">
              <button 
                class="btn"
                [class.btn-primary]="viewMode === 'list'"
                [class.btn-outline-primary]="viewMode !== 'list'"
                (click)="viewMode = 'list'"
              >
                <i class="material-icons">list</i>
              </button>
              <button 
                class="btn"
                [class.btn-primary]="viewMode === 'cards'"
                [class.btn-outline-primary]="viewMode !== 'cards'"
                (click)="viewMode = 'cards'"
              >
                <i class="material-icons">view_module</i>
              </button>
            </div>
            <button class="btn btn-sm btn-outline-primary" (click)="exportPerformances()">
              <i class="material-icons me-1">download</i>
              Exporter
            </button>
          </div>
        </div>
        
        <div class="card-body p-0">
          <!-- Vue liste -->
          <div *ngIf="viewMode === 'list'" class="table-responsive">
            <table class="table table-striped mb-0">
              <thead>
                <tr>
                  <th>Joueur</th>
                  <th>Entraînement</th>
                  <th>Catégorie</th>
                  <th>Note</th>
                  <th>Niveau</th>
                  <th>Date</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let performance of filteredPerformances">
                  <td>
                    <div class="player-info">
                      <div class="player-avatar">
                        <i class="material-icons">person</i>
                      </div>
                      <div class="player-details">
                        <strong>Joueur #{{ performance.joueurId }}</strong>
                        <div class="text-muted small">ID: {{ performance.joueurId }}</div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div class="entrainement-info">
                      <strong>{{ getEntrainementTitle(performance.entrainementId) }}</strong>
                      <div class="text-muted small">
                        {{ formatDate(getEntrainementDate(performance.entrainementId)) }}
                      </div>
                    </div>
                  </td>
                  <td>
                    <span 
                      class="badge category-badge"
                      [style.background-color]="getCategorieColor(performance.categorie)"
                    >
                      <i class="material-icons me-1">{{ getCategorieIcon(performance.categorie) }}</i>
                      {{ getCategorieLabel(performance.categorie) }}
                    </span>
                  </td>
                  <td>
                    <div class="note-display">
                      <span class="note-value" [style.color]="getNoteColor(performance.note)">
                        {{ formatNote(performance.note) }}
                      </span>
                      <div class="note-bar">
                        <div 
                          class="note-fill"
                          [style.width.%]="(performance.note / 10) * 100"
                          [style.background-color]="getNoteColor(performance.note)"
                        ></div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <span 
                      class="badge niveau-badge"
                      [style.background-color]="getNiveauColor(performance.note)"
                    >
                      {{ getNiveauLabel(performance.note) }}
                    </span>
                  </td>
                  <td>
                    <div>{{ formatDateTime(performance.dateEvaluation) }}</div>
                  </td>
                  <td>
                    <div class="btn-group btn-group-sm">
                      <a 
                        [routerLink]="['/performances', performance.id]" 
                        class="btn btn-outline-primary"
                        title="Voir détails"
                      >
                        <i class="material-icons">visibility</i>
                      </a>
                      <a 
                        [routerLink]="['/performances', performance.id, 'modifier']" 
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

          <!-- Vue cartes -->
          <div *ngIf="viewMode === 'cards'" class="performance-cards">
            <div class="row">
              <div class="col-md-6 col-lg-4 mb-3" *ngFor="let performance of filteredPerformances">
                <div class="performance-card">
                  <div class="card-header-custom">
                    <div class="player-info">
                      <div class="player-avatar">
                        <i class="material-icons">person</i>
                      </div>
                      <div class="player-details">
                        <strong>Joueur #{{ performance.joueurId }}</strong>
                        <small class="text-muted">{{ formatDateTime(performance.dateEvaluation) }}</small>
                      </div>
                    </div>
                    <div class="note-circle" [style.border-color]="getNoteColor(performance.note)">
                      <span [style.color]="getNoteColor(performance.note)">{{ formatNote(performance.note) }}</span>
                    </div>
                  </div>
                  
                  <div class="card-body-custom">
                    <div class="entrainement-title">
                      {{ getEntrainementTitle(performance.entrainementId) }}
                    </div>
                    
                    <div class="category-info">
                      <span 
                        class="badge category-badge"
                        [style.background-color]="getCategorieColor(performance.categorie)"
                      >
                        <i class="material-icons me-1">{{ getCategorieIcon(performance.categorie) }}</i>
                        {{ getCategorieLabel(performance.categorie) }}
                      </span>
                      <span 
                        class="badge niveau-badge"
                        [style.background-color]="getNiveauColor(performance.note)"
                      >
                        {{ getNiveauLabel(performance.note) }}
                      </span>
                    </div>
                    
                    <div *ngIf="performance.commentaire" class="commentaire">
                      <i class="material-icons">comment</i>
                      <span>{{ performance.commentaire | slice:0:100 }}{{ performance.commentaire.length > 100 ? '...' : '' }}</span>
                    </div>
                  </div>
                  
                  <div class="card-actions">
                    <a [routerLink]="['/performances', performance.id]" class="btn btn-sm btn-outline-primary">
                      <i class="material-icons me-1">visibility</i>
                      Détails
                    </a>
                    <a [routerLink]="['/performances', performance.id, 'modifier']" class="btn btn-sm btn-outline-secondary">
                      <i class="material-icons me-1">edit</i>
                      Modifier
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Message si aucune performance -->
          <div *ngIf="filteredPerformances.length === 0" class="text-center py-5">
            <i class="material-icons text-muted" style="font-size: 4rem;">assessment</i>
            <h5 class="text-muted mt-3">Aucune évaluation trouvée</h5>
            <p class="text-muted">Aucune évaluation ne correspond à vos critères de recherche.</p>
            <a routerLink="/performances/evaluer" class="btn btn-primary">
              <i class="material-icons me-1">add</i>
              Créer une évaluation
            </a>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .performance-list-container {
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

    .stats-section {
      margin-bottom: 1.5rem;
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

    .filter-section {
      background: white;
      border-radius: var(--border-radius);
      padding: 1.5rem;
      margin-bottom: 1.5rem;
      box-shadow: var(--box-shadow);
    }

    .categories-overview {
      margin-bottom: 1.5rem;
      
      .category-card {
        background: white;
        border-radius: var(--border-radius);
        padding: 1rem;
        box-shadow: var(--box-shadow);
        border-left: 4px solid;
        
        .category-header {
          display: flex;
          align-items: center;
          margin-bottom: 0.75rem;
          
          .material-icons {
            margin-right: 0.5rem;
            font-size: 1.5rem;
          }
          
          h6 {
            margin: 0;
            color: var(--primary-color);
          }
        }
        
        .category-stats {
          .category-average {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--primary-color);
          }
          
          .category-count {
            font-size: 0.9rem;
            color: var(--secondary-color);
          }
        }
      }
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-actions {
        display: flex;
        align-items: center;
      }
    }

    .player-info {
      display: flex;
      align-items: center;
      
      .player-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: var(--light-color);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 0.75rem;
        
        .material-icons {
          color: var(--secondary-color);
        }
      }
      
      .player-details {
        strong {
          color: var(--primary-color);
        }
      }
    }

    .entrainement-info {
      strong {
        color: var(--primary-color);
      }
    }

    .note-display {
      .note-value {
        font-size: 1.2rem;
        font-weight: 600;
      }
      
      .note-bar {
        width: 60px;
        height: 4px;
        background: #eee;
        border-radius: 2px;
        margin-top: 0.25rem;
        
        .note-fill {
          height: 100%;
          border-radius: 2px;
          transition: var(--transition);
        }
      }
    }

    .badge {
      font-size: 0.75rem;
      padding: 0.35rem 0.65rem;
      border-radius: 0.375rem;
      
      &.category-badge,
      &.niveau-badge {
        display: inline-flex;
        align-items: center;
        
        .material-icons {
          font-size: 0.9rem;
        }
      }
    }

    /* Vue cartes */
    .performance-cards {
      padding: 1rem;
    }

    .performance-card {
      background: white;
      border-radius: var(--border-radius);
      box-shadow: var(--box-shadow);
      transition: var(--transition);
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(0,0,0,0.15);
      }
      
      .card-header-custom {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        border-bottom: 1px solid #eee;
        
        .player-info {
          .player-avatar {
            width: 32px;
            height: 32px;
            margin-right: 0.5rem;
          }
        }
        
        .note-circle {
          width: 50px;
          height: 50px;
          border: 3px solid;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-weight: 700;
          font-size: 1.1rem;
        }
      }
      
      .card-body-custom {
        padding: 1rem;
        
        .entrainement-title {
          font-weight: 500;
          color: var(--primary-color);
          margin-bottom: 0.75rem;
        }
        
        .category-info {
          display: flex;
          gap: 0.5rem;
          margin-bottom: 0.75rem;
        }
        
        .commentaire {
          display: flex;
          align-items: flex-start;
          gap: 0.5rem;
          font-size: 0.9rem;
          color: var(--secondary-color);
          
          .material-icons {
            font-size: 1rem;
            margin-top: 0.1rem;
          }
        }
      }
      
      .card-actions {
        padding: 0.75rem 1rem;
        border-top: 1px solid #eee;
        display: flex;
        gap: 0.5rem;
      }
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
      
      .categories-overview .row > div {
        margin-bottom: 1rem;
      }
      
      .performance-card {
        .card-header-custom {
          flex-direction: column;
          align-items: flex-start;
          gap: 1rem;
        }
        
        .card-actions {
          flex-direction: column;
          
          .btn {
            width: 100%;
          }
        }
      }
    }
  `]
})
export class PerformanceListComponent implements OnInit {
  performances: Performance[] = [];
  filteredPerformances: Performance[] = [];
  entrainements: Entrainement[] = [];
  joueurs: any[] = []; // À remplacer par le service des joueurs
  
  viewMode: 'list' | 'cards' = 'list';
  
  filters: PerformanceFilters = {};
  
  stats = {
    totalEvaluations: 0,
    moyenneGenerale: 0,
    moyenneParCategorie: {} as { [key: string]: number },
    meilleureNote: 0,
    plusFaibleNote: 10,
    tendanceGenerale: 'stable' as 'up' | 'down' | 'stable'
  };

  categorieOptions = Object.values(CategoriePerformance).map(categorie => ({
    value: categorie,
    label: CategoriePerformanceLabels[categorie]
  }));

  constructor(
    private performanceService: PerformanceService,
    private entrainementService: EntrainementService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    // Charger les entraînements
    this.entrainementService.getAllEntrainements().subscribe({
      next: (entrainements) => {
        this.entrainements = entrainements;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des entraînements:', error);
      }
    });

    // Charger les performances
    this.loadPerformances();
    
    // Charger les statistiques
    this.loadStats();
  }

  private loadPerformances(): void {
    this.performanceService.getAllPerformances().subscribe({
      next: (performances) => {
        this.performances = performances;
        this.applyFilters();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des performances:', error);
      }
    });
  }

  private loadStats(): void {
    this.performanceService.getStatistiques().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des statistiques:', error);
      }
    });
  }

  applyFilters(): void {
    this.filteredPerformances = this.performances.filter(performance => {
      if (this.filters.joueurId && performance.joueurId !== this.filters.joueurId) {
        return false;
      }
      if (this.filters.entrainementId && performance.entrainementId !== this.filters.entrainementId) {
        return false;
      }
      if (this.filters.categorie && performance.categorie !== this.filters.categorie) {
        return false;
      }
      if (this.filters.noteMin && performance.note < this.filters.noteMin) {
        return false;
      }
      if (this.filters.noteMax && performance.note > this.filters.noteMax) {
        return false;
      }
      return true;
    });
  }

  clearFilters(): void {
    this.filters = {};
    this.applyFilters();
  }

  exportPerformances(): void {
    this.performanceService.exporterPerformances('excel', this.filters).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'performances.xlsx';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Erreur lors de l\'export:', error);
      }
    });
  }

  // Méthodes utilitaires
  getEntrainementTitle(entrainementId: number): string {
    const entrainement = this.entrainements.find(e => e.id === entrainementId);
    return entrainement?.titre || 'Entraînement inconnu';
  }

  getEntrainementDate(entrainementId: number): string {
    const entrainement = this.entrainements.find(e => e.id === entrainementId);
    return entrainement?.date || '';
  }

  getEvaluationsCountByCategorie(categorie: CategoriePerformance): number {
    return this.filteredPerformances.filter(p => p.categorie === categorie).length;
  }

  getCategorieLabel(categorie: CategoriePerformance): string {
    return CategoriePerformanceLabels[categorie];
  }

  getCategorieColor(categorie: CategoriePerformance): string {
    return CategoriePerformanceColors[categorie];
  }

  getCategorieIcon(categorie: CategoriePerformance): string {
    return CategoriePerformanceIcons[categorie];
  }

  getNoteColor(note: number): string {
    if (note >= 8) return '#4caf50';
    if (note >= 6) return '#8bc34a';
    if (note >= 4) return '#ff9800';
    return '#f44336';
  }

  getNiveauLabel(note: number): string {
    if (note >= 8) return 'Excellent';
    if (note >= 6) return 'Bon';
    if (note >= 4) return 'Moyen';
    return 'Faible';
  }

  getNiveauColor(note: number): string {
    return this.getNoteColor(note);
  }

  getTendanceLabel(tendance: string): string {
    switch (tendance) {
      case 'up': return 'En progression';
      case 'down': return 'En baisse';
      case 'stable': return 'Stable';
      default: return 'Stable';
    }
  }

  getTendanceIcon(tendance: string): string {
    switch (tendance) {
      case 'up': return 'trending_up';
      case 'down': return 'trending_down';
      case 'stable': return 'trending_flat';
      default: return 'trending_flat';
    }
  }

  getTendanceClass(tendance: string): string {
    switch (tendance) {
      case 'up': return 'success';
      case 'down': return 'danger';
      case 'stable': return 'warning';
      default: return 'secondary';
    }
  }

  formatNote(note: number): string {
    return note?.toFixed(1) || '0.0';
  }

  formatDate(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR');
  }

  formatDateTime(dateTime: string): string {
    if (!dateTime) return '';
    return new Date(dateTime).toLocaleString('fr-FR');
  }
}
