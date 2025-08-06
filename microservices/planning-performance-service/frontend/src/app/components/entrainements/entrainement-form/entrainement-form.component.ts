import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { EntrainementService } from '../../../services/entrainement.service';
import { 
  Entrainement, 
  TypeEntrainement,
  TypeEntrainementLabels,
  CreateEntrainementDto
} from '../../../models/entrainement.model';

@Component({
  selector: 'app-entrainement-form',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  template: `
    <div class="entrainement-form-container fade-in">
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <i class="material-icons me-2">{{ isEditMode ? 'edit' : 'add_circle' }}</i>
            {{ isEditMode ? 'Modifier l\'entraînement' : 'Nouvel entraînement' }}
          </h1>
          <p class="page-subtitle">
            {{ isEditMode ? 'Modifiez les informations de l\'entraînement' : 'Planifiez une nouvelle séance d\'entraînement' }}
          </p>
        </div>
        <div class="header-actions">
          <button type="button" class="btn btn-outline-secondary" (click)="goBack()">
            <i class="material-icons me-1">arrow_back</i>
            Retour
          </button>
        </div>
      </div>

      <div class="row">
        <div class="col-lg-8">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="material-icons me-2">info</i>
                Informations générales
              </h5>
            </div>
            <div class="card-body">
              <form [formGroup]="entrainementForm" (ngSubmit)="onSubmit()">
                <div class="row">
                  <div class="col-md-8 mb-3">
                    <label for="titre" class="form-label required">Titre de l'entraînement</label>
                    <input 
                      type="text" 
                      id="titre"
                      class="form-control"
                      formControlName="titre"
                      placeholder="Ex: Entraînement technique - Service"
                      [class.is-invalid]="isFieldInvalid('titre')"
                    >
                    <div *ngIf="isFieldInvalid('titre')" class="invalid-feedback">
                      Le titre est obligatoire (3-100 caractères)
                    </div>
                  </div>

                  <div class="col-md-4 mb-3">
                    <label for="type" class="form-label required">Type d'entraînement</label>
                    <select 
                      id="type"
                      class="form-select"
                      formControlName="type"
                      [class.is-invalid]="isFieldInvalid('type')"
                    >
                      <option value="">Sélectionner un type</option>
                      <option *ngFor="let type of typeOptions" [value]="type.value">
                        {{ type.label }}
                      </option>
                    </select>
                    <div *ngIf="isFieldInvalid('type')" class="invalid-feedback">
                      Le type est obligatoire
                    </div>
                  </div>
                </div>

                <div class="mb-3">
                  <label for="description" class="form-label">Description</label>
                  <textarea 
                    id="description"
                    class="form-control"
                    formControlName="description"
                    rows="3"
                    placeholder="Décrivez les objectifs et le contenu de l'entraînement..."
                    [class.is-invalid]="isFieldInvalid('description')"
                  ></textarea>
                  <div *ngIf="isFieldInvalid('description')" class="invalid-feedback">
                    La description ne peut pas dépasser 500 caractères
                  </div>
                </div>

                <div class="row">
                  <div class="col-md-4 mb-3">
                    <label for="date" class="form-label required">Date</label>
                    <input 
                      type="date" 
                      id="date"
                      class="form-control"
                      formControlName="date"
                      [min]="minDate"
                      [class.is-invalid]="isFieldInvalid('date')"
                    >
                    <div *ngIf="isFieldInvalid('date')" class="invalid-feedback">
                      La date est obligatoire et doit être future
                    </div>
                  </div>

                  <div class="col-md-4 mb-3">
                    <label for="heureDebut" class="form-label required">Heure de début</label>
                    <input 
                      type="time" 
                      id="heureDebut"
                      class="form-control"
                      formControlName="heureDebut"
                      [class.is-invalid]="isFieldInvalid('heureDebut')"
                    >
                    <div *ngIf="isFieldInvalid('heureDebut')" class="invalid-feedback">
                      L'heure de début est obligatoire
                    </div>
                  </div>

                  <div class="col-md-4 mb-3">
                    <label for="heureFin" class="form-label required">Heure de fin</label>
                    <input 
                      type="time" 
                      id="heureFin"
                      class="form-control"
                      formControlName="heureFin"
                      [class.is-invalid]="isFieldInvalid('heureFin')"
                    >
                    <div *ngIf="isFieldInvalid('heureFin')" class="invalid-feedback">
                      L'heure de fin est obligatoire et doit être après l'heure de début
                    </div>
                  </div>
                </div>

                <div class="row">
                  <div class="col-md-8 mb-3">
                    <label for="lieu" class="form-label required">Lieu</label>
                    <input 
                      type="text" 
                      id="lieu"
                      class="form-control"
                      formControlName="lieu"
                      placeholder="Ex: Gymnase municipal, Terrain A"
                      [class.is-invalid]="isFieldInvalid('lieu')"
                    >
                    <div *ngIf="isFieldInvalid('lieu')" class="invalid-feedback">
                      Le lieu est obligatoire (3-100 caractères)
                    </div>
                  </div>

                  <div class="col-md-4 mb-3">
                    <label for="nombreMaxParticipants" class="form-label required">Nombre max de participants</label>
                    <input 
                      type="number" 
                      id="nombreMaxParticipants"
                      class="form-control"
                      formControlName="nombreMaxParticipants"
                      min="1"
                      max="50"
                      [class.is-invalid]="isFieldInvalid('nombreMaxParticipants')"
                    >
                    <div *ngIf="isFieldInvalid('nombreMaxParticipants')" class="invalid-feedback">
                      Le nombre doit être entre 1 et 50
                    </div>
                  </div>
                </div>

                <div class="mb-3">
                  <label for="coachId" class="form-label required">Coach responsable</label>
                  <select 
                    id="coachId"
                    class="form-select"
                    formControlName="coachId"
                    [class.is-invalid]="isFieldInvalid('coachId')"
                  >
                    <option value="">Sélectionner un coach</option>
                    <option *ngFor="let coach of coaches" [value]="coach.id">
                      {{ coach.nom }} {{ coach.prenom }}
                    </option>
                  </select>
                  <div *ngIf="isFieldInvalid('coachId')" class="invalid-feedback">
                    Le coach responsable est obligatoire
                  </div>
                </div>

                <div *ngIf="isEditMode" class="mb-3">
                  <label for="notes" class="form-label">Notes</label>
                  <textarea 
                    id="notes"
                    class="form-control"
                    formControlName="notes"
                    rows="2"
                    placeholder="Notes additionnelles..."
                  ></textarea>
                </div>

                <div class="form-actions">
                  <button 
                    type="submit" 
                    class="btn btn-primary"
                    [disabled]="entrainementForm.invalid || loading"
                  >
                    <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>
                    <i *ngIf="!loading" class="material-icons me-1">{{ isEditMode ? 'save' : 'add' }}</i>
                    {{ isEditMode ? 'Enregistrer les modifications' : 'Créer l\'entraînement' }}
                  </button>
                  
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    (click)="resetForm()"
                    [disabled]="loading"
                  >
                    <i class="material-icons me-1">refresh</i>
                    Réinitialiser
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>

        <!-- Panneau d'aide -->
        <div class="col-lg-4">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="material-icons me-2">help_outline</i>
                Conseils
              </h5>
            </div>
            <div class="card-body">
              <div class="help-section">
                <h6><i class="material-icons me-1">lightbulb</i>Bonnes pratiques</h6>
                <ul class="help-list">
                  <li>Choisissez un titre descriptif et clair</li>
                  <li>Planifiez au moins 24h à l'avance</li>
                  <li>Adaptez la durée selon le type d'entraînement</li>
                  <li>Vérifiez la disponibilité du lieu</li>
                </ul>
              </div>

              <div class="help-section">
                <h6><i class="material-icons me-1">schedule</i>Durées recommandées</h6>
                <div class="duration-guide">
                  <div class="duration-item">
                    <span class="badge bg-primary">Physique</span>
                    <span>90-120 min</span>
                  </div>
                  <div class="duration-item">
                    <span class="badge bg-success">Technique</span>
                    <span>60-90 min</span>
                  </div>
                  <div class="duration-item">
                    <span class="badge bg-warning">Tactique</span>
                    <span>75-105 min</span>
                  </div>
                  <div class="duration-item">
                    <span class="badge bg-danger">Match</span>
                    <span>120-180 min</span>
                  </div>
                </div>
              </div>

              <div class="help-section">
                <h6><i class="material-icons me-1">info</i>Informations</h6>
                <div class="alert alert-info">
                  <small>
                    Les participants pourront s'inscrire dès la création de l'entraînement.
                    Vous pourrez modifier ces informations tant que l'entraînement n'a pas commencé.
                  </small>
                </div>
              </div>
            </div>
          </div>

          <!-- Aperçu en temps réel -->
          <div *ngIf="entrainementForm.valid" class="card mt-3">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="material-icons me-2">preview</i>
                Aperçu
              </h5>
            </div>
            <div class="card-body">
              <div class="preview-card">
                <h6 class="preview-title">{{ entrainementForm.get('titre')?.value }}</h6>
                <div class="preview-badges">
                  <span class="badge bg-primary">{{ getTypeLabel(entrainementForm.get('type')?.value) }}</span>
                </div>
                <div class="preview-details">
                  <div class="preview-item">
                    <i class="material-icons">schedule</i>
                    <span>{{ formatPreviewDateTime() }}</span>
                  </div>
                  <div class="preview-item">
                    <i class="material-icons">location_on</i>
                    <span>{{ entrainementForm.get('lieu')?.value }}</span>
                  </div>
                  <div class="preview-item">
                    <i class="material-icons">people</i>
                    <span>Max {{ entrainementForm.get('nombreMaxParticipants')?.value }} participants</span>
                  </div>
                  <div *ngIf="entrainementForm.get('heureDebut')?.value && entrainementForm.get('heureFin')?.value" class="preview-item">
                    <i class="material-icons">access_time</i>
                    <span>{{ calculateDuration() }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .entrainement-form-container {
      max-width: 1200px;
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

    .required::after {
      content: ' *';
      color: var(--danger-color);
    }

    .form-actions {
      display: flex;
      gap: 1rem;
      margin-top: 2rem;
      padding-top: 1.5rem;
      border-top: 1px solid #eee;
    }

    .help-section {
      margin-bottom: 1.5rem;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      h6 {
        color: var(--primary-color);
        font-weight: 500;
        margin-bottom: 0.75rem;
        display: flex;
        align-items: center;
      }
    }

    .help-list {
      list-style: none;
      padding: 0;
      margin: 0;
      
      li {
        padding: 0.25rem 0;
        color: var(--secondary-color);
        font-size: 0.9rem;
        position: relative;
        padding-left: 1rem;
        
        &::before {
          content: '•';
          color: var(--primary-color);
          position: absolute;
          left: 0;
        }
      }
    }

    .duration-guide {
      .duration-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.5rem 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        .badge {
          font-size: 0.7rem;
        }
        
        span:last-child {
          font-weight: 500;
          color: var(--secondary-color);
        }
      }
    }

    .preview-card {
      border: 1px solid #e0e0e0;
      border-radius: var(--border-radius);
      padding: 1rem;
      background: #f8f9fa;
      
      .preview-title {
        color: var(--primary-color);
        font-weight: 500;
        margin-bottom: 0.5rem;
      }
      
      .preview-badges {
        margin-bottom: 1rem;
      }
      
      .preview-details {
        .preview-item {
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
    }

    .spinner-border-sm {
      width: 1rem;
      height: 1rem;
    }

    @media (max-width: 768px) {
      .page-header {
        flex-direction: column;
        gap: 1rem;
        
        .header-actions {
          width: 100%;
        }
      }
      
      .form-actions {
        flex-direction: column;
        
        .btn {
          width: 100%;
        }
      }
    }
  `]
})
export class EntrainementFormComponent implements OnInit {
  entrainementForm!: FormGroup;
  isEditMode = false;
  entrainementId: number | null = null;
  loading = false;
  minDate = new Date().toISOString().split('T')[0];

