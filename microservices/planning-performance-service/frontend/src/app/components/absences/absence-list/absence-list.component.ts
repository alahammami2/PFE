import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AbsenceService } from '../../../services/absence.service';
import { EntrainementService } from '../../../services/entrainement.service';
import { 
  Absence,
  TypeAbsence,
  StatutAbsence,
  TypeAbsenceLabels,
  TypeAbsenceColors,
  TypeAbsenceIcons,
  StatutAbsenceLabels,
  StatutAbsenceColors,
  StatutAbsenceIcons,
  AbsenceFilters
} from '../../../models/absence.model';
import { Entrainement } from '../../../models/entrainement.model';

@Component({
  selector: 'app-absence-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="absence-list-container fade-in">
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <i class="material-icons me-2">event_busy</i>
            Gestion des Absences
          </h1>
          <p class="page-subtitle">Suivi et traitement des absences des joueurs</p>
        </div>
        <div class="header-actions">
          <a routerLink="/absences/declarer" class="btn btn-primary">
            <i class="material-icons me-1">add</i>
            Déclarer une Absence
          </a>
        </div>
      </div>

      <!-- Statistiques rapides -->
      <div class="stats-section">
        <div class="row">
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card stat-primary">
              <div class="stat-number">{{ stats.totalAbsences }}</div>
              <div class="stat-label">Total Absences</div>
              <i class="material-icons stat-icon">event_busy</i>
            </div>
          </div>
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card stat-warning">
              <div class="stat-number">{{ getAbsencesEnAttente() }}</div>
              <div class="stat-label">En Attente</div>
              <i class="material-icons stat-icon">schedule</i>
            </div>
          </div>
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card stat-success">
              <div class="stat-number">{{ stats.absencesJustifiees }}</div>
              <div class="stat-label">Justifiées</div>
              <i class="material-icons stat-icon">verified</i>
            </div>
          </div>
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card stat-info">
              <div class="stat-number">{{ stats.tauxAbsenteisme }}%</div>
              <div class="stat-label">Taux d'Absentéisme</div>
              <i class="material-icons stat-icon">analytics</i>
            </div>
          </div>
        </div>
      </div>

      <!-- Alertes -->
      <div *ngIf="absencesRequiringAttention.length > 0" class="alerts-section">
        <div class="alert alert-warning">
          <h6>
            <i class="material-icons me-2">warning</i>
            Absences nécessitant votre attention ({{ absencesRequiringAttention.length }})
          </h6>
          <div class="alert-items">
            <div *ngFor="let absence of absencesRequiringAttention.slice(0, 3)" class="alert-item">
              <strong>Joueur #{{ absence.joueurId }}</strong> - 
              {{ getTypeLabel(absence.type) }} 
              <span class="text-muted">({{ formatDateAbsence(absence.dateAbsence) }})</span>
            </div>
            <div *ngIf="absencesRequiringAttention.length > 3" class="text-muted">
              ... et {{ absencesRequiringAttention.length - 3 }} autres
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
            <label class="form-label">Type</label>
            <select class="form-select" [(ngModel)]="filters.type" (change)="applyFilters()">
              <option value="">Tous les types</option>
              <option *ngFor="let type of typeOptions" [value]="type.value">
                {{ type.label }}
              </option>
            </select>
          </div>
          
          <div class="col-md-2 mb-3">
            <label class="form-label">Statut</label>
            <select class="form-select" [(ngModel)]="filters.statut" (change)="applyFilters()">
              <option value="">Tous</option>
              <option *ngFor="let statut of statutOptions" [value]="statut.value">
                {{ statut.label }}
              </option>
            </select>
          </div>
          
          <div class="col-md-2 mb-3">
            <label class="form-label">Justifiée</label>
            <select class="form-select" [(ngModel)]="filters.justifiee" (change)="applyFilters()">
              <option value="">Toutes</option>
              <option value="true">Oui</option>
              <option value="false">Non</option>
            </select>
          </div>
          
          <div class="col-md-2 mb-3 d-flex align-items-end">
            <button class="btn btn-outline-secondary w-100" (click)="clearFilters()">
              <i class="material-icons me-1">clear</i>
              Effacer
            </button>
          </div>
        </div>
      </div>

      <!-- Vue par type -->
      <div class="types-overview">
        <div class="row">
          <div class="col-md-4 col-sm-6 mb-3" *ngFor="let type of typeOptions">
            <div class="type-card" [style.border-left-color]="getTypeColor(type.value)">
              <div class="type-header">
                <i class="material-icons" [style.color]="getTypeColor(type.value)">
                  {{ getTypeIcon(type.value) }}
                </i>
                <h6>{{ type.label }}</h6>
              </div>
              <div class="type-stats">
                <div class="type-count">
                  {{ getAbsencesCountByType(type.value) }}
                </div>
                <div class="type-label">absences</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Liste des absences -->
      <div class="card">
        <div class="card-header">
          <h5 class="mb-0">
            <i class="material-icons me-2">list</i>
            Absences ({{ filteredAbsences.length }})
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
            <button class="btn btn-sm btn-outline-primary" (click)="exportAbsences()">
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
                  <th>Type</th>
                  <th>Date Absence</th>
                  <th>Statut</th>
                  <th>Justifiée</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let absence of filteredAbsences">
                  <td>
                    <div class="player-info">
                      <div class="player-avatar">
                        <i class="material-icons">person</i>
                      </div>
                      <div class="player-details">
                        <strong>Joueur #{{ absence.joueurId }}</strong>
                        <div class="text-muted small">ID: {{ absence.joueurId }}</div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div class="entrainement-info">
                      <strong>{{ getEntrainementTitle(absence.entrainementId) }}</strong>
                      <div class="text-muted small">
                        {{ formatDate(getEntrainementDate(absence.entrainementId)) }}
                      </div>
                    </div>
                  </td>
                  <td>
                    <span 
                      class="badge type-badge"
                      [style.background-color]="getTypeColor(absence.type)"
                    >
                      <i class="material-icons me-1">{{ getTypeIcon(absence.type) }}</i>
                      {{ getTypeLabel(absence.type) }}
                    </span>
                  </td>
                  <td>
                    <div>{{ formatDateAbsence(absence.dateAbsence) }}</div>
                    <div class="text-muted small">
                      Déclarée: {{ formatDateTime(absence.dateDeclaration) }}
                    </div>
                  </td>
                  <td>
                    <span 
                      class="badge statut-badge"
                      [style.background-color]="getStatutColor(absence.statut)"
                    >
                      <i class="material-icons me-1">{{ getStatutIcon(absence.statut) }}</i>
                      {{ getStatutLabel(absence.statut) }}
                    </span>
                  </td>
                  <td>
                    <div class="justification-info">
                      <span 
                        class="badge"
                        [class.bg-success]="absence.justifiee"
                        [class.bg-secondary]="!absence.justifiee"
                      >
                        {{ absence.justifiee ? 'Oui' : 'Non' }}
                      </span>
                      <div *ngIf="absence.justificatifUrl" class="mt-1">
                        <a href="#" class="text-primary small" (click)="downloadJustificatif(absence.id!)">
                          <i class="material-icons me-1" style="font-size: 14px;">attachment</i>
                          Justificatif
                        </a>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div class="btn-group btn-group-sm">
                      <a 
                        [routerLink]="['/absences', absence.id]" 
                        class="btn btn-outline-primary"
                        title="Voir détails"
                      >
                        <i class="material-icons">visibility</i>
                      </a>
                      <button 
                        *ngIf="canApproveAbsence(absence)"
                        class="btn btn-outline-success"
                        (click)="approuverAbsence(absence.id!)"
                        title="Approuver"
                      >
                        <i class="material-icons">check</i>
                      </button>
                      <button 
                        *ngIf="canApproveAbsence(absence)"
                        class="btn btn-outline-danger"
                        (click)="rejeterAbsence(absence.id!)"
                        title="Rejeter"
                      >
                        <i class="material-icons">close</i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Vue cartes -->
          <div *ngIf="viewMode === 'cards'" class="absence-cards">
            <div class="row">
              <div class="col-md-6 col-lg-4 mb-3" *ngFor="let absence of filteredAbsences">
                <div class="absence-card">
                  <div class="card-header-custom">
                    <div class="player-info">
                      <div class="player-avatar">
                        <i class="material-icons">person</i>
                      </div>
                      <div class="player-details">
                        <strong>Joueur #{{ absence.joueurId }}</strong>
                        <small class="text-muted">{{ formatDateTime(absence.dateDeclaration) }}</small>
                      </div>
                    </div>
                    <div class="absence-priority" [class]="'priority-' + getAbsencePriority(absence)">
                      <i class="material-icons">{{ getPriorityIcon(getAbsencePriority(absence)) }}</i>
                    </div>
                  </div>
                  
                  <div class="card-body-custom">
                    <div class="absence-date">
                      <strong>{{ formatDateAbsence(absence.dateAbsence) }}</strong>
                    </div>
                    
                    <div class="absence-details">
                      <span 
                        class="badge type-badge"
                        [style.background-color]="getTypeColor(absence.type)"
                      >
                        <i class="material-icons me-1">{{ getTypeIcon(absence.type) }}</i>
                        {{ getTypeLabel(absence.type) }}
                      </span>
                      <span 
                        class="badge statut-badge"
                        [style.background-color]="getStatutColor(absence.statut)"
                      >
                        {{ getStatutLabel(absence.statut) }}
                      </span>
                    </div>
                    
                    <div class="entrainement-title">
                      {{ getEntrainementTitle(absence.entrainementId) }}
                    </div>
                    
                    <div *ngIf="absence.motif" class="motif">
                      <i class="material-icons">comment</i>
                      <span>{{ absence.motif | slice:0:80 }}{{ absence.motif.length > 80 ? '...' : '' }}</span>
                    </div>
                    
                    <div class="justification-status">
                      <span 
                        class="badge"
                        [class.bg-success]="absence.justifiee"
                        [class.bg-secondary]="!absence.justifiee"
                      >
                        <i class="material-icons me-1">{{ absence.justifiee ? 'verified' : 'help_outline' }}</i>
                        {{ absence.justifiee ? 'Justifiée' : 'Non justifiée' }}
                      </span>
                    </div>
                  </div>
                  
                  <div class="card-actions">
                    <a [routerLink]="['/absences', absence.id]" class="btn btn-sm btn-outline-primary">
                      <i class="material-icons me-1">visibility</i>
                      Détails
                    </a>
                    <button 
                      *ngIf="canApproveAbsence(absence)"
                      class="btn btn-sm btn-outline-success"
                      (click)="approuverAbsence(absence.id!)"
                    >
                      <i class="material-icons me-1">check</i>
                      Approuver
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Message si aucune absence -->
          <div *ngIf="filteredAbsences.length === 0" class="text-center py-5">
            <i class="material-icons text-muted" style="font-size: 4rem;">event_available</i>
            <h5 class="text-muted mt-3">Aucune absence trouvée</h5>
            <p class="text-muted">Aucune absence ne correspond à vos critères de recherche.</p>
            <a routerLink="/absences/declarer" class="btn btn-primary">
              <i class="material-icons me-1">add</i>
              Déclarer une absence
            </a>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .absence-list-container {
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

    .alerts-section {
      margin-bottom: 1.5rem;
      
      .alert-items {
        margin-top: 0.75rem;
        
        .alert-item {
          padding: 0.25rem 0;
          border-bottom: 1px solid rgba(0,0,0,0.1);
          
          &:last-child {
            border-bottom: none;
          }
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

    .types-overview {
      margin-bottom: 1.5rem;
      
      .type-card {
        background: white;
        border-radius: var(--border-radius);
        padding: 1rem;
        box-shadow: var(--box-shadow);
        border-left: 4px solid;
        
        .type-header {
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
        
        .type-stats {
          text-align: center;
          
          .type-count {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--primary-color);
          }
          
          .type-label {
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

    .justification-info {
      text-align: center;
    }

    .badge {
      font-size: 0.75rem;
      padding: 0.35rem 0.65rem;
      border-radius: 0.375rem;
      
      &.type-badge,
      &.statut-badge {
        display: inline-flex;
        align-items: center;
        
        .material-icons {
          font-size: 0.9rem;
        }
      }
    }

    /* Vue cartes */
    .absence-cards {
      padding: 1rem;
    }

    .absence-card {
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
        
        .absence-priority {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          
          &.priority-high {
            background: rgba(244, 67, 54, 0.1);
            color: #f44336;
          }
          
          &.priority-medium {
            background: rgba(255, 152, 0, 0.1);
            color: #ff9800;
          }
          
          &.priority-low {
            background: rgba(76, 175, 80, 0.1);
            color: #4caf50;
          }
        }
      }
      
      .card-body-custom {
        padding: 1rem;
        
        .absence-date {
          font-weight: 500;
          color: var(--primary-color);
          margin-bottom: 0.75rem;
        }
        
        .absence-details {
          display: flex;
          gap: 0.5rem;
          margin-bottom: 0.75rem;
          flex-wrap: wrap;
        }
        
        .entrainement-title {
          font-size: 0.9rem;
          color: var(--secondary-color);
          margin-bottom: 0.75rem;
        }
        
        .motif {
          display: flex;
          align-items: flex-start;
          gap: 0.5rem;
          font-size: 0.9rem;
          color: var(--secondary-color);
          margin-bottom: 0.75rem;
          
          .material-icons {
            font-size: 1rem;
            margin-top: 0.1rem;
          }
        }
        
        .justification-status {
          text-align: center;
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
      
      .types-overview .row > div {
        margin-bottom: 1rem;
      }
      
      .absence-card {
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
export class AbsenceListComponent implements OnInit {
  absences: Absence[] = [];
  filteredAbsences: Absence[] = [];
  entrainements: Entrainement[] = [];
  joueurs: any[] = []; // À remplacer par le service des joueurs
  absencesRequiringAttention: Absence[] = [];
  
  viewMode: 'list' | 'cards' = 'list';
  
  filters: AbsenceFilters = {};
  
  stats = {
    totalAbsences: 0,
    absencesJustifiees: 0,
    absencesNonJustifiees: 0,
    tauxAbsenteisme: 0,
    repartitionParType: {} as { [key: string]: number },
    repartitionParStatut: {} as { [key: string]: number }
  };

  typeOptions = Object.values(TypeAbsence).map(type => ({
    value: type,
    label: TypeAbsenceLabels[type]
  }));

  statutOptions = Object.values(StatutAbsence).map(statut => ({
    value: statut,
    label: StatutAbsenceLabels[statut]
  }));

  constructor(
    private absenceService: AbsenceService,
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

    // Simuler le chargement des joueurs
    this.joueurs = [
      { id: 1, nom: 'Joueur 1' },
      { id: 2, nom: 'Joueur 2' },
      { id: 3, nom: 'Joueur 3' }
    ];

    // Charger les absences
    this.loadAbsences();
    
    // Charger les statistiques
    this.loadStats();
    
    // Charger les absences nécessitant attention
    this.loadAbsencesRequiringAttention();
  }

  private loadAbsences(): void {
    this.absenceService.getAllAbsences().subscribe({
      next: (absences) => {
        this.absences = absences;
        this.applyFilters();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des absences:', error);
      }
    });
  }

  private loadStats(): void {
    this.absenceService.getStatistiques().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des statistiques:', error);
      }
    });
  }

  private loadAbsencesRequiringAttention(): void {
    this.absenceService.getAbsencesEnAttente().subscribe({
      next: (absences) => {
        this.absencesRequiringAttention = absences;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des absences en attente:', error);
      }
    });
  }

  applyFilters(): void {
    this.filteredAbsences = this.absences.filter(absence => {
      if (this.filters.joueurId && absence.joueurId !== this.filters.joueurId) {
        return false;
      }
      if (this.filters.entrainementId && absence.entrainementId !== this.filters.entrainementId) {
        return false;
      }
      if (this.filters.type && absence.type !== this.filters.type) {
        return false;
      }
      if (this.filters.statut && absence.statut !== this.filters.statut) {
        return false;
      }
      if (this.filters.justifiee !== undefined && absence.justifiee !== (this.filters.justifiee === 'true')) {
        return false;
      }
      return true;
    });
  }

  clearFilters(): void {
    this.filters = {};
    this.applyFilters();
  }

  approuverAbsence(absenceId: number): void {
    this.absenceService.approuverAbsence(absenceId).subscribe({
      next: () => {
        this.loadAbsences();
        this.loadAbsencesRequiringAttention();
      },
      error: (error) => {
        console.error('Erreur lors de l\'approbation:', error);
      }
    });
  }

  rejeterAbsence(absenceId: number): void {
    const commentaire = prompt('Commentaire de rejet (optionnel):');
    this.absenceService.rejeterAbsence(absenceId, commentaire || '').subscribe({
      next: () => {
        this.loadAbsences();
        this.loadAbsencesRequiringAttention();
      },
      error: (error) => {
        console.error('Erreur lors du rejet:', error);
      }
    });
  }

  downloadJustificatif(absenceId: number): void {
    this.absenceService.downloadJustificatif(absenceId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'justificatif.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Erreur lors du téléchargement:', error);
      }
    });
  }

  exportAbsences(): void {
    this.absenceService.exporterAbsences('excel', this.filters).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'absences.xlsx';
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

  getAbsencesEnAttente(): number {
    return this.absences.filter(a => a.statut === StatutAbsence.EN_ATTENTE).length;
  }

  getAbsencesCountByType(type: TypeAbsence): number {
    return this.filteredAbsences.filter(a => a.type === type).length;
  }

  canApproveAbsence(absence: Absence): boolean {
    return absence.statut === StatutAbsence.EN_ATTENTE;
  }

  getAbsencePriority(absence: Absence): 'high' | 'medium' | 'low' {
    if (absence.type === TypeAbsence.DERNIERE_MINUTE) return 'high';
    if (absence.type === TypeAbsence.MALADIE || absence.type === TypeAbsence.BLESSURE) return 'medium';
    return 'low';
  }

  getPriorityIcon(priority: string): string {
    switch (priority) {
      case 'high': return 'priority_high';
      case 'medium': return 'remove';
      case 'low': return 'keyboard_arrow_down';
      default: return 'help';
    }
  }

  getTypeLabel(type: TypeAbsence): string {
    return TypeAbsenceLabels[type];
  }

  getTypeColor(type: TypeAbsence): string {
    return TypeAbsenceColors[type];
  }

  getTypeIcon(type: TypeAbsence): string {
    return TypeAbsenceIcons[type];
  }

  getStatutLabel(statut: StatutAbsence): string {
    return StatutAbsenceLabels[statut];
  }

  getStatutColor(statut: StatutAbsence): string {
    return StatutAbsenceColors[statut];
  }

  getStatutIcon(statut: StatutAbsence): string {
    return StatutAbsenceIcons[statut];
  }

  formatDate(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR');
  }

  formatDateTime(dateTime: string): string {
    if (!dateTime) return '';
    return new Date(dateTime).toLocaleString('fr-FR');
  }

  formatDateAbsence(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}
