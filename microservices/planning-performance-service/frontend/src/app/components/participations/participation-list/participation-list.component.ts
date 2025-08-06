import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParticipationService } from '../../../services/participation.service';
import { EntrainementService } from '../../../services/entrainement.service';
import { 
  Participation,
  StatutParticipation,
  StatutParticipationLabels,
  StatutParticipationColors
} from '../../../models/participation.model';
import { Entrainement } from '../../../models/entrainement.model';

@Component({
  selector: 'app-participation-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="participation-list-container fade-in">
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <i class="material-icons me-2">how_to_reg</i>
            Gestion des Participations
          </h1>
          <p class="page-subtitle">Suivez les inscriptions et présences aux entraînements</p>
        </div>
        <div class="header-actions">
          <button class="btn btn-outline-primary" (click)="refreshData()">
            <i class="material-icons me-1">refresh</i>
            Actualiser
          </button>
        </div>
      </div>

      <!-- Filtres -->
      <div class="filter-section">
        <div class="row">
          <div class="col-md-4 mb-3">
            <label class="form-label">Entraînement</label>
            <select class="form-select" [(ngModel)]="selectedEntrainementId" (change)="applyFilters()">
              <option value="">Tous les entraînements</option>
              <option *ngFor="let entrainement of entrainements" [value]="entrainement.id">
                {{ entrainement.titre }} - {{ formatDate(entrainement.date) }}
              </option>
            </select>
          </div>
          
          <div class="col-md-3 mb-3">
            <label class="form-label">Statut de participation</label>
            <select class="form-select" [(ngModel)]="selectedStatut" (change)="applyFilters()">
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
              [(ngModel)]="dateDebut"
              (change)="applyFilters()"
            >
          </div>
          
          <div class="col-md-2 mb-3 d-flex align-items-end">
            <button class="btn btn-outline-secondary" (click)="clearFilters()">
              <i class="material-icons me-1">clear</i>
              Effacer
            </button>
          </div>
        </div>
      </div>

      <!-- Statistiques rapides -->
      <div class="stats-section">
        <div class="row">
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card stat-primary">
              <div class="stat-number">{{ stats.totalParticipations }}</div>
              <div class="stat-label">Total Participations</div>
              <i class="material-icons stat-icon">people</i>
            </div>
          </div>
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card stat-success">
              <div class="stat-number">{{ stats.presences }}</div>
              <div class="stat-label">Présences</div>
              <i class="material-icons stat-icon">check_circle</i>
            </div>
          </div>
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card stat-danger">
              <div class="stat-number">{{ stats.absences }}</div>
              <div class="stat-label">Absences</div>
              <i class="material-icons stat-icon">cancel</i>
            </div>
          </div>
          <div class="col-md-3 col-sm-6 mb-3">
            <div class="stat-card stat-info">
              <div class="stat-number">{{ formatPercentage(stats.tauxPresence) }}</div>
              <div class="stat-label">Taux de Présence</div>
              <i class="material-icons stat-icon">trending_up</i>
            </div>
          </div>
        </div>
      </div>

      <!-- Liste des participations -->
      <div class="card">
        <div class="card-header">
          <h5 class="mb-0">
            <i class="material-icons me-2">list</i>
            Participations ({{ filteredParticipations.length }})
          </h5>
          <div class="header-actions">
            <button class="btn btn-sm btn-outline-primary" (click)="exportParticipations()">
              <i class="material-icons me-1">download</i>
              Exporter
            </button>
          </div>
        </div>
        
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>Joueur</th>
                  <th>Entraînement</th>
                  <th>Date</th>
                  <th>Statut</th>
                  <th>Heure d'inscription</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let participation of filteredParticipations">
                  <td>
                    <div class="player-info">
                      <div class="player-avatar">
                        <i class="material-icons">person</i>
                      </div>
                      <div class="player-details">
                        <strong>Joueur #{{ participation.joueurId }}</strong>
                        <div class="text-muted small">ID: {{ participation.joueurId }}</div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div class="entrainement-info">
                      <strong>{{ getEntrainementTitle(participation.entrainementId) }}</strong>
                      <div class="text-muted small">
                        {{ getEntrainementTime(participation.entrainementId) }}
                      </div>
                    </div>
                  </td>
                  <td>
                    <div>{{ formatDate(getEntrainementDate(participation.entrainementId)) }}</div>
                  </td>
                  <td>
                    <span 
                      class="badge"
                      [style.background-color]="getStatutColor(participation.statut)"
                    >
                      {{ getStatutLabel(participation.statut) }}
                    </span>
                  </td>
                  <td>
                    <div>{{ formatDateTime(participation.heureInscription) }}</div>
                  </td>
                  <td>
                    <div class="btn-group btn-group-sm">
                      <button 
                        *ngIf="canMarkPresent(participation)"
                        (click)="markPresent(participation.id!)"
                        class="btn btn-success"
                        title="Marquer présent"
                      >
                        <i class="material-icons">check</i>
                      </button>
                      
                      <button 
                        *ngIf="canMarkAbsent(participation)"
                        (click)="markAbsent(participation.id!)"
                        class="btn btn-danger"
                        title="Marquer absent"
                      >
                        <i class="material-icons">close</i>
                      </button>
                      
                      <a 
                        [routerLink]="['/participations/presence', participation.entrainementId]" 
                        class="btn btn-outline-primary"
                        title="Gérer les présences"
                      >
                        <i class="material-icons">how_to_reg</i>
                      </a>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Message si aucune participation -->
          <div *ngIf="filteredParticipations.length === 0" class="text-center py-5">
            <i class="material-icons text-muted" style="font-size: 4rem;">people_outline</i>
            <h5 class="text-muted mt-3">Aucune participation trouvée</h5>
            <p class="text-muted">Aucune participation ne correspond à vos critères de recherche.</p>
          </div>
        </div>
      </div>

      <!-- Entraînements nécessitant une attention -->
      <div class="card mt-4">
        <div class="card-header">
          <h5 class="mb-0">
            <i class="material-icons me-2">warning</i>
            Entraînements nécessitant une attention
          </h5>
        </div>
        <div class="card-body">
          <div *ngIf="entrainementsAttention.length === 0" class="text-center py-3">
            <i class="material-icons text-success" style="font-size: 2rem;">check_circle</i>
            <p class="text-success mt-2 mb-0">Toutes les présences sont à jour !</p>
          </div>
          
          <div *ngFor="let entrainement of entrainementsAttention" class="attention-item">
            <div class="attention-info">
              <h6 class="attention-title">{{ entrainement.titre }}</h6>
              <div class="attention-details">
                <span class="text-muted">{{ formatDate(entrainement.date) }} à {{ entrainement.heureDebut }}</span>
                <span class="badge bg-warning ms-2">{{ getAttentionReason(entrainement) }}</span>
              </div>
            </div>
            <div class="attention-actions">
              <a 
                [routerLink]="['/participations/presence', entrainement.id]" 
                class="btn btn-sm btn-primary"
              >
                <i class="material-icons me-1">how_to_reg</i>
                Gérer les présences
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .participation-list-container {
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

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
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

    .badge {
      font-size: 0.75rem;
      padding: 0.35rem 0.65rem;
      border-radius: 0.375rem;
    }

    .attention-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem;
      border: 1px solid #ffc107;
      border-radius: var(--border-radius);
      background: rgba(255, 193, 7, 0.1);
      margin-bottom: 1rem;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .attention-info {
        flex: 1;
        
        .attention-title {
          margin: 0 0 0.25rem 0;
          color: var(--primary-color);
          font-weight: 500;
        }
        
        .attention-details {
          display: flex;
          align-items: center;
          flex-wrap: wrap;
          gap: 0.5rem;
        }
      }
      
      .attention-actions {
        margin-left: 1rem;
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
      
      .attention-item {
        flex-direction: column;
        align-items: flex-start;
        
        .attention-actions {
          margin-left: 0;
          margin-top: 1rem;
          width: 100%;
        }
      }
      
      .player-info {
        .player-avatar {
          width: 32px;
          height: 32px;
          margin-right: 0.5rem;
        }
      }
    }
  `]
})
export class ParticipationListComponent implements OnInit {
  participations: Participation[] = [];
  filteredParticipations: Participation[] = [];
  entrainements: Entrainement[] = [];
  entrainementsAttention: Entrainement[] = [];
  
  selectedEntrainementId = '';
  selectedStatut = '';
  dateDebut = '';
  
  stats = {
    totalParticipations: 0,
    presences: 0,
    absences: 0,
    tauxPresence: 0
  };

  statutOptions = Object.values(StatutParticipation).map(statut => ({
    value: statut,
    label: StatutParticipationLabels[statut]
  }));

  constructor(
    private participationService: ParticipationService,
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
        this.checkEntrainementsAttention();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des entraînements:', error);
      }
    });

    // Charger les participations
    this.loadParticipations();
  }

  private loadParticipations(): void {
    this.participationService.getAllParticipations().subscribe({
      next: (participations) => {
        this.participations = participations;
        this.applyFilters();
        this.calculateStats();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des participations:', error);
      }
    });
  }

  applyFilters(): void {
    this.filteredParticipations = this.participations.filter(participation => {
      if (this.selectedEntrainementId && participation.entrainementId.toString() !== this.selectedEntrainementId) {
        return false;
      }
      if (this.selectedStatut && participation.statut !== this.selectedStatut) {
        return false;
      }
      if (this.dateDebut) {
        const entrainementDate = this.getEntrainementDate(participation.entrainementId);
        if (entrainementDate && entrainementDate < this.dateDebut) {
          return false;
        }
      }
      return true;
    });
    
    this.calculateStats();
  }

  clearFilters(): void {
    this.selectedEntrainementId = '';
    this.selectedStatut = '';
    this.dateDebut = '';
    this.applyFilters();
  }

  refreshData(): void {
    this.loadData();
  }

  private calculateStats(): void {
    this.stats.totalParticipations = this.filteredParticipations.length;
    this.stats.presences = this.filteredParticipations.filter(p => p.statut === StatutParticipation.PRESENT).length;
    this.stats.absences = this.filteredParticipations.filter(p => p.statut === StatutParticipation.ABSENT).length;
    
    if (this.stats.totalParticipations > 0) {
      this.stats.tauxPresence = (this.stats.presences / this.stats.totalParticipations) * 100;
    } else {
      this.stats.tauxPresence = 0;
    }
  }

  private checkEntrainementsAttention(): void {
    const now = new Date();
    this.entrainementsAttention = this.entrainements.filter(entrainement => {
      const entrainementDate = new Date(entrainement.date);
      const isFinished = entrainement.statut === 'TERMINE';
      const hasParticipations = this.participations.some(p => p.entrainementId === entrainement.id);
      
      // Entraînements terminés sans participations marquées
      return isFinished && !hasParticipations;
    });
  }

  markPresent(participationId: number): void {
    this.participationService.marquerPresence(participationId).subscribe({
      next: () => {
        this.loadParticipations();
      },
      error: (error) => {
        console.error('Erreur lors du marquage de présence:', error);
      }
    });
  }

  markAbsent(participationId: number): void {
    this.participationService.marquerAbsence(participationId).subscribe({
      next: () => {
        this.loadParticipations();
      },
      error: (error) => {
        console.error('Erreur lors du marquage d\'absence:', error);
      }
    });
  }

  exportParticipations(): void {
    // Logique d'export à implémenter
    console.log('Export des participations');
  }

  // Méthodes de validation
  canMarkPresent(participation: Participation): boolean {
    return participation.statut === StatutParticipation.INSCRIT;
  }

  canMarkAbsent(participation: Participation): boolean {
    return participation.statut === StatutParticipation.INSCRIT || participation.statut === StatutParticipation.PRESENT;
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

  getEntrainementTime(entrainementId: number): string {
    const entrainement = this.entrainements.find(e => e.id === entrainementId);
    return entrainement ? `${entrainement.heureDebut} - ${entrainement.heureFin}` : '';
  }

  getAttentionReason(entrainement: Entrainement): string {
    return 'Présences non marquées';
  }

  getStatutLabel(statut: StatutParticipation): string {
    return StatutParticipationLabels[statut];
  }

  getStatutColor(statut: StatutParticipation): string {
    return StatutParticipationColors[statut];
  }

  formatDate(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR');
  }

  formatDateTime(dateTime: string): string {
    if (!dateTime) return '';
    return new Date(dateTime).toLocaleString('fr-FR');
  }

  formatPercentage(value: number): string {
    return `${Math.round(value)}%`;
  }
}
