import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ToastrService } from 'ngx-toastr';

import { AbsenceService } from '../../../services/absence.service';
import { EntrainementService } from '../../../services/entrainement.service';
import { Absence, TypeAbsence, StatutAbsence } from '../../../models/absence.model';
import { Entrainement } from '../../../models/entrainement.model';

@Component({
  selector: 'app-absence-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="container-fluid py-4">
      <div class="row justify-content-center">
        <div class="col-lg-8">
          <mat-card class="absence-form-card">
            <mat-card-header class="pb-3">
              <mat-card-title class="d-flex align-items-center">
                <mat-icon class="me-2 text-warning">event_busy</mat-icon>
                {{ isEditMode ? 'Modifier l\'absence' : 'Déclarer une absence' }}
              </mat-card-title>
              <mat-card-subtitle>
                {{ isEditMode ? 'Modifiez les informations de l\'absence' : 'Remplissez le formulaire pour déclarer votre absence' }}
              </mat-card-subtitle>
            </mat-card-header>

            <mat-card-content>
              <form [formGroup]="absenceForm" (ngSubmit)="onSubmit()" class="absence-form">
                
                <!-- Sélection de l'entraînement -->
                <div class="row mb-3">
                  <div class="col-12">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Entraînement concerné</mat-label>
                      <mat-select formControlName="entrainementId" [disabled]="isEditMode">
                        <mat-option *ngFor="let entrainement of entrainements" [value]="entrainement.id">
                          {{ entrainement.titre }} - {{ entrainement.date | date:'dd/MM/yyyy' }} à {{ entrainement.heureDebut }}
                        </mat-option>
                      </mat-select>
                      <mat-error *ngIf="absenceForm.get('entrainementId')?.hasError('required')">
                        Veuillez sélectionner un entraînement
                      </mat-error>
                    </mat-form-field>
                  </div>
                </div>

                <!-- Type d'absence -->
                <div class="row mb-3">
                  <div class="col-md-6">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Type d'absence</mat-label>
                      <mat-select formControlName="type">
                        <mat-option *ngFor="let type of typeAbsenceOptions" [value]="type.value">
                          <mat-icon class="me-2">{{ type.icon }}</mat-icon>
                          {{ type.label }}
                        </mat-option>
                      </mat-select>
                      <mat-error *ngIf="absenceForm.get('type')?.hasError('required')">
                        Veuillez sélectionner un type d'absence
                      </mat-error>
                    </mat-form-field>
                  </div>
                  
                  <div class="col-md-6">
                    <mat-checkbox formControlName="justificationFournie" class="mt-3">
                      Justification fournie
                    </mat-checkbox>
                    <div class="text-muted small mt-1">
                      Cochez si vous avez un document justificatif
                    </div>
                  </div>
                </div>

                <!-- Motif -->
                <div class="row mb-3">
                  <div class="col-12">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Motif de l'absence</mat-label>
                      <textarea 
                        matInput 
                        formControlName="motif" 
                        rows="3"
                        placeholder="Décrivez brièvement le motif de votre absence...">
                      </textarea>
                      <mat-error *ngIf="absenceForm.get('motif')?.hasError('required')">
                        Le motif est obligatoire
                      </mat-error>
                      <mat-error *ngIf="absenceForm.get('motif')?.hasError('minlength')">
                        Le motif doit contenir au moins 10 caractères
                      </mat-error>
                    </mat-form-field>
                  </div>
                </div>

                <!-- Commentaire -->
                <div class="row mb-3">
                  <div class="col-12">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Commentaire (optionnel)</mat-label>
                      <textarea 
                        matInput 
                        formControlName="commentaire" 
                        rows="2"
                        placeholder="Informations complémentaires...">
                      </textarea>
                    </mat-form-field>
                  </div>
                </div>

                <!-- Informations sur le statut (mode édition) -->
                <div *ngIf="isEditMode && currentAbsence" class="row mb-3">
                  <div class="col-12">
                    <div class="alert alert-info">
                      <div class="d-flex align-items-center">
                        <mat-icon class="me-2">info</mat-icon>
                        <div>
                          <strong>Statut actuel :</strong> 
                          <span [class]="getStatutClass(currentAbsence.statut)">
                            {{ getStatutLabel(currentAbsence.statut) }}
                          </span>
                        </div>
                      </div>
                      <div *ngIf="currentAbsence.commentaireApprobation" class="mt-2">
                        <strong>Commentaire d'approbation :</strong> {{ currentAbsence.commentaireApprobation }}
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Actions -->
                <div class="row">
                  <div class="col-12">
                    <div class="d-flex justify-content-between align-items-center">
                      <button 
                        type="button" 
                        mat-stroked-button 
                        color="primary"
                        (click)="onCancel()"
                        [disabled]="isLoading">
                        <mat-icon>arrow_back</mat-icon>
                        Retour
                      </button>

                      <div class="d-flex gap-2">
                        <button 
                          *ngIf="isEditMode && canDelete()" 
                          type="button" 
                          mat-stroked-button 
                          color="warn"
                          (click)="onDelete()"
                          [disabled]="isLoading">
                          <mat-icon>delete</mat-icon>
                          Supprimer
                        </button>

                        <button 
                          type="submit" 
                          mat-raised-button 
                          color="primary"
                          [disabled]="absenceForm.invalid || isLoading">
                          <mat-spinner *ngIf="isLoading" diameter="20" class="me-2"></mat-spinner>
                          <mat-icon *ngIf="!isLoading">{{ isEditMode ? 'save' : 'send' }}</mat-icon>
                          {{ isEditMode ? 'Modifier' : 'Déclarer l\'absence' }}
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

              </form>
            </mat-card-content>
          </mat-card>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .absence-form-card {
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      border-radius: 12px;
    }

    .absence-form {
      max-width: 100%;
    }

    .mat-mdc-form-field {
      margin-bottom: 8px;
    }

    .alert {
      border-radius: 8px;
      border: none;
    }

    .alert-info {
      background-color: #e3f2fd;
      color: #1976d2;
    }

    .badge {
      font-size: 0.75rem;
      padding: 0.25rem 0.5rem;
      border-radius: 0.375rem;
    }

    .badge-warning {
      background-color: #fff3cd;
      color: #856404;
    }

    .badge-success {
      background-color: #d1edff;
      color: #0f5132;
    }

    .badge-danger {
      background-color: #f8d7da;
      color: #721c24;
    }

    .gap-2 {
      gap: 0.5rem;
    }

    mat-icon {
      vertical-align: middle;
    }

    .text-muted {
      color: #6c757d !important;
    }

    .small {
      font-size: 0.875rem;
    }
  `]
})
export class AbsenceFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private absenceService = inject(AbsenceService);
  private entrainementService = inject(EntrainementService);
  private toastr = inject(ToastrService);

  absenceForm: FormGroup;
  isEditMode = false;
  isLoading = false;
  currentAbsence: Absence | null = null;
  entrainements: Entrainement[] = [];

  typeAbsenceOptions = [
    { value: TypeAbsence.MALADIE, label: 'Maladie', icon: 'sick' },
    { value: TypeAbsence.BLESSURE, label: 'Blessure', icon: 'healing' },
    { value: TypeAbsence.URGENCE_FAMILIALE, label: 'Urgence familiale', icon: 'family_restroom' },
    { value: TypeAbsence.OBLIGATIONS_PROFESSIONNELLES, label: 'Obligations professionnelles', icon: 'work' },
    { value: TypeAbsence.OBLIGATIONS_SCOLAIRES, label: 'Obligations scolaires', icon: 'school' },
    { value: TypeAbsence.CONGES, label: 'Congés', icon: 'beach_access' },
    { value: TypeAbsence.AUTRE, label: 'Autre', icon: 'help_outline' }
  ];

  constructor() {
    this.absenceForm = this.fb.group({
      entrainementId: ['', Validators.required],
      type: ['', Validators.required],
      motif: ['', [Validators.required, Validators.minLength(10)]],
      justificationFournie: [false],
      commentaire: ['']
    });
  }

  ngOnInit(): void {
    this.loadEntrainements();
    
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.loadAbsence(+id);
    }

    // Pré-sélectionner l'entraînement si passé en paramètre
    const entrainementId = this.route.snapshot.queryParamMap.get('entrainementId');
    if (entrainementId && !this.isEditMode) {
      this.absenceForm.patchValue({ entrainementId: +entrainementId });
    }
  }

  loadEntrainements(): void {
    // Charger les entraînements futurs uniquement
    const today = new Date();
    this.entrainementService.getEntrainements({
      dateDebut: today.toISOString().split('T')[0],
      size: 50,
      sort: 'date,asc'
    }).subscribe({
      next: (response) => {
        this.entrainements = response.content || [];
      },
      error: (error) => {
        console.error('Erreur lors du chargement des entraînements:', error);
        this.toastr.error('Erreur lors du chargement des entraînements');
      }
    });
  }

  loadAbsence(id: number): void {
    this.isLoading = true;
    this.absenceService.getAbsenceById(id).subscribe({
      next: (absence) => {
        this.currentAbsence = absence;
        this.absenceForm.patchValue({
          entrainementId: absence.entrainementId,
          type: absence.type,
          motif: absence.motif,
          justificationFournie: absence.justificationFournie,
          commentaire: absence.commentaire
        });
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement de l\'absence:', error);
        this.toastr.error('Erreur lors du chargement de l\'absence');
        this.router.navigate(['/absences']);
        this.isLoading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.absenceForm.valid) {
      this.isLoading = true;
      const formData = this.absenceForm.value;

      if (this.isEditMode && this.currentAbsence) {
        this.absenceService.updateAbsence(this.currentAbsence.id!, formData).subscribe({
          next: () => {
            this.toastr.success('Absence modifiée avec succès');
            this.router.navigate(['/absences']);
          },
          error: (error) => {
            console.error('Erreur lors de la modification:', error);
            this.toastr.error('Erreur lors de la modification de l\'absence');
            this.isLoading = false;
          }
        });
      } else {
        this.absenceService.createAbsence(formData).subscribe({
          next: () => {
            this.toastr.success('Absence déclarée avec succès');
            this.router.navigate(['/absences']);
          },
          error: (error) => {
            console.error('Erreur lors de la création:', error);
            this.toastr.error('Erreur lors de la déclaration de l\'absence');
            this.isLoading = false;
          }
        });
      }
    }
  }

  onDelete(): void {
    if (this.currentAbsence && confirm('Êtes-vous sûr de vouloir supprimer cette absence ?')) {
      this.isLoading = true;
      this.absenceService.deleteAbsence(this.currentAbsence.id!).subscribe({
        next: () => {
          this.toastr.success('Absence supprimée avec succès');
          this.router.navigate(['/absences']);
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          this.toastr.error('Erreur lors de la suppression de l\'absence');
          this.isLoading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/absences']);
  }

  canDelete(): boolean {
    return this.currentAbsence?.statut === StatutAbsence.EN_ATTENTE;
  }

  getStatutClass(statut: StatutAbsence): string {
    switch (statut) {
      case StatutAbsence.EN_ATTENTE:
        return 'badge badge-warning';
      case StatutAbsence.APPROUVEE:
        return 'badge badge-success';
      case StatutAbsence.REJETEE:
        return 'badge badge-danger';
      default:
        return 'badge badge-secondary';
    }
  }

  getStatutLabel(statut: StatutAbsence): string {
    switch (statut) {
      case StatutAbsence.EN_ATTENTE:
        return 'En attente';
      case StatutAbsence.APPROUVEE:
        return 'Approuvée';
      case StatutAbsence.REJETEE:
        return 'Rejetée';
      default:
        return statut;
    }
  }
}
