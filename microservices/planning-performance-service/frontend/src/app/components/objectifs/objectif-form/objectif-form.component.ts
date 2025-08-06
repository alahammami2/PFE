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
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSliderModule } from '@angular/material/slider';
import { MatChipsModule } from '@angular/material/chips';
import { ToastrService } from 'ngx-toastr';

import { ObjectifService } from '../../../services/objectif.service';
import { ObjectifIndividuel, TypeObjectif, PrioriteObjectif, StatutObjectif } from '../../../models/objectif.model';

@Component({
  selector: 'app-objectif-form',
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
    MatIconModule,
    MatProgressSpinnerModule,
    MatSliderModule,
    MatChipsModule
  ],
  template: `
    <div class="container-fluid py-4">
      <div class="row justify-content-center">
        <div class="col-lg-8">
          <mat-card class="objectif-form-card">
            <mat-card-header class="pb-3">
              <mat-card-title class="d-flex align-items-center">
                <mat-icon class="me-2 text-primary">flag</mat-icon>
                {{ isEditMode ? 'Modifier l\'objectif' : 'Créer un nouvel objectif' }}
              </mat-card-title>
              <mat-card-subtitle>
                {{ isEditMode ? 'Modifiez les informations de l\'objectif' : 'Définissez un objectif SMART pour améliorer vos performances' }}
              </mat-card-subtitle>
            </mat-card-header>

            <mat-card-content>
              <form [formGroup]="objectifForm" (ngSubmit)="onSubmit()" class="objectif-form">
                
                <!-- Informations de base -->
                <div class="section-header mb-3">
                  <h5 class="text-primary">
                    <mat-icon class="me-2">info</mat-icon>
                    Informations générales
                  </h5>
                </div>

                <div class="row mb-3">
                  <div class="col-12">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Titre de l'objectif</mat-label>
                      <input 
                        matInput 
                        formControlName="titre" 
                        placeholder="Ex: Améliorer mon service">
                      <mat-error *ngIf="objectifForm.get('titre')?.hasError('required')">
                        Le titre est obligatoire
                      </mat-error>
                      <mat-error *ngIf="objectifForm.get('titre')?.hasError('minlength')">
                        Le titre doit contenir au moins 5 caractères
                      </mat-error>
                    </mat-form-field>
                  </div>
                </div>

                <div class="row mb-3">
                  <div class="col-12">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Description détaillée</mat-label>
                      <textarea 
                        matInput 
                        formControlName="description" 
                        rows="3"
                        placeholder="Décrivez précisément votre objectif et comment vous comptez l'atteindre...">
                      </textarea>
                      <mat-error *ngIf="objectifForm.get('description')?.hasError('required')">
                        La description est obligatoire
                      </mat-error>
                      <mat-error *ngIf="objectifForm.get('description')?.hasError('minlength')">
                        La description doit contenir au moins 20 caractères
                      </mat-error>
                    </mat-form-field>
                  </div>
                </div>

                <!-- Catégorisation -->
                <div class="section-header mb-3 mt-4">
                  <h5 class="text-primary">
                    <mat-icon class="me-2">category</mat-icon>
                    Catégorisation
                  </h5>
                </div>

                <div class="row mb-3">
                  <div class="col-md-6">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Type d'objectif</mat-label>
                      <mat-select formControlName="type">
                        <mat-option *ngFor="let type of typeObjectifOptions" [value]="type.value">
                          <mat-icon class="me-2">{{ type.icon }}</mat-icon>
                          {{ type.label }}
                        </mat-option>
                      </mat-select>
                      <mat-error *ngIf="objectifForm.get('type')?.hasError('required')">
                        Veuillez sélectionner un type
                      </mat-error>
                    </mat-form-field>
                  </div>
                  
                  <div class="col-md-6">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Priorité</mat-label>
                      <mat-select formControlName="priorite">
                        <mat-option *ngFor="let priorite of prioriteOptions" [value]="priorite.value">
                          <mat-chip [class]="priorite.class" class="me-2">{{ priorite.label }}</mat-chip>
                        </mat-option>
                      </mat-select>
                      <mat-error *ngIf="objectifForm.get('priorite')?.hasError('required')">
                        Veuillez sélectionner une priorité
                      </mat-error>
                    </mat-form-field>
                  </div>
                </div>

                <!-- Période -->
                <div class="section-header mb-3 mt-4">
                  <h5 class="text-primary">
                    <mat-icon class="me-2">schedule</mat-icon>
                    Période et échéances
                  </h5>
                </div>

                <div class="row mb-3">
                  <div class="col-md-6">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Date de début</mat-label>
                      <input 
                        matInput 
                        [matDatepicker]="pickerDebut" 
                        formControlName="dateDebut">
                      <mat-datepicker-toggle matIconSuffix [for]="pickerDebut"></mat-datepicker-toggle>
                      <mat-datepicker #pickerDebut></mat-datepicker>
                      <mat-error *ngIf="objectifForm.get('dateDebut')?.hasError('required')">
                        La date de début est obligatoire
                      </mat-error>
                    </mat-form-field>
                  </div>
                  
                  <div class="col-md-6">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Date de fin</mat-label>
                      <input 
                        matInput 
                        [matDatepicker]="pickerFin" 
                        formControlName="dateFin">
                      <mat-datepicker-toggle matIconSuffix [for]="pickerFin"></mat-datepicker-toggle>
                      <mat-datepicker #pickerFin></mat-datepicker>
                      <mat-error *ngIf="objectifForm.get('dateFin')?.hasError('required')">
                        La date de fin est obligatoire
                      </mat-error>
                      <mat-error *ngIf="objectifForm.get('dateFin')?.hasError('dateOrder')">
                        La date de fin doit être après la date de début
                      </mat-error>
                    </mat-form-field>
                  </div>
                </div>

                <!-- Mesures et critères -->
                <div class="section-header mb-3 mt-4">
                  <h5 class="text-primary">
                    <mat-icon class="me-2">analytics</mat-icon>
                    Mesures et critères de réussite
                  </h5>
                </div>

                <div class="row mb-3">
                  <div class="col-12">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Critères de mesure</mat-label>
                      <textarea 
                        matInput 
                        formControlName="criteresMesure" 
                        rows="2"
                        placeholder="Ex: Pourcentage de réussite au service, nombre d'attaques réussies...">
                      </textarea>
                      <mat-error *ngIf="objectifForm.get('criteresMesure')?.hasError('required')">
                        Les critères de mesure sont obligatoires
                      </mat-error>
                    </mat-form-field>
                  </div>
                </div>

                <div class="row mb-3">
                  <div class="col-md-4">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Valeur cible</mat-label>
                      <input 
                        matInput 
                        type="number" 
                        formControlName="valeurCible"
                        placeholder="Ex: 80">
                      <mat-error *ngIf="objectifForm.get('valeurCible')?.hasError('required')">
                        La valeur cible est obligatoire
                      </mat-error>
                      <mat-error *ngIf="objectifForm.get('valeurCible')?.hasError('min')">
                        La valeur doit être positive
                      </mat-error>
                    </mat-form-field>
                  </div>
                  
                  <div class="col-md-4">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Valeur actuelle</mat-label>
                      <input 
                        matInput 
                        type="number" 
                        formControlName="valeurActuelle"
                        placeholder="Ex: 65">
                      <mat-error *ngIf="objectifForm.get('valeurActuelle')?.hasError('min')">
                        La valeur doit être positive
                      </mat-error>
                    </mat-form-field>
                  </div>
                  
                  <div class="col-md-4">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Unité de mesure</mat-label>
                      <input 
                        matInput 
                        formControlName="uniteMesure"
                        placeholder="Ex: %, points, secondes">
                      <mat-error *ngIf="objectifForm.get('uniteMesure')?.hasError('required')">
                        L'unité de mesure est obligatoire
                      </mat-error>
                    </mat-form-field>
                  </div>
                </div>

                <!-- Progression (mode édition) -->
                <div *ngIf="isEditMode && currentObjectif" class="section-header mb-3 mt-4">
                  <h5 class="text-primary">
                    <mat-icon class="me-2">trending_up</mat-icon>
                    Progression actuelle
                  </h5>
                  
                  <div class="progress-info mb-3">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                      <span>Progression: {{ currentObjectif.progression }}%</span>
                      <mat-chip [class]="getStatutClass(currentObjectif.statut)">
                        {{ getStatutLabel(currentObjectif.statut) }}
                      </mat-chip>
                    </div>
                    <div class="progress">
                      <div 
                        class="progress-bar" 
                        [style.width.%]="currentObjectif.progression"
                        [class]="getProgressBarClass(currentObjectif.progression)">
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Actions -->
                <div class="row mt-4">
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
                          [disabled]="objectifForm.invalid || isLoading">
                          <mat-spinner *ngIf="isLoading" diameter="20" class="me-2"></mat-spinner>
                          <mat-icon *ngIf="!isLoading">{{ isEditMode ? 'save' : 'flag' }}</mat-icon>
                          {{ isEditMode ? 'Modifier' : 'Créer l\'objectif' }}
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
    .objectif-form-card {
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      border-radius: 12px;
    }

    .objectif-form {
      max-width: 100%;
    }

    .section-header {
      border-bottom: 2px solid #e9ecef;
      padding-bottom: 0.5rem;
    }

    .section-header h5 {
      margin: 0;
      font-weight: 600;
    }

    .mat-mdc-form-field {
      margin-bottom: 8px;
    }

    .progress {
      height: 8px;
      background-color: #e9ecef;
      border-radius: 4px;
      overflow: hidden;
    }

    .progress-bar {
      height: 100%;
      transition: width 0.3s ease;
    }

    .progress-bar.bg-danger {
      background-color: #dc3545;
    }

    .progress-bar.bg-warning {
      background-color: #ffc107;
    }

    .progress-bar.bg-info {
      background-color: #17a2b8;
    }

    .progress-bar.bg-success {
      background-color: #28a745;
    }

    .progress-info {
      background-color: #f8f9fa;
      padding: 1rem;
      border-radius: 8px;
      border: 1px solid #e9ecef;
    }

    .gap-2 {
      gap: 0.5rem;
    }

    mat-chip.priorite-faible {
      background-color: #d1ecf1;
      color: #0c5460;
    }

    mat-chip.priorite-moyenne {
      background-color: #fff3cd;
      color: #856404;
    }

    mat-chip.priorite-haute {
      background-color: #f8d7da;
      color: #721c24;
    }

    mat-chip.statut-en-cours {
      background-color: #d1ecf1;
      color: #0c5460;
    }

    mat-chip.statut-termine {
      background-color: #d1edff;
      color: #0f5132;
    }

    mat-chip.statut-en-retard {
      background-color: #f8d7da;
      color: #721c24;
    }

    mat-chip.statut-suspendu {
      background-color: #e2e3e5;
      color: #383d41;
    }

    mat-icon {
      vertical-align: middle;
    }

    .text-primary {
      color: #007bff !important;
    }
  `]
})
export class ObjectifFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private objectifService = inject(ObjectifService);
  private toastr = inject(ToastrService);

  objectifForm: FormGroup;
  isEditMode = false;
  isLoading = false;
  currentObjectif: ObjectifIndividuel | null = null;

  typeObjectifOptions = [
    { value: TypeObjectif.TECHNIQUE, label: 'Technique', icon: 'sports_volleyball' },
    { value: TypeObjectif.PHYSIQUE, label: 'Physique', icon: 'fitness_center' },
    { value: TypeObjectif.TACTIQUE, label: 'Tactique', icon: 'psychology' },
    { value: TypeObjectif.MENTAL, label: 'Mental', icon: 'psychology_alt' },
    { value: TypeObjectif.PERSONNEL, label: 'Personnel', icon: 'person' }
  ];

  prioriteOptions = [
    { value: PrioriteObjectif.FAIBLE, label: 'Faible', class: 'priorite-faible' },
    { value: PrioriteObjectif.MOYENNE, label: 'Moyenne', class: 'priorite-moyenne' },
    { value: PrioriteObjectif.HAUTE, label: 'Haute', class: 'priorite-haute' }
  ];

  constructor() {
    this.objectifForm = this.fb.group({
      titre: ['', [Validators.required, Validators.minLength(5)]],
      description: ['', [Validators.required, Validators.minLength(20)]],
      type: ['', Validators.required],
      priorite: ['', Validators.required],
      dateDebut: ['', Validators.required],
      dateFin: ['', Validators.required],
      criteresMesure: ['', Validators.required],
      valeurCible: ['', [Validators.required, Validators.min(0)]],
      valeurActuelle: [0, [Validators.min(0)]],
      uniteMesure: ['', Validators.required]
    }, { validators: this.dateOrderValidator });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.loadObjectif(+id);
    }

    // Pré-remplir avec la date d'aujourd'hui
    if (!this.isEditMode) {
      const today = new Date();
      const nextMonth = new Date(today);
      nextMonth.setMonth(nextMonth.getMonth() + 1);
      
      this.objectifForm.patchValue({
        dateDebut: today,
        dateFin: nextMonth
      });
    }
  }

  dateOrderValidator(group: FormGroup) {
    const dateDebut = group.get('dateDebut')?.value;
    const dateFin = group.get('dateFin')?.value;
    
    if (dateDebut && dateFin && new Date(dateFin) <= new Date(dateDebut)) {
      return { dateOrder: true };
    }
    return null;
  }

  loadObjectif(id: number): void {
    this.isLoading = true;
    this.objectifService.getObjectifById(id).subscribe({
      next: (objectif) => {
        this.currentObjectif = objectif;
        this.objectifForm.patchValue({
          titre: objectif.titre,
          description: objectif.description,
          type: objectif.type,
          priorite: objectif.priorite,
          dateDebut: new Date(objectif.dateDebut),
          dateFin: new Date(objectif.dateFin),
          criteresMesure: objectif.criteresMesure,
          valeurCible: objectif.valeurCible,
          valeurActuelle: objectif.valeurActuelle,
          uniteMesure: objectif.uniteMesure
        });
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement de l\'objectif:', error);
        this.toastr.error('Erreur lors du chargement de l\'objectif');
        this.router.navigate(['/objectifs']);
        this.isLoading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.objectifForm.valid) {
      this.isLoading = true;
      const formData = this.objectifForm.value;

      // Formater les dates
      formData.dateDebut = this.formatDate(formData.dateDebut);
      formData.dateFin = this.formatDate(formData.dateFin);

      if (this.isEditMode && this.currentObjectif) {
        this.objectifService.updateObjectif(this.currentObjectif.id!, formData).subscribe({
          next: () => {
            this.toastr.success('Objectif modifié avec succès');
            this.router.navigate(['/objectifs']);
          },
          error: (error) => {
            console.error('Erreur lors de la modification:', error);
            this.toastr.error('Erreur lors de la modification de l\'objectif');
            this.isLoading = false;
          }
        });
      } else {
        this.objectifService.createObjectif(formData).subscribe({
          next: () => {
            this.toastr.success('Objectif créé avec succès');
            this.router.navigate(['/objectifs']);
          },
          error: (error) => {
            console.error('Erreur lors de la création:', error);
            this.toastr.error('Erreur lors de la création de l\'objectif');
            this.isLoading = false;
          }
        });
      }
    }
  }

  onDelete(): void {
    if (this.currentObjectif && confirm('Êtes-vous sûr de vouloir supprimer cet objectif ?')) {
      this.isLoading = true;
      this.objectifService.deleteObjectif(this.currentObjectif.id!).subscribe({
        next: () => {
          this.toastr.success('Objectif supprimé avec succès');
          this.router.navigate(['/objectifs']);
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          this.toastr.error('Erreur lors de la suppression de l\'objectif');
          this.isLoading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/objectifs']);
  }

  canDelete(): boolean {
    return this.currentObjectif?.statut !== StatutObjectif.TERMINE;
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  getStatutLabel(statut: StatutObjectif): string {
    const labels = {
      [StatutObjectif.EN_COURS]: 'En cours',
      [StatutObjectif.TERMINE]: 'Terminé',
      [StatutObjectif.EN_RETARD]: 'En retard',
      [StatutObjectif.SUSPENDU]: 'Suspendu'
    };
    return labels[statut] || statut;
  }

  getStatutClass(statut: StatutObjectif): string {
    const classes = {
      [StatutObjectif.EN_COURS]: 'statut-en-cours',
      [StatutObjectif.TERMINE]: 'statut-termine',
      [StatutObjectif.EN_RETARD]: 'statut-en-retard',
      [StatutObjectif.SUSPENDU]: 'statut-suspendu'
    };
    return classes[statut] || 'statut-en-cours';
  }

  getProgressBarClass(progression: number): string {
    if (progression < 25) return 'bg-danger';
    if (progression < 50) return 'bg-warning';
    if (progression < 75) return 'bg-info';
    return 'bg-success';
  }
}
