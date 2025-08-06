import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { PerformanceService } from '../../../services/performance.service';
import { EntrainementService } from '../../../services/entrainement.service';
import { 
  Performance,
  CreatePerformanceDto,
  UpdatePerformanceDto,
  CategoriePerformance,
  CategoriePerformanceLabels,
  CategoriePerformanceColors,
  CategoriePerformanceIcons
} from '../../../models/performance.model';
import { Entrainement } from '../../../models/entrainement.model';

@Component({
  selector: 'app-performance-form',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  template: `
    <div class="performance-form-container fade-in">
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <i class="material-icons me-2">assessment</i>
            {{ isEditMode ? 'Modifier l\'Évaluation' : 'Nouvelle Évaluation' }}
          </h1>
          <p class="page-subtitle">
            {{ isEditMode ? 'Modifiez les détails de l\'évaluation' : 'Créez une nouvelle évaluation de performance' }}
          </p>
        </div>
        <div class="header-actions">
          <button class="btn btn-outline-secondary" (click)="goBack()">
            <i class="material-icons me-1">arrow_back</i>
            Retour
          </button>
        </div>
      </div>

      <!-- Informations de l'entraînement -->
      <div *ngIf="selectedEntrainement" class="entrainement-info">
        <div class="card">
          <div class="card-body">
            <h6 class="card-title">
              <i class="material-icons me-2">fitness_center</i>
              Entraînement sélectionné
            </h6>
            <div class="row">
              <div class="col-md-6">
                <strong>{{ selectedEntrainement.titre }}</strong>
                <div class="text-muted">{{ formatDate(selectedEntrainement.date) }}</div>
              </div>
              <div class="col-md-6">
                <div class="text-muted">
                  {{ selectedEntrainement.heureDebut }} - {{ selectedEntrainement.heureFin }}
                  <br>📍 {{ selectedEntrainement.lieu }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Formulaire -->
      <div class="card">
        <div class="card-header">
          <h5 class="mb-0">
            <i class="material-icons me-2">edit</i>
            Détails de l'évaluation
          </h5>
        </div>
        
        <div class="card-body">
          <form [formGroup]="performanceForm" (ngSubmit)="onSubmit()">
            <div class="row">
              <!-- Sélection de l'entraînement -->
              <div class="col-md-6 mb-3">
                <label class="form-label required">Entraînement</label>
                <select 
                  class="form-select" 
                  formControlName="entrainementId"
                  [class.is-invalid]="isFieldInvalid('entrainementId')"
                  (change)="onEntrainementChange()"
                >
                  <option value="">Sélectionnez un entraînement</option>
                  <option *ngFor="let entrainement of entrainements" [value]="entrainement.id">
                    {{ entrainement.titre }} - {{ formatDate(entrainement.date) }}
                  </option>
                </select>
                <div class="invalid-feedback" *ngIf="isFieldInvalid('entrainementId')">
                  Veuillez sélectionner un entraînement
                </div>
              </div>

              <!-- Sélection du joueur -->
              <div class="col-md-6 mb-3">
                <label class="form-label required">Joueur</label>
                <select 
                  class="form-select" 
                  formControlName="joueurId"
                  [class.is-invalid]="isFieldInvalid('joueurId')"
                >
                  <option value="">Sélectionnez un joueur</option>
                  <option *ngFor="let joueur of joueurs" [value]="joueur.id">
                    {{ joueur.nom }}
                  </option>
                </select>
                <div class="invalid-feedback" *ngIf="isFieldInvalid('joueurId')">
                  Veuillez sélectionner un joueur
                </div>
              </div>
            </div>

            <!-- Catégorie de performance -->
            <div class="mb-4">
              <label class="form-label required">Catégorie de performance</label>
              <div class="category-selection">
                <div class="row">
                  <div class="col-md-3 col-sm-6 mb-2" *ngFor="let categorie of categorieOptions">
                    <div 
                      class="category-option"
                      [class.selected]="performanceForm.get('categorie')?.value === categorie.value"
                      [style.border-color]="getCategorieColor(categorie.value)"
                      (click)="selectCategorie(categorie.value)"
                    >
                      <i 
                        class="material-icons category-icon"
                        [style.color]="getCategorieColor(categorie.value)"
                      >
                        {{ getCategorieIcon(categorie.value) }}
                      </i>
                      <div class="category-label">{{ categorie.label }}</div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="invalid-feedback d-block" *ngIf="isFieldInvalid('categorie')">
                Veuillez sélectionner une catégorie
              </div>
            </div>

            <!-- Note -->
            <div class="mb-4">
              <label class="form-label required">Note (sur 10)</label>
              <div class="note-input-container">
                <div class="note-slider">
                  <input 
                    type="range" 
                    class="form-range" 
                    min="0" 
                    max="10" 
                    step="0.1"
                    formControlName="note"
                    [style.background]="getNoteSliderBackground()"
                  >
                  <div class="range-labels">
                    <span>0</span>
                    <span>2</span>
                    <span>4</span>
                    <span>6</span>
                    <span>8</span>
                    <span>10</span>
                  </div>
                </div>
                <div class="note-display">
                  <div 
                    class="note-value"
                    [style.color]="getNoteColor(performanceForm.get('note')?.value || 0)"
                  >
                    {{ formatNote(performanceForm.get('note')?.value || 0) }}
                  </div>
                  <div 
                    class="note-level"
                    [style.color]="getNoteColor(performanceForm.get('note')?.value || 0)"
                  >
                    {{ getNiveauLabel(performanceForm.get('note')?.value || 0) }}
                  </div>
                </div>
              </div>
              <div class="note-guide">
                <div class="guide-item">
                  <span class="guide-range" style="color: #4caf50;">8-10</span>
                  <span class="guide-label">Excellent - Performance exceptionnelle</span>
                </div>
                <div class="guide-item">
                  <span class="guide-range" style="color: #8bc34a;">6-7.9</span>
                  <span class="guide-label">Bon - Performance satisfaisante</span>
                </div>
                <div class="guide-item">
                  <span class="guide-range" style="color: #ff9800;">4-5.9</span>
                  <span class="guide-label">Moyen - Performance correcte</span>
                </div>
                <div class="guide-item">
                  <span class="guide-range" style="color: #f44336;">0-3.9</span>
                  <span class="guide-label">Faible - Performance à améliorer</span>
                </div>
              </div>
            </div>

            <!-- Commentaire -->
            <div class="mb-4">
              <label class="form-label">Commentaire</label>
              <textarea 
                class="form-control" 
                rows="4"
                formControlName="commentaire"
                placeholder="Ajoutez vos observations sur la performance du joueur..."
                maxlength="1000"
              ></textarea>
              <div class="form-text">
                {{ (performanceForm.get('commentaire')?.value || '').length }}/1000 caractères
              </div>
            </div>

            <!-- Évaluateur -->
            <div class="mb-4">
              <label class="form-label required">Évaluateur</label>
              <select 
                class="form-select" 
                formControlName="evaluateurId"
                [class.is-invalid]="isFieldInvalid('evaluateurId')"
              >
                <option value="">Sélectionnez un évaluateur</option>
                <option *ngFor="let evaluateur of evaluateurs" [value]="evaluateur.id">
                  {{ evaluateur.nom }}
                </option>
              </select>
              <div class="invalid-feedback" *ngIf="isFieldInvalid('evaluateurId')">
                Veuillez sélectionner un évaluateur
              </div>
            </div>

            <!-- Aperçu de l'évaluation -->
            <div class="evaluation-preview">
              <h6>
                <i class="material-icons me-2">preview</i>
                Aperçu de l'évaluation
              </h6>
              <div class="preview-card">
                <div class="preview-header">
                  <div class="preview-player">
                    <strong>{{ getJoueurName(performanceForm.get('joueurId')?.value) }}</strong>
                  </div>
                  <div class="preview-note">
                    <span 
                      class="note-badge"
                      [style.background-color]="getNoteColor(performanceForm.get('note')?.value || 0)"
                    >
                      {{ formatNote(performanceForm.get('note')?.value || 0) }}
                    </span>
                  </div>
                </div>
                <div class="preview-details">
                  <div class="preview-category">
                    <span 
                      class="badge category-badge"
                      [style.background-color]="getCategorieColor(performanceForm.get('categorie')?.value)"
                      *ngIf="performanceForm.get('categorie')?.value"
                    >
                      <i class="material-icons me-1">{{ getCategorieIcon(performanceForm.get('categorie')?.value) }}</i>
                      {{ getCategorieLabel(performanceForm.get('categorie')?.value) }}
                    </span>
                  </div>
                  <div class="preview-comment" *ngIf="performanceForm.get('commentaire')?.value">
                    <i class="material-icons me-2">comment</i>
                    {{ performanceForm.get('commentaire')?.value }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Actions -->
            <div class="form-actions">
              <button 
                type="button" 
                class="btn btn-outline-secondary me-2"
                (click)="goBack()"
              >
                <i class="material-icons me-1">cancel</i>
                Annuler
              </button>
              <button 
                type="submit" 
                class="btn btn-primary"
                [disabled]="performanceForm.invalid || saving"
              >
                <i class="material-icons me-1">{{ isEditMode ? 'save' : 'add' }}</i>
                {{ saving ? 'Sauvegarde...' : (isEditMode ? 'Modifier' : 'Créer') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .performance-form-container {
      max-width: 800px;
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

    .entrainement-info {
      margin-bottom: 1.5rem;
      
      .card-title {
        color: var(--primary-color);
        margin-bottom: 1rem;
        display: flex;
        align-items: center;
      }
    }

    .required::after {
      content: ' *';
      color: #dc3545;
    }

    .category-selection {
      .category-option {
        border: 2px solid #ddd;
        border-radius: var(--border-radius);
        padding: 1rem;
        text-align: center;
        cursor: pointer;
        transition: var(--transition);
        background: white;
        
        &:hover {
          border-color: var(--primary-color);
          transform: translateY(-2px);
        }
        
        &.selected {
          border-color: currentColor;
          background: rgba(0,0,0,0.05);
        }
        
        .category-icon {
          font-size: 2rem;
          margin-bottom: 0.5rem;
          display: block;
        }
        
        .category-label {
          font-weight: 500;
          color: var(--primary-color);
        }
      }
    }

    .note-input-container {
      display: flex;
      align-items: center;
      gap: 2rem;
      
      .note-slider {
        flex: 1;
        
        .form-range {
          width: 100%;
          margin-bottom: 0.5rem;
        }
        
        .range-labels {
          display: flex;
          justify-content: space-between;
          font-size: 0.8rem;
          color: var(--secondary-color);
        }
      }
      
      .note-display {
        text-align: center;
        min-width: 100px;
        
        .note-value {
          font-size: 2rem;
          font-weight: 700;
          line-height: 1;
        }
        
        .note-level {
          font-size: 0.9rem;
          font-weight: 500;
          margin-top: 0.25rem;
        }
      }
    }

    .note-guide {
      margin-top: 1rem;
      padding: 1rem;
      background: #f8f9fa;
      border-radius: var(--border-radius);
      
      .guide-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.25rem 0;
        
        .guide-range {
          font-weight: 600;
          min-width: 60px;
        }
        
        .guide-label {
          color: var(--secondary-color);
        }
      }
    }

    .evaluation-preview {
      margin-top: 2rem;
      padding: 1.5rem;
      background: #f8f9fa;
      border-radius: var(--border-radius);
      
      h6 {
        color: var(--primary-color);
        margin-bottom: 1rem;
        display: flex;
        align-items: center;
      }
      
      .preview-card {
        background: white;
        border-radius: var(--border-radius);
        padding: 1rem;
        box-shadow: var(--box-shadow);
        
        .preview-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 1rem;
          
          .preview-player {
            strong {
              color: var(--primary-color);
            }
          }
          
          .note-badge {
            color: white;
            padding: 0.5rem 1rem;
            border-radius: 2rem;
            font-weight: 600;
            font-size: 1.1rem;
          }
        }
        
        .preview-details {
          .preview-category {
            margin-bottom: 0.75rem;
            
            .badge {
              display: inline-flex;
              align-items: center;
              padding: 0.5rem 0.75rem;
              
              .material-icons {
                font-size: 1rem;
              }
            }
          }
          
          .preview-comment {
            display: flex;
            align-items: flex-start;
            gap: 0.5rem;
            color: var(--secondary-color);
            font-style: italic;
            
            .material-icons {
              font-size: 1.2rem;
              margin-top: 0.1rem;
            }
          }
        }
      }
    }

    .form-actions {
      margin-top: 2rem;
      padding-top: 1.5rem;
      border-top: 1px solid #eee;
      display: flex;
      justify-content: flex-end;
    }

    @media (max-width: 768px) {
      .page-header {
        flex-direction: column;
        gap: 1rem;
        
        .header-actions {
          width: 100%;
        }
      }
      
      .note-input-container {
        flex-direction: column;
        gap: 1rem;
        
        .note-display {
          order: -1;
        }
      }
      
      .category-selection .row > div {
        margin-bottom: 1rem;
      }
      
      .form-actions {
        flex-direction: column-reverse;
        gap: 0.5rem;
        
        .btn {
          width: 100%;
        }
      }
    }
  `]
})
export class PerformanceFormComponent implements OnInit {
  performanceForm: FormGroup;
  isEditMode = false;
  saving = false;
  performanceId?: number;
  
