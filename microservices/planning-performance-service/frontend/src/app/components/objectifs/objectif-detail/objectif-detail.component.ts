import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatSliderModule } from '@angular/material/slider';
import { MatTabsModule } from '@angular/material/tabs';
import { ToastrService } from 'ngx-toastr';

import { ObjectifService } from '../../../services/objectif.service';
import { ObjectifIndividuel, TypeObjectif, PrioriteObjectif, StatutObjectif } from '../../../models/objectif.model';

@Component({
  selector: 'app-objectif-detail',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatDividerModule,
    MatChipsModule,
    MatSliderModule,
    MatTabsModule
  ],
  template: `
    <div class="container-fluid py-4" *ngIf="objectif">
      <div class="row justify-content-center">
        <div class="col-lg-10">
          
          <!-- En-tête -->
          <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
              <h2 class="mb-1">
                <mat-icon class="me-2 text-primary">flag</mat-icon>
                {{ objectif.titre }}
              </h2>
              <p class="text-muted mb-0">Objectif #{{ objectif.id }}</p>
            </div>
            <div class="d-flex gap-2">
              <button 
                mat-stroked-button 
                color="primary"
                (click)="goBack()">
                <mat-icon>arrow_back</mat-icon>
                Retour
              </button>
              <button 
                *ngIf="canEdit()"
                mat-raised-button 
                color="primary"
                (click)="editObjectif()">
                <mat-icon>edit</mat-icon>
                Modifier
              </button>
            </div>
          </div>

          <mat-tab-group>
            <!-- Onglet Vue d'ensemble -->
            <mat-tab label="Vue d'ensemble">
              <div class="tab-content py-4">
                
                <!-- Progression globale -->
                <mat-card class="mb-4 progress-card">
                  <mat-card-content>
                    <div class="row align-items-center">
                      <div class="col-md-8">
                        <h5 class="mb-3">Progression actuelle</h5>
                        <div class="progress-info">
                          <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="progress-label">{{ objectif.progression }}% complété</span>
                            <mat-chip [class]="getStatutClass(objectif.statut)">
                              <mat-icon matChipAvatar>{{ getStatutIcon(objectif.statut) }}</mat-icon>
                              {{ getStatutLabel(objectif.statut) }}
                            </mat-chip>
                          </div>
                          <mat-progress-bar 
                            mode="determinate" 
                            [value]="objectif.progression"
                            [class]="getProgressBarClass(objectif.progression)">
                          </mat-progress-bar>
                        </div>
                      </div>
                      <div class="col-md-4 text-center">
                        <div class="circular-progress">
                          <div class="progress-circle" [style.background]="getCircularProgressStyle(objectif.progression)">
                            <span class="progress-text">{{ objectif.progression }}%</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </mat-card-content>
                </mat-card>

                <!-- Informations principales -->
                <div class="row">
                  <div class="col-md-8">
                    <mat-card class="mb-4">
                      <mat-card-header>
                        <mat-card-title>Informations générales</mat-card-title>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="info-grid">
                          <div class="info-item">
                            <label class="info-label">Description</label>
                            <div class="info-value">{{ objectif.description }}</div>
                          </div>

                          <div class="info-item">
                            <label class="info-label">Type d'objectif</label>
                            <div class="info-value">
                              <mat-chip [class]="getTypeClass(objectif.type)">
                                <mat-icon matChipAvatar>{{ getTypeIcon(objectif.type) }}</mat-icon>
                                {{ getTypeLabel(objectif.type) }}
                              </mat-chip>
                            </div>
                          </div>

                          <div class="info-item">
                            <label class="info-label">Priorité</label>
                            <div class="info-value">
                              <mat-chip [class]="getPrioriteClass(objectif.priorite)">
                                {{ getPrioriteLabel(objectif.priorite) }}
                              </mat-chip>
                            </div>
                          </div>

                          <div class="info-item">
                            <label class="info-label">Critères de mesure</label>
                            <div class="info-value">{{ objectif.criteresMesure }}</div>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>

                  <div class="col-md-4">
                    <mat-card class="mb-4">
                      <mat-card-header>
                        <mat-card-title>Échéances</mat-card-title>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="timeline">
                          <div class="timeline-item">
                            <mat-icon class="timeline-icon text-success">play_arrow</mat-icon>
                            <div class="timeline-content">
                              <div class="timeline-label">Début</div>
                              <div class="timeline-value">{{ objectif.dateDebut | date:'dd/MM/yyyy' }}</div>
                            </div>
                          </div>
                          
                          <div class="timeline-item">
                            <mat-icon class="timeline-icon text-primary">flag</mat-icon>
                            <div class="timeline-content">
                              <div class="timeline-label">Fin prévue</div>
                              <div class="timeline-value">{{ objectif.dateFin | date:'dd/MM/yyyy' }}</div>
                            </div>
                          </div>

                          <div class="timeline-item">
                            <mat-icon class="timeline-icon" [class]="getTimeRemainingClass()">schedule</mat-icon>
                            <div class="timeline-content">
                              <div class="timeline-label">Temps restant</div>
                              <div class="timeline-value">{{ getTimeRemaining() }}</div>
                            </div>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                </div>

                <!-- Métriques -->
                <mat-card class="mb-4">
                  <mat-card-header>
                    <mat-card-title>Métriques de performance</mat-card-title>
                  </mat-card-header>
                  <mat-card-content>
                    <div class="row text-center">
                      <div class="col-md-3">
                        <div class="metric-card">
                          <div class="metric-value">{{ objectif.valeurActuelle || 0 }}</div>
                          <div class="metric-label">Valeur actuelle</div>
                          <div class="metric-unit">{{ objectif.uniteMesure }}</div>
                        </div>
                      </div>
                      
                      <div class="col-md-3">
                        <div class="metric-card">
                          <div class="metric-value">{{ objectif.valeurCible }}</div>
                          <div class="metric-label">Valeur cible</div>
                          <div class="metric-unit">{{ objectif.uniteMesure }}</div>
                        </div>
                      </div>
                      
                      <div class="col-md-3">
                        <div class="metric-card">
                          <div class="metric-value" [class]="getEcartClass()">
                            {{ getEcart() }}
                          </div>
                          <div class="metric-label">Écart</div>
                          <div class="metric-unit">{{ objectif.uniteMesure }}</div>
                        </div>
                      </div>
                      
                      <div class="col-md-3">
                        <div class="metric-card">
                          <div class="metric-value text-primary">{{ getVitesseProgression() }}</div>
                          <div class="metric-label">Vitesse</div>
                          <div class="metric-unit">%/semaine</div>
                        </div>
                      </div>
                    </div>
                  </mat-card-content>
                </mat-card>

              </div>
            </mat-tab>

            <!-- Onglet Mise à jour progression -->
            <mat-tab label="Mise à jour">
              <div class="tab-content py-4">
                <mat-card>
                  <mat-card-header>
                    <mat-card-title>
                      <mat-icon class="me-2">trending_up</mat-icon>
                      Mettre à jour la progression
                    </mat-card-title>
                    <mat-card-subtitle>
                      Enregistrez vos progrès et ajoutez des commentaires
                    </mat-card-subtitle>
                  </mat-card-header>
                  <mat-card-content>
                    <form [formGroup]="progressionForm" (ngSubmit)="updateProgression()" class="progression-form">
                      
                      <div class="row mb-3">
                        <div class="col-md-6">
                          <mat-form-field appearance="outline" class="w-100">
                            <mat-label>Nouvelle valeur actuelle</mat-label>
                            <input 
                              matInput 
                              type="number" 
                              formControlName="valeurActuelle"
                              [placeholder]="objectif.valeurActuelle?.toString() || '0'">
                            <span matTextSuffix>{{ objectif.uniteMesure }}</span>
                          </mat-form-field>
                        </div>
                        
                        <div class="col-md-6">
                          <label class="form-label">Progression (%)</label>
                          <mat-slider 
                            class="w-100"
                            [min]="0" 
                            [max]="100" 
                            [step]="1"
                            [value]="progressionForm.get('progression')?.value"
                            (input)="onProgressionChange($event)">
                          </mat-slider>
                          <div class="text-center mt-2">
                            <strong>{{ progressionForm.get('progression')?.value }}%</strong>
                          </div>
                        </div>
                      </div>

                      <div class="row mb-3">
                        <div class="col-12">
                          <mat-form-field appearance="outline" class="w-100">
                            <mat-label>Commentaire sur les progrès</mat-label>
                            <textarea 
                              matInput 
                              formControlName="commentaire" 
                              rows="3"
                              placeholder="Décrivez vos progrès, difficultés rencontrées, prochaines étapes...">
                            </textarea>
                          </mat-form-field>
                        </div>
                      </div>

                      <div class="d-flex justify-content-end">
                        <button 
                          type="submit" 
                          mat-raised-button 
                          color="primary"
                          [disabled]="progressionForm.invalid || isLoading">
                          <mat-spinner *ngIf="isLoading" diameter="20" class="me-2"></mat-spinner>
                          <mat-icon *ngIf="!isLoading">save</mat-icon>
                          Enregistrer les progrès
                        </button>
                      </div>

                    </form>
                  </mat-card-content>
                </mat-card>
              </div>
            </mat-tab>

            <!-- Onglet Historique -->
            <mat-tab label="Historique">
              <div class="tab-content py-4">
                <mat-card>
                  <mat-card-header>
                    <mat-card-title>
                      <mat-icon class="me-2">history</mat-icon>
                      Historique des mises à jour
                    </mat-card-title>
                  </mat-card-header>
                  <mat-card-content>
                    <div class="timeline-history">
                      <!-- TODO: Implémenter l'historique des mises à jour -->
                      <div class="text-center text-muted py-4">
                        <mat-icon class="mb-2" style="font-size: 48px;">history</mat-icon>
                        <p>L'historique des mises à jour sera disponible prochainement</p>
                      </div>
                    </div>
                  </mat-card-content>
                </mat-card>
              </div>
            </mat-tab>

          </mat-tab-group>

        </div>
      </div>
    </div>

    <!-- Loading state -->
    <div *ngIf="!objectif && isLoading" class="d-flex justify-content-center align-items-center" style="height: 400px;">
      <mat-spinner diameter="50"></mat-spinner>
    </div>
  `,
  styles: [`
    .progress-card {
      background: linear-gradient(135deg, #f8f9fa 0%, #e3f2fd 100%);
      border: 2px solid #e3f2fd;
    }

    .progress-info {
      margin-top: 1rem;
    }

    .progress-label {
      font-weight: 600;
      font-size: 1.1rem;
    }

    .circular-progress {
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .progress-circle {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
    }

    .progress-text {
      font-size: 1.5rem;
      font-weight: bold;
      color: white;
      text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
    }

    .info-grid {
      display: grid;
      gap: 1.5rem;
    }

    .info-item {
      margin-bottom: 1rem;
    }

    .info-label {
      font-weight: 600;
      color: #666;
      font-size: 0.875rem;
      display: block;
      margin-bottom: 0.25rem;
    }

    .info-value {
      color: #333;
      font-size: 1rem;
    }

    .timeline {
      position: relative;
    }

    .timeline-item {
      display: flex;
      align-items: center;
      margin-bottom: 1.5rem;
      position: relative;
    }

    .timeline-icon {
      margin-right: 1rem;
      font-size: 24px;
    }

    .timeline-content {
      flex: 1;
    }

    .timeline-label {
      font-weight: 600;
      color: #666;
      font-size: 0.875rem;
    }

    .timeline-value {
      color: #333;
      font-size: 1rem;
    }

    .metric-card {
      padding: 1.5rem;
      border: 1px solid #e9ecef;
      border-radius: 8px;
      background: #f8f9fa;
      margin-bottom: 1rem;
    }

    .metric-value {
      font-size: 2rem;
      font-weight: bold;
      color: #333;
    }

    .metric-label {
      font-size: 0.875rem;
      color: #666;
      margin-top: 0.5rem;
    }

    .metric-unit {
      font-size: 0.75rem;
      color: #999;
    }

    .tab-content {
      min-height: 400px;
    }

    .progression-form {
      max-width: 600px;
    }

    .gap-2 {
      gap: 0.5rem;
    }

    .text-success {
      color: #28a745 !important;
    }

    .text-warning {
      color: #ffc107 !important;
    }

    .text-danger {
      color: #dc3545 !important;
    }

    .text-primary {
      color: #007bff !important;
    }

    .text-muted {
      color: #6c757d !important;
    }

    /* Chips styles */
    mat-chip.type-technique {
      background-color: #e3f2fd;
      color: #1976d2;
    }

    mat-chip.type-physique {
      background-color: #f3e5f5;
      color: #7b1fa2;
    }

    mat-chip.type-tactique {
      background-color: #e8f5e8;
      color: #388e3c;
    }

    mat-chip.type-mental {
      background-color: #fff3e0;
      color: #f57c00;
    }

    mat-chip.type-personnel {
      background-color: #fce4ec;
      color: #c2185b;
    }

    mat-chip.priorite-faible {
      background-color: #e8f5e8;
      color: #388e3c;
    }

    mat-chip.priorite-moyenne {
      background-color: #fff3e0;
      color: #f57c00;
    }

    mat-chip.priorite-haute {
      background-color: #ffebee;
      color: #d32f2f;
    }

    mat-chip.statut-en-cours {
      background-color: #e3f2fd;
      color: #1976d2;
    }

    mat-chip.statut-termine {
      background-color: #e8f5e8;
      color: #388e3c;
    }

    mat-chip.statut-en-retard {
      background-color: #ffebee;
      color: #d32f2f;
    }

    mat-chip.statut-suspendu {
      background-color: #f5f5f5;
      color: #616161;
    }

    mat-icon {
      vertical-align: middle;
    }
  `]
})
export class ObjectifDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private objectifService = inject(ObjectifService);
  private toastr = inject(ToastrService);
  private fb = inject(FormBuilder);

  objectif: ObjectifIndividuel | null = null;
  isLoading = false;

  progressionForm: FormGroup;

  constructor() {
    this.progressionForm = this.fb.group({
      progression: [0, [Validators.min(0), Validators.max(100)]],
      valeurActuelle: [0, [Validators.min(0)]],
      commentaire: ['']
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadObjectif(+id);
    }
  }

  loadObjectif(id: number): void {
    this.isLoading = true;
    this.objectifService.getObjectifById(id).subscribe({
      next: (objectif) => {
        this.objectif = objectif;
        this.progressionForm.patchValue({
          progression: objectif.progression,
          valeurActuelle: objectif.valeurActuelle || 0
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

  updateProgression(): void {
    if (!this.objectif || this.progressionForm.invalid) return;

    this.isLoading = true;
    const formData = this.progressionForm.value;

    this.objectifService.updateProgression(
      this.objectif.id!,
      formData.progression,
      formData.commentaire
    ).subscribe({
      next: (updatedObjectif) => {
        this.objectif = updatedObjectif;
        this.toastr.success('Progression mise à jour avec succès');
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors de la mise à jour:', error);
        this.toastr.error('Erreur lors de la mise à jour de la progression');
        this.isLoading = false;
      }
    });
  }

  onProgressionChange(event: any): void {
    this.progressionForm.patchValue({
      progression: event.value
    });
  }

  editObjectif(): void {
    if (this.objectif) {
      this.router.navigate(['/objectifs', this.objectif.id, 'edit']);
    }
  }

  goBack(): void {
    this.router.navigate(['/objectifs']);
  }

  canEdit(): boolean {
    return this.objectif?.statut !== StatutObjectif.TERMINE;
  }

  // Méthodes utilitaires pour l'affichage
  getTypeLabel(type: TypeObjectif): string {
    const labels = {
      [TypeObjectif.TECHNIQUE]: 'Technique',
      [TypeObjectif.PHYSIQUE]: 'Physique',
      [TypeObjectif.TACTIQUE]: 'Tactique',
      [TypeObjectif.MENTAL]: 'Mental',
      [TypeObjectif.PERSONNEL]: 'Personnel'
    };
    return labels[type] || type;
  }

  getTypeIcon(type: TypeObjectif): string {
    const icons = {
      [TypeObjectif.TECHNIQUE]: 'sports_volleyball',
      [TypeObjectif.PHYSIQUE]: 'fitness_center',
      [TypeObjectif.TACTIQUE]: 'psychology',
      [TypeObjectif.MENTAL]: 'psychology_alt',
      [TypeObjectif.PERSONNEL]: 'person'
    };
    return icons[type] || 'flag';
  }

  getTypeClass(type: TypeObjectif): string {
    return `type-${type.toLowerCase()}`;
  }

  getPrioriteLabel(priorite: PrioriteObjectif): string {
    const labels = {
      [PrioriteObjectif.FAIBLE]: 'Faible',
      [PrioriteObjectif.MOYENNE]: 'Moyenne',
      [PrioriteObjectif.HAUTE]: 'Haute'
    };
    return labels[priorite] || priorite;
  }

  getPrioriteClass(priorite: PrioriteObjectif): string {
    return `priorite-${priorite.toLowerCase()}`;
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

  getStatutIcon(statut: StatutObjectif): string {
    const icons = {
      [StatutObjectif.EN_COURS]: 'play_circle',
      [StatutObjectif.TERMINE]: 'check_circle',
      [StatutObjectif.EN_RETARD]: 'warning',
      [StatutObjectif.SUSPENDU]: 'pause_circle'
    };
    return icons[statut] || 'help';
  }

  getStatutClass(statut: StatutObjectif): string {
    return `statut-${statut.toLowerCase().replace('_', '-')}`;
  }

  getProgressBarClass(progression: number): string {
    if (progression < 25) return 'warn';
    if (progression < 75) return 'accent';
    return 'primary';
  }

  getCircularProgressStyle(progression: number): string {
    const hue = (progression / 100) * 120; // De rouge (0) à vert (120)
    return `conic-gradient(hsl(${hue}, 70%, 50%) ${progression}%, #e0e0e0 ${progression}%)`;
  }

  getTimeRemaining(): string {
    if (!this.objectif) return '';
    
    const today = new Date();
    const endDate = new Date(this.objectif.dateFin);
    const diffTime = endDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) return 'Échéance dépassée';
    if (diffDays === 0) return 'Aujourd\'hui';
    if (diffDays === 1) return '1 jour';
    if (diffDays < 7) return `${diffDays} jours`;
    if (diffDays < 30) return `${Math.ceil(diffDays / 7)} semaines`;
    return `${Math.ceil(diffDays / 30)} mois`;
  }

  getTimeRemainingClass(): string {
    if (!this.objectif) return 'text-muted';
    
    const today = new Date();
    const endDate = new Date(this.objectif.dateFin);
    const diffTime = endDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) return 'text-danger';
    if (diffDays < 7) return 'text-warning';
    return 'text-success';
  }

  getEcart(): number {
    if (!this.objectif) return 0;
    return this.objectif.valeurCible - (this.objectif.valeurActuelle || 0);
  }

  getEcartClass(): string {
    const ecart = this.getEcart();
    if (ecart <= 0) return 'text-success';
    if (ecart <= this.objectif!.valeurCible * 0.1) return 'text-warning';
    return 'text-danger';
  }

  getVitesseProgression(): string {
    if (!this.objectif) return '0';
    
    const startDate = new Date(this.objectif.dateDebut);
    const today = new Date();
    const diffTime = today.getTime() - startDate.getTime();
    const diffWeeks = diffTime / (1000 * 60 * 60 * 24 * 7);
    
    if (diffWeeks <= 0) return '0';
    return (this.objectif.progression / diffWeeks).toFixed(1);
  }
}