  typeOptions = Object.values(TypeEntrainement).map(type => ({
    value: type,
    label: TypeEntrainementLabels[type]
  }));

  coaches = [
    { id: 1, nom: 'Dupont', prenom: 'Jean' },
    { id: 2, nom: 'Martin', prenom: 'Marie' },
    { id: 3, nom: 'Bernard', prenom: 'Pierre' }
  ];

  constructor(
    private fb: FormBuilder,
    private entrainementService: EntrainementService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.entrainementId = +params['id'];
        this.loadEntrainement();
      }
    });
  }

  private initForm(): void {
    this.entrainementForm = this.fb.group({
      titre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      date: ['', [Validators.required]],
      heureDebut: ['', [Validators.required]],
      heureFin: ['', [Validators.required]],
      type: ['', [Validators.required]],
      lieu: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      coachId: ['', [Validators.required]],
      nombreMaxParticipants: [12, [Validators.required, Validators.min(1), Validators.max(50)]],
      notes: ['']
    }, { validators: this.timeValidator });
  }

  private timeValidator(form: FormGroup) {
    const heureDebut = form.get('heureDebut')?.value;
    const heureFin = form.get('heureFin')?.value;
    
    if (heureDebut && heureFin && heureDebut >= heureFin) {
      form.get('heureFin')?.setErrors({ timeInvalid: true });
      return { timeInvalid: true };
    }
    
    return null;
  }

  private loadEntrainement(): void {
    if (!this.entrainementId) return;
    
    this.loading = true;
    this.entrainementService.getEntrainementById(this.entrainementId).subscribe({
      next: (entrainement) => {
        this.entrainementForm.patchValue({
          titre: entrainement.titre,
          description: entrainement.description,
          date: entrainement.date,
          heureDebut: entrainement.heureDebut,
          heureFin: entrainement.heureFin,
          type: entrainement.type,
          lieu: entrainement.lieu,
          coachId: entrainement.coachId,
          nombreMaxParticipants: entrainement.nombreMaxParticipants,
          notes: entrainement.notes
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement:', error);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.entrainementForm.invalid) return;
    
    this.loading = true;
    const formData = this.entrainementForm.value;
    
    if (this.isEditMode && this.entrainementId) {
      this.entrainementService.updateEntrainement(this.entrainementId, formData).subscribe({
        next: () => {
          this.router.navigate(['/entrainements', this.entrainementId]);
        },
        error: (error) => {
          console.error('Erreur lors de la modification:', error);
          this.loading = false;
        }
      });
    } else {
      const createDto: CreateEntrainementDto = formData;
      this.entrainementService.createEntrainement(createDto).subscribe({
        next: (entrainement) => {
          this.router.navigate(['/entrainements', entrainement.id]);
        },
        error: (error) => {
          console.error('Erreur lors de la création:', error);
          this.loading = false;
        }
      });
    }
  }

  resetForm(): void {
    if (this.isEditMode) {
      this.loadEntrainement();
    } else {
      this.entrainementForm.reset();
      this.entrainementForm.patchValue({
        nombreMaxParticipants: 12
      });
    }
  }

  goBack(): void {
    if (this.isEditMode && this.entrainementId) {
      this.router.navigate(['/entrainements', this.entrainementId]);
    } else {
      this.router.navigate(['/entrainements']);
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.entrainementForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getTypeLabel(type: TypeEntrainement): string {
    return TypeEntrainementLabels[type] || '';
  }

  formatPreviewDateTime(): string {
    const date = this.entrainementForm.get('date')?.value;
    const heureDebut = this.entrainementForm.get('heureDebut')?.value;
    
    if (!date || !heureDebut) return '';
    
    const dateObj = new Date(date);
    return `${dateObj.toLocaleDateString('fr-FR')} à ${heureDebut}`;
  }

  calculateDuration(): string {
    const heureDebut = this.entrainementForm.get('heureDebut')?.value;
    const heureFin = this.entrainementForm.get('heureFin')?.value;
    
    if (!heureDebut || !heureFin) return '';
    
    return this.entrainementService.formatDuree(heureDebut, heureFin);
  }
}