  entrainements: Entrainement[] = [];
  joueurs: any[] = []; // À remplacer par le service des joueurs
  evaluateurs: any[] = []; // À remplacer par le service des utilisateurs
  selectedEntrainement?: Entrainement;

  categorieOptions = Object.values(CategoriePerformance).map(categorie => ({
    value: categorie,
    label: CategoriePerformanceLabels[categorie]
  }));

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private performanceService: PerformanceService,
    private entrainementService: EntrainementService
  ) {
    this.performanceForm = this.createForm();
  }

  ngOnInit(): void {
    this.loadData();
    this.checkEditMode();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      entrainementId: ['', Validators.required],
      joueurId: ['', Validators.required],
      categorie: ['', Validators.required],
      note: [5, [Validators.required, Validators.min(0), Validators.max(10)]],
      commentaire: ['', Validators.maxLength(1000)],
      evaluateurId: ['', Validators.required]
    });
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

    // Simuler le chargement des joueurs et évaluateurs
    this.joueurs = [
      { id: 1, nom: 'Joueur 1' },
      { id: 2, nom: 'Joueur 2' },
      { id: 3, nom: 'Joueur 3' }
    ];

    this.evaluateurs = [
      { id: 1, nom: 'Coach Principal' },
      { id: 2, nom: 'Assistant Coach' }
    ];
  }

  private checkEditMode(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.performanceId = +params['id'];
        this.loadPerformance();
      }
    });
  }

  private loadPerformance(): void {
    if (this.performanceId) {
      this.performanceService.getPerformanceById(this.performanceId).subscribe({
        next: (performance) => {
          this.performanceForm.patchValue({
            entrainementId: performance.entrainementId,
            joueurId: performance.joueurId,
            categorie: performance.categorie,
            note: performance.note,
            commentaire: performance.commentaire,
            evaluateurId: performance.evaluateurId
          });
          this.onEntrainementChange();
        },
        error: (error) => {
          console.error('Erreur lors du chargement de la performance:', error);
        }
      });
    }
  }

  onEntrainementChange(): void {
    const entrainementId = this.performanceForm.get('entrainementId')?.value;
    if (entrainementId) {
      this.selectedEntrainement = this.entrainements.find(e => e.id == entrainementId);
    } else {
      this.selectedEntrainement = undefined;
    }
  }

  selectCategorie(categorie: CategoriePerformance): void {
    this.performanceForm.patchValue({ categorie });
  }

  onSubmit(): void {
    if (this.performanceForm.valid) {
      this.saving = true;
      
      const formValue = this.performanceForm.value;
      
      if (this.isEditMode && this.performanceId) {
        const updateDto: UpdatePerformanceDto = {
          categorie: formValue.categorie,
          note: formValue.note,
          commentaire: formValue.commentaire,
          evaluateurId: formValue.evaluateurId
        };
        
        this.performanceService.updatePerformance(this.performanceId, updateDto).subscribe({
          next: () => {
            this.saving = false;
            this.router.navigate(['/performances']);
          },
          error: (error) => {
            this.saving = false;
            console.error('Erreur lors de la modification:', error);
          }
        });
      } else {
        const createDto: CreatePerformanceDto = {
          entrainementId: formValue.entrainementId,
          joueurId: formValue.joueurId,
          categorie: formValue.categorie,
          note: formValue.note,
          commentaire: formValue.commentaire,
          evaluateurId: formValue.evaluateurId
        };
        
        this.performanceService.createPerformance(createDto).subscribe({
          next: () => {
            this.saving = false;
            this.router.navigate(['/performances']);
          },
          error: (error) => {
            this.saving = false;
            console.error('Erreur lors de la création:', error);
          }
        });
      }
    }
  }

  goBack(): void {
    this.router.navigate(['/performances']);
  }

  // Méthodes utilitaires
  isFieldInvalid(fieldName: string): boolean {
    const field = this.performanceForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
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

  getNoteSliderBackground(): string {
    const note = this.performanceForm.get('note')?.value || 0;
    const percentage = (note / 10) * 100;
    const color = this.getNoteColor(note);
    return `linear-gradient(to right, ${color} 0%, ${color} ${percentage}%, #ddd ${percentage}%, #ddd 100%)`;
  }

  getJoueurName(joueurId: number): string {
    const joueur = this.joueurs.find(j => j.id == joueurId);
    return joueur?.nom || 'Joueur non sélectionné';
  }

  formatNote(note: number): string {
    return note?.toFixed(1) || '0.0';
  }

  formatDate(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR');
  }
}
