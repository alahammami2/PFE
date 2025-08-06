import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParticipationService } from '../../../services/participation.service';
import { EntrainementService } from '../../../services/entrainement.service';
import { 
  Participation,
  StatutParticipation,
  StatutParticipationLabels,
  StatutParticipationColors,
  StatutParticipationIcons
} from '../../../models/participation.model';
import { Entrainement } from '../../../models/entrainement.model';

interface PresenceItem {
  participation: Participation;
  joueurNom: string;
  nouveauStatut: StatutParticipation;
  commentaire: string;
  heureArrivee: string;
  modifie: boolean;
}

@Component({
  selector: 'app-presence-form',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="presence-form-container fade-in">
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <i class="material-icons me-2">how_to_reg</i>
            Gestion des Présences
          </h1>
          <div *ngIf="entrainement" class="entrainement-info">
            <h5>{{ entrainement.titre }}</h5>
            <p class="text-muted">
              {{ formatDate(entrainement.date) }} de {{ entrainement.heureDebut }} à {{ entrainement.heureFin }}
              <span class="ms-2">📍 {{ entrainement.lieu }}</span>
            </p>
          </div>
        </div>
        <div class="header-actions">
          <button class="btn btn-outline-secondary" (click)="goBack()">
            <i class="material-icons me-1">arrow_back</i>
            Retour
          </button>
          <button 
            class="btn btn-success" 
            (click)="sauvegarderPresences()"
            [disabled]="!hasChanges() || saving"
          >
            <i class="material-icons me-1">save</i>
            {{ saving ? 'Sauvegarde...' : 'Sauvegarder' }}
          </button>
        </div>
      </div>

      <!-- Actions rapides -->
      <div class="quick-actions">
        <div class="row">
          <div class="col-md-3 col-sm-6 mb-2">
            <button class="btn btn-outline-success w-100" (click)="marquerTousPresents()">
              <i class="material-icons me-1">check_circle</i>
              Tous présents
            </button>
          </div>
          <div class="col-md-3 col-sm-6 mb-2">
            <button class="btn btn-outline-danger w-100" (click)="marquerTousAbsents()">
              <i class="material-icons me-1">cancel</i>
              Tous absents
            </button>
          </div>
          <div class="col-md-3 col-sm-6 mb-2">
            <button class="btn btn-outline-warning w-100" (click)="resetChanges()">
              <i class="material-icons me-1">refresh</i>
              Réinitialiser
            </button>
          </div>
          <div class="col-md-3 col-sm-6 mb-2">
            <button class="btn btn-outline-info w-100" (click)="toggleSelectMode()">
              <i class="material-icons me-1">{{ selectMode ? 'close' : 'check_box' }}</i>
              {{ selectMode ? 'Annuler' : 'Sélection' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Statistiques en temps réel -->
      <div class="stats-preview">
        <div class="row">
          <div class="col-md-3 col-6">
            <div class="stat-item stat-primary">
              <div class="stat-number">{{ getTotalInscrits() }}</div>
              <div class="stat-label">Inscrits</div>
            </div>
          </div>
          <div class="col-md-3 col-6">
            <div class="stat-item stat-success">
              <div class="stat-number">{{ getTotalPresents() }}</div>
              <div class="stat-label">Présents</div>
            </div>
          </div>
          <div class="col-md-3 col-6">
            <div class="stat-item stat-danger">
              <div class="stat-number">{{ getTotalAbsents() }}</div>
              <div class="stat-label">Absents</div>
            </div>
          </div>
          <div class="col-md-3 col-6">
            <div class="stat-item stat-info">
              <div class="stat-number">{{ getTauxPresence() }}%</div>
              <div class="stat-label">Taux présence</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Filtres et recherche -->
      <div class="filters-section">
        <div class="row align-items-end">
          <div class="col-md-4 mb-3">
            <label class="form-label">Rechercher un joueur</label>
            <input 
              type="text" 
              class="form-control" 
              [(ngModel)]="searchTerm"
              placeholder="Nom du joueur..."
              (input)="applyFilters()"
            >
          </div>
          <div class="col-md-3 mb-3">
            <label class="form-label">Filtrer par statut</label>
            <select class="form-select" [(ngModel)]="filterStatut" (change)="applyFilters()">
              <option value="">Tous les statuts</option>
              <option *ngFor="let statut of statutOptions" [value]="statut.value">
                {{ statut.label }}
              </option>
            </select>
          </div>
          <div class="col-md-3 mb-3">
            <label class="form-label">Trier par</label>
            <select class="form-select" [(ngModel)]="sortBy" (change)="applySort()">
              <option value="nom">Nom du joueur</option>
              <option value="statut">Statut</option>
              <option value="heure">Heure d'arrivée</option>
            </select>
          </div>
          <div class="col-md-2 mb-3">
            <button class="btn btn-outline-secondary w-100" (click)="clearFilters()">
              <i class="material-icons">clear</i>
            </button>
          </div>
        </div>
      </div>

      <!-- Liste des participations -->
      <div class="presence-list">
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">
              Participants ({{ filteredPresenceItems.length }})
            </h5>
            <div *ngIf="selectMode" class="selection-actions">
              <button class="btn btn-sm btn-success me-2" (click)="marquerSelectionPresents()">
                <i class="material-icons me-1">check</i>
                Présents ({{ selectedItems.length }})
              </button>
              <button class="btn btn-sm btn-danger" (click)="marquerSelectionAbsents()">
                <i class="material-icons me-1">close</i>
                Absents ({{ selectedItems.length }})
              </button>
            </div>
          </div>
          
          <div class="card-body p-0">
            <div class="presence-item" *ngFor="let item of filteredPresenceItems; trackBy: trackByParticipation">
              <div class="presence-checkbox" *ngIf="selectMode">
                <input 
                  type="checkbox" 
                  class="form-check-input"
                  [checked]="isSelected(item)"
                  (change)="toggleSelection(item)"
                >
              </div>
              
              <div class="player-info">
                <div class="player-avatar">
                  <i class="material-icons">person</i>
                </div>
                <div class="player-details">
                  <strong>{{ item.joueurNom }}</strong>
                  <div class="text-muted small">ID: {{ item.participation.joueurId }}</div>
                </div>
              </div>
              
              <div class="presence-controls">
                <div class="status-buttons">
                  <button 
                    *ngFor="let statut of statutOptions"
                    class="btn btn-sm status-btn"
                    [class.active]="item.nouveauStatut === statut.value"
                    [style.background-color]="item.nouveauStatut === statut.value ? getStatutColor(statut.value) : ''"
                    [style.color]="item.nouveauStatut === statut.value ? 'white' : ''"
                    (click)="changerStatut(item, statut.value)"
                  >
                    <i class="material-icons">{{ getStatutIcon(statut.value) }}</i>
                    {{ statut.label }}
                  </button>
                </div>
                
                <div class="time-input" *ngIf="item.nouveauStatut === 'PRESENT'">
                  <label class="form-label small">Heure d'arrivée</label>
                  <input 
                    type="time" 
                    class="form-control form-control-sm"
                    [(ngModel)]="item.heureArrivee"
                    (change)="markAsModified(item)"
                  >
                </div>
                
                <div class="comment-input">
                  <label class="form-label small">Commentaire</label>
                  <textarea 
                    class="form-control form-control-sm"
                    rows="2"
                    [(ngModel)]="item.commentaire"
                    (input)="markAsModified(item)"
                    placeholder="Commentaire optionnel..."
                  ></textarea>
                </div>
              </div>
              
              <div class="presence-status">
                <span 
                  class="badge status-badge"
                  [style.background-color]="getStatutColor(item.nouveauStatut)"
                >
                  <i class="material-icons me-1">{{ getStatutIcon(item.nouveauStatut) }}</i>
                  {{ getStatutLabel(item.nouveauStatut) }}
                </span>
                
                <div *ngIf="item.modifie" class="modification-indicator">
                  <i class="material-icons text-warning">edit</i>
                  <small class="text-warning">Modifié</small>
                </div>
              </div>
            </div>
            
            <!-- Message si aucun participant -->
            <div *ngIf="filteredPresenceItems.length === 0" class="text-center py-5">
              <i class="material-icons text-muted" style="font-size: 4rem;">people_outline</i>
              <h5 class="text-muted mt-3">Aucun participant trouvé</h5>
              <p class="text-muted">Aucun participant ne correspond à vos critères de recherche.</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Résumé des modifications -->
      <div *ngIf="hasChanges()" class="changes-summary">
        <div class="alert alert-info">
          <h6>
            <i class="material-icons me-2">info</i>
            Résumé des modifications
          </h6>
          <div class="changes-list">
            <div *ngFor="let item of getModifiedItems()" class="change-item">
              <strong>{{ item.joueurNom }}</strong> : 
              {{ getStatutLabel(item.participation.statut) }} → 
              <span [style.color]="getStatutColor(item.nouveauStatut)">
                {{ getStatutLabel(item.nouveauStatut) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .presence-form-container {
      max-width: 1200px;
      margin: 0 auto;
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 2rem;
      
      .header-content {
        flex: 1;
        
        .page-title {
          color: var(--primary-color);
          font-weight: 600;
          margin-bottom: 1rem;
          display: flex;
          align-items: center;
        }
        
        .entrainement-info {
          h5 {
            color: var(--primary-color);
            margin-bottom: 0.5rem;
          }
        }
      }
      
      .header-actions {
        display: flex;
        gap: 0.75rem;
      }
    }

    .quick-actions {
      margin-bottom: 1.5rem;
    }

    .stats-preview {
      margin-bottom: 1.5rem;
      
      .stat-item {
        text-align: center;
        padding: 1rem;
        border-radius: var(--border-radius);
        
        .stat-number {
          font-size: 1.5rem;
          font-weight: 700;
          margin-bottom: 0.25rem;
        }
        
        .stat-label {
          font-size: 0.9rem;
          opacity: 0.8;
        }
      }
    }

    .filters-section {
      background: white;
      border-radius: var(--border-radius);
      padding: 1.5rem;
      margin-bottom: 1.5rem;
      box-shadow: var(--box-shadow);
    }

    .presence-list {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .selection-actions {
          display: flex;
          gap: 0.5rem;
        }
      }
    }

    .presence-item {
      display: flex;
      align-items: center;
      padding: 1.5rem;
      border-bottom: 1px solid #eee;
      gap: 1rem;
      
      &:last-child {
        border-bottom: none;
      }
      
      .presence-checkbox {
        .form-check-input {
          transform: scale(1.2);
        }
      }
      
      .player-info {
        display: flex;
        align-items: center;
        min-width: 200px;
        
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
      
      .presence-controls {
        flex: 1;
        display: grid;
        grid-template-columns: 1fr auto auto;
        gap: 1rem;
        align-items: start;
        
        .status-buttons {
          display: flex;
          gap: 0.5rem;
          flex-wrap: wrap;
          
          .status-btn {
            border: 2px solid #ddd;
            transition: var(--transition);
            
            &:not(.active):hover {
              border-color: var(--primary-color);
            }
            
            &.active {
              border-color: transparent;
            }
            
            .material-icons {
              font-size: 1rem;
              margin-right: 0.25rem;
            }
          }
        }
        
        .time-input,
        .comment-input {
          min-width: 150px;
          
          .form-label {
            margin-bottom: 0.25rem;
            font-weight: 500;
          }
        }
      }
      
      .presence-status {
        text-align: center;
        min-width: 120px;
        
        .status-badge {
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 0.5rem 0.75rem;
          border-radius: 0.375rem;
          margin-bottom: 0.5rem;
          
          .material-icons {
            font-size: 1rem;
          }
        }
        
        .modification-indicator {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 0.25rem;
          
          .material-icons {
            font-size: 1rem;
          }
        }
      }
    }

    .changes-summary {
      margin-top: 2rem;
      
      .changes-list {
        margin-top: 1rem;
        
        .change-item {
          padding: 0.5rem 0;
          border-bottom: 1px solid rgba(0,0,0,0.1);
          
          &:last-child {
            border-bottom: none;
          }
        }
      }
    }

    @media (max-width: 768px) {
      .page-header {
        flex-direction: column;
        gap: 1rem;
        
        .header-actions {
          width: 100%;
          justify-content: stretch;
          
          .btn {
            flex: 1;
          }
        }
      }
      
      .presence-item {
        flex-direction: column;
        align-items: flex-start;
        
        .presence-controls {
          width: 100%;
          grid-template-columns: 1fr;
          gap: 1rem;
          
          .status-buttons {
            justify-content: center;
          }
        }
        
        .presence-status {
          width: 100%;
        }
      }
      
      .stats-preview .row > div {
        margin-bottom: 1rem;
      }
    }
  `]
})
export class PresenceFormComponent implements OnInit {
  entrainement: Entrainement | null = null;
  presenceItems: PresenceItem[] = [];
  filteredPresenceItems: PresenceItem[] = [];
  
  searchTerm = '';
  filterStatut = '';
  sortBy = 'nom';
  selectMode = false;
  selectedItems: PresenceItem[] = [];
  saving = false;

  statutOptions = Object.values(StatutParticipation).map(statut => ({
    value: statut,
    label: StatutParticipationLabels[statut]
  }));

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private participationService: ParticipationService,
    private entrainementService: EntrainementService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const entrainementId = +params['id'];
      if (entrainementId) {
        this.loadData(entrainementId);
      }
    });
  }

  private loadData(entrainementId: number): void {
    // Charger l'entraînement
    this.entrainementService.getEntrainementById(entrainementId).subscribe({
      next: (entrainement) => {
        this.entrainement = entrainement;
      },
      error: (error) => {
        console.error('Erreur lors du chargement de l\'entraînement:', error);
      }
    });

    // Charger les participations
    this.participationService.getParticipationsByEntrainement(entrainementId).subscribe({
      next: (participations) => {
        this.presenceItems = participations.map(participation => ({
          participation,
          joueurNom: `Joueur #${participation.joueurId}`, // À remplacer par le vrai nom
          nouveauStatut: participation.statut,
          commentaire: participation.commentaire || '',
          heureArrivee: participation.heurePresence || this.getCurrentTime(),
          modifie: false
        }));
        this.applyFilters();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des participations:', error);
      }
    });
  }

  applyFilters(): void {
    this.filteredPresenceItems = this.presenceItems.filter(item => {
      if (this.searchTerm && !item.joueurNom.toLowerCase().includes(this.searchTerm.toLowerCase())) {
        return false;
      }
      if (this.filterStatut && item.nouveauStatut !== this.filterStatut) {
        return false;
      }
      return true;
    });
    
    this.applySort();
  }

  applySort(): void {
    this.filteredPresenceItems.sort((a, b) => {
      switch (this.sortBy) {
        case 'nom':
          return a.joueurNom.localeCompare(b.joueurNom);
        case 'statut':
          return a.nouveauStatut.localeCompare(b.nouveauStatut);
        case 'heure':
          return a.heureArrivee.localeCompare(b.heureArrivee);
        default:
          return 0;
      }
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.filterStatut = '';
    this.applyFilters();
  }

  changerStatut(item: PresenceItem, nouveauStatut: StatutParticipation): void {
    item.nouveauStatut = nouveauStatut;
    this.markAsModified(item);
    
    if (nouveauStatut === StatutParticipation.PRESENT && !item.heureArrivee) {
      item.heureArrivee = this.getCurrentTime();
    }
  }

  markAsModified(item: PresenceItem): void {
    item.modifie = item.nouveauStatut !== item.participation.statut ||
                   item.commentaire !== (item.participation.commentaire || '') ||
                   (item.nouveauStatut === StatutParticipation.PRESENT && 
                    item.heureArrivee !== (item.participation.heurePresence || ''));
  }

  // Actions en lot
  marquerTousPresents(): void {
    this.filteredPresenceItems.forEach(item => {
      this.changerStatut(item, StatutParticipation.PRESENT);
    });
  }

  marquerTousAbsents(): void {
    this.filteredPresenceItems.forEach(item => {
      this.changerStatut(item, StatutParticipation.ABSENT);
    });
  }

  resetChanges(): void {
    this.presenceItems.forEach(item => {
      item.nouveauStatut = item.participation.statut;
      item.commentaire = item.participation.commentaire || '';
      item.heureArrivee = item.participation.heurePresence || this.getCurrentTime();
      item.modifie = false;
    });
    this.applyFilters();
  }

  // Mode sélection
  toggleSelectMode(): void {
    this.selectMode = !this.selectMode;
    if (!this.selectMode) {
      this.selectedItems = [];
    }
  }

  toggleSelection(item: PresenceItem): void {
    const index = this.selectedItems.indexOf(item);
    if (index > -1) {
      this.selectedItems.splice(index, 1);
    } else {
      this.selectedItems.push(item);
    }
  }

  isSelected(item: PresenceItem): boolean {
    return this.selectedItems.includes(item);
  }

  marquerSelectionPresents(): void {
    this.selectedItems.forEach(item => {
      this.changerStatut(item, StatutParticipation.PRESENT);
    });
    this.selectedItems = [];
  }

  marquerSelectionAbsents(): void {
    this.selectedItems.forEach(item => {
      this.changerStatut(item, StatutParticipation.ABSENT);
    });
    this.selectedItems = [];
  }

  // Sauvegarde
  sauvegarderPresences(): void {
    const modifications = this.getModifiedItems();
    if (modifications.length === 0) return;

    this.saving = true;
    
    const updates = modifications.map(item => {
      return this.participationService.updateParticipation(item.participation.id!, {
        statut: item.nouveauStatut,
        commentaire: item.commentaire,
        heurePresence: item.nouveauStatut === StatutParticipation.PRESENT ? item.heureArrivee : undefined
      });
    });

    // Exécuter toutes les mises à jour
    Promise.all(updates.map(update => update.toPromise())).then(() => {
      this.saving = false;
      // Marquer comme non modifié
      modifications.forEach(item => {
        item.participation.statut = item.nouveauStatut;
        item.participation.commentaire = item.commentaire;
        if (item.nouveauStatut === StatutParticipation.PRESENT) {
          item.participation.heurePresence = item.heureArrivee;
        }
        item.modifie = false;
      });
      
      // Notification de succès
      console.log('Présences sauvegardées avec succès');
    }).catch(error => {
      this.saving = false;
      console.error('Erreur lors de la sauvegarde:', error);
    });
  }

  goBack(): void {
    this.router.navigate(['/entrainements', this.entrainement?.id]);
  }

  // Méthodes utilitaires
  hasChanges(): boolean {
    return this.presenceItems.some(item => item.modifie);
  }

  getModifiedItems(): PresenceItem[] {
    return this.presenceItems.filter(item => item.modifie);
  }

  getTotalInscrits(): number {
    return this.presenceItems.length;
  }

  getTotalPresents(): number {
    return this.filteredPresenceItems.filter(item => item.nouveauStatut === StatutParticipation.PRESENT).length;
  }

  getTotalAbsents(): number {
    return this.filteredPresenceItems.filter(item => item.nouveauStatut === StatutParticipation.ABSENT).length;
  }

  getTauxPresence(): number {
    const total = this.filteredPresenceItems.length;
    if (total === 0) return 0;
    return Math.round((this.getTotalPresents() / total) * 100);
  }

  getCurrentTime(): string {
    return new Date().toTimeString().slice(0, 5);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  getStatutLabel(statut: StatutParticipation): string {
    return StatutParticipationLabels[statut];
  }

  getStatutColor(statut: StatutParticipation): string {
    return StatutParticipationColors[statut];
  }

  getStatutIcon(statut: StatutParticipation): string {
    return StatutParticipationIcons[statut];
  }

  trackByParticipation(index: number, item: PresenceItem): number {
    return item.participation.id || index;
  }
}
