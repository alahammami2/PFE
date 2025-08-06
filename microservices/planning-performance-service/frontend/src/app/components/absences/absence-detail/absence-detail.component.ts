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
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';

import { AbsenceService } from '../../../services/absence.service';
import { EntrainementService } from '../../../services/entrainement.service';
import { Absence, TypeAbsence, StatutAbsence } from '../../../models/absence.model';
import { Entrainement } from '../../../models/entrainement.model';

@Component({
  selector: 'app-absence-detail',
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
    MatDividerModule,
    MatChipsModule,
    MatDialogModule
  ],
  template: `
    <div class="container-fluid py-4" *ngIf="absence">
      <div class="row justify-content-center">
        <div class="col-lg-8">
          
          <!-- En-tête -->
          <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
              <h2 class="mb-1">
                <mat-icon class="me-2 text-warning">event_busy</mat-icon>
                Détails de l'absence
              </h2>
              <p class="text-muted mb-0">Absence #{{ absence.id }}</p>
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
                (click)="editAbsence()">
                <mat-icon>edit</mat-icon>
                Modifier
              </button>
            </div>
          </div>

          <!-- Informations principales -->
          <mat-card class="mb-4">
            <mat-card-header>
              <mat-card-title>Informations générales</mat-card-title>
            </mat-card-header>
            <mat-card-content>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <div class="info-item">
                    <label class="info-label">Entraînement concerné</label>
                    <div class="info-value" *ngIf="entrainement">
                      <strong>{{ entrainement.titre }}</strong><br>
                      <small class="text-muted">
                        {{ entrainement.date | date:'dd/MM/yyyy' }} à {{ entrainement.heureDebut }}
                        <br>{{ entrainement.lieu }}
                      </small>
                    </div>
                  </div>
                </div>
                
                <div class="col-md-6 mb-3">
                  <div class="info-item">
                    <label class="info-label">Type d'absence</label>
                    <div class="info-value">
                      <mat-chip-set>
                        <mat-chip [class]="getTypeClass(absence.type)">
                          <mat-icon matChipAvatar>{{ getTypeIcon(absence.type) }}</mat-icon>
                          {{ getTypeLabel(absence.type) }}
                        </mat-chip>
                      </mat-chip-set>
                    </div>
                  </div>
                </div>

                <div class="col-md-6 mb-3">
                  <div class="info-item">
                    <label class="info-label">Statut</label>
                    <div class="info-value">
                      <mat-chip-set>
                        <mat-chip [class]="getStatutClass(absence.statut)">
                          <mat-icon matChipAvatar>{{ getStatutIcon(absence.statut) }}</mat-icon>
                          {{ getStatutLabel(absence.statut) }}
                        </mat-chip>
                      </mat-chip-set>
                    </div>
                  </div>
                </div>

                <div class="col-md-6 mb-3">
                  <div class="info-item">
                    <label class="info-label">Justification fournie</label>
                    <div class="info-value">
                      <mat-icon [class]="absence.justificationFournie ? 'text-success' : 'text-muted'">
                        {{ absence.justificationFournie ? 'check_circle' : 'cancel' }}
                      </mat-icon>
                      {{ absence.justificationFournie ? 'Oui' : 'Non' }}
                    </div>
                  </div>
                </div>

                <div class="col-12 mb-3">
                  <div class="info-item">
                    <label class="info-label">Motif</label>
                    <div class="info-value">
                      {{ absence.motif }}
                    </div>
                  </div>
                </div>

                <div class="col-12 mb-3" *ngIf="absence.commentaire">
                  <div class="info-item">
                    <label class="info-label">Commentaire</label>
                    <div class="info-value">
                      {{ absence.commentaire }}
                    </div>
                  </div>
                </div>
              </div>
            </mat-card-content>
          </mat-card>

          <!-- Historique et traitement -->
          <mat-card class="mb-4">
            <mat-card-header>
              <mat-card-title>Historique et traitement</mat-card-title>
            </mat-card-header>
            <mat-card-content>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <div class="info-item">
                    <label class="info-label">Date de déclaration</label>
                    <div class="info-value">
                      {{ absence.dateDeclaration | date:'dd/MM/yyyy à HH:mm' }}
                    </div>
                  </div>
                </div>

                <div class="col-md-6 mb-3" *ngIf="absence.dateTraitement">
                  <div class="info-item">
                    <label class="info-label">Date de traitement</label>
                    <div class="info-value">
                      {{ absence.dateTraitement | date:'dd/MM/yyyy à HH:mm' }}
                    </div>
                  </div>
                </div>

                <div class="col-12 mb-3" *ngIf="absence.commentaireApprobation">
                  <div class="info-item">
                    <label class="info-label">Commentaire d'approbation</label>
                    <div class="info-value">
                      {{ absence.commentaireApprobation }}
                    </div>
                  </div>
                </div>
              </div>
            </mat-card-content>
          </mat-card>

          <!-- Actions d'approbation (pour les coaches/admins) -->
          <mat-card *ngIf="canApprove()" class="approval-card">
            <mat-card-header>
              <mat-card-title class="text-primary">
                <mat-icon class="me-2">gavel</mat-icon>
                Actions d'approbation
              </mat-card-title>
              <mat-card-subtitle>
                Approuvez ou rejetez cette demande d'absence
              </mat-card-subtitle>
            </mat-card-header>
            <mat-card-content>
              <form [formGroup]="approvalForm" class="approval-form">
                <div class="row mb-3">
                  <div class="col-12">
                    <mat-form-field appearance="outline" class="w-100">
                      <mat-label>Commentaire d'approbation</mat-label>
                      <textarea 
                        matInput 
                        formControlName="commentaireApprobation" 
                        rows="3"
                        placeholder="Ajoutez un commentaire sur votre décision...">
                      </textarea>
                    </mat-form-field>
                  </div>
                </div>

                <div class="d-flex justify-content-end gap-2">
                  <button 
                    type="button"
                    mat-stroked-button 
                    color="warn"
                    (click)="rejectAbsence()"
                    [disabled]="isLoading">
                    <mat-spinner *ngIf="isLoading && actionType === 'reject'" diameter="20" class="me-2"></mat-spinner>
                    <mat-icon *ngIf="!isLoading || actionType !== 'reject'">close</mat-icon>
                    Rejeter
                  </button>
                  
                  <button 
                    type="button"
                    mat-raised-button 
                    color="primary"
                    (click)="approveAbsence()"
                    [disabled]="isLoading">
                    <mat-spinner *ngIf="isLoading && actionType === 'approve'" diameter="20" class="me-2"></mat-spinner>
                    <mat-icon *ngIf="!isLoading || actionType !== 'approve'">check</mat-icon>
                    Approuver
                  </button>
                </div>
              </form>
            </mat-card-content>
          </mat-card>

        </div>
      </div>
    </div>

    <!-- Loading state -->
    <div *ngIf="!absence && isLoading" class="d-flex justify-content-center align-items-center" style="height: 400px;">
      <mat-spinner diameter="50"></mat-spinner>
    </div>
  `,
  styles: [`
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

    .approval-card {
      border: 2px solid #e3f2fd;
      background: linear-gradient(135deg, #f8f9fa 0%, #e3f2fd 100%);
    }

    .approval-form {
      margin-top: 1rem;
    }

    .gap-2 {
      gap: 0.5rem;
    }

    .text-success {
      color: #28a745 !important;
    }

    .text-muted {
      color: #6c757d !important;
    }

    .text-primary {
      color: #007bff !important;
    }

    .text-warning {
      color: #ffc107 !important;
    }

    mat-chip.type-maladie {
      background-color: #fff3cd;
      color: #856404;
    }

    mat-chip.type-blessure {
      background-color: #f8d7da;
      color: #721c24;
    }

    mat-chip.type-urgence {
      background-color: #d1ecf1;
      color: #0c5460;
    }

    mat-chip.type-travail {
      background-color: #d4edda;
      color: #155724;
    }

    mat-chip.type-autre {
      background-color: #e2e3e5;
      color: #383d41;
    }

    mat-chip.statut-attente {
      background-color: #fff3cd;
      color: #856404;
    }

    mat-chip.statut-approuvee {
      background-color: #d1edff;
      color: #0f5132;
    }

    mat-chip.statut-rejetee {
      background-color: #f8d7da;
      color: #721c24;
    }

    mat-icon {
      vertical-align: middle;
    }
  `]
})
export class AbsenceDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private absenceService = inject(AbsenceService);
  private entrainementService = inject(EntrainementService);
  private toastr = inject(ToastrService);
  private fb = inject(FormBuilder);
  private dialog = inject(MatDialog);

  absence: Absence | null = null;
  entrainement: Entrainement | null = null;
  isLoading = false;
  actionType: 'approve' | 'reject' | null = null;

  approvalForm: FormGroup;

  constructor() {
    this.approvalForm = this.fb.group({
      commentaireApprobation: ['']
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadAbsence(+id);
    }
  }

  loadAbsence(id: number): void {
    this.isLoading = true;
    this.absenceService.getAbsenceById(id).subscribe({
      next: (absence) => {
        this.absence = absence;
        this.loadEntrainement(absence.entrainementId);
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

  loadEntrainement(entrainementId: number): void {
    this.entrainementService.getEntrainementById(entrainementId).subscribe({
      next: (entrainement) => {
        this.entrainement = entrainement;
      },
      error: (error) => {
        console.error('Erreur lors du chargement de l\'entraînement:', error);
      }
    });
  }

  approveAbsence(): void {
    if (!this.absence) return;

    this.actionType = 'approve';
    this.isLoading = true;
    
    const commentaire = this.approvalForm.get('commentaireApprobation')?.value || '';
    
    this.absenceService.approuverAbsence(this.absence.id!, commentaire).subscribe({
      next: (updatedAbsence) => {
        this.absence = updatedAbsence;
        this.toastr.success('Absence approuvée avec succès');
        this.isLoading = false;
        this.actionType = null;
      },
      error: (error) => {
        console.error('Erreur lors de l\'approbation:', error);
        this.toastr.error('Erreur lors de l\'approbation de l\'absence');
        this.isLoading = false;
        this.actionType = null;
      }
    });
  }

  rejectAbsence(): void {
    if (!this.absence) return;

    this.actionType = 'reject';
    this.isLoading = true;
    
    const commentaire = this.approvalForm.get('commentaireApprobation')?.value || '';
    
    this.absenceService.rejeterAbsence(this.absence.id!, commentaire).subscribe({
      next: (updatedAbsence) => {
        this.absence = updatedAbsence;
        this.toastr.success('Absence rejetée');
        this.isLoading = false;
        this.actionType = null;
      },
      error: (error) => {
        console.error('Erreur lors du rejet:', error);
        this.toastr.error('Erreur lors du rejet de l\'absence');
        this.isLoading = false;
        this.actionType = null;
      }
    });
  }

  editAbsence(): void {
    if (this.absence) {
      this.router.navigate(['/absences', this.absence.id, 'edit']);
    }
  }

  goBack(): void {
    this.router.navigate(['/absences']);
  }

  canEdit(): boolean {
    return this.absence?.statut === StatutAbsence.EN_ATTENTE;
  }

  canApprove(): boolean {
    // TODO: Vérifier les permissions utilisateur (coach/admin)
    return this.absence?.statut === StatutAbsence.EN_ATTENTE;
  }

  getTypeLabel(type: TypeAbsence): string {
    const labels = {
      [TypeAbsence.MALADIE]: 'Maladie',
      [TypeAbsence.BLESSURE]: 'Blessure',
      [TypeAbsence.URGENCE_FAMILIALE]: 'Urgence familiale',
      [TypeAbsence.OBLIGATIONS_PROFESSIONNELLES]: 'Obligations professionnelles',
      [TypeAbsence.OBLIGATIONS_SCOLAIRES]: 'Obligations scolaires',
      [TypeAbsence.CONGES]: 'Congés',
      [TypeAbsence.AUTRE]: 'Autre'
    };
    return labels[type] || type;
  }

  getTypeIcon(type: TypeAbsence): string {
    const icons = {
      [TypeAbsence.MALADIE]: 'sick',
      [TypeAbsence.BLESSURE]: 'healing',
      [TypeAbsence.URGENCE_FAMILIALE]: 'family_restroom',
      [TypeAbsence.OBLIGATIONS_PROFESSIONNELLES]: 'work',
      [TypeAbsence.OBLIGATIONS_SCOLAIRES]: 'school',
      [TypeAbsence.CONGES]: 'beach_access',
      [TypeAbsence.AUTRE]: 'help_outline'
    };
    return icons[type] || 'help_outline';
  }

  getTypeClass(type: TypeAbsence): string {
    const classes = {
      [TypeAbsence.MALADIE]: 'type-maladie',
      [TypeAbsence.BLESSURE]: 'type-blessure',
      [TypeAbsence.URGENCE_FAMILIALE]: 'type-urgence',
      [TypeAbsence.OBLIGATIONS_PROFESSIONNELLES]: 'type-travail',
      [TypeAbsence.OBLIGATIONS_SCOLAIRES]: 'type-travail',
      [TypeAbsence.CONGES]: 'type-autre',
      [TypeAbsence.AUTRE]: 'type-autre'
    };
    return classes[type] || 'type-autre';
  }

  getStatutLabel(statut: StatutAbsence): string {
    const labels = {
      [StatutAbsence.EN_ATTENTE]: 'En attente',
      [StatutAbsence.APPROUVEE]: 'Approuvée',
      [StatutAbsence.REJETEE]: 'Rejetée'
    };
    return labels[statut] || statut;
  }

  getStatutIcon(statut: StatutAbsence): string {
    const icons = {
      [StatutAbsence.EN_ATTENTE]: 'schedule',
      [StatutAbsence.APPROUVEE]: 'check_circle',
      [StatutAbsence.REJETEE]: 'cancel'
    };
    return icons[statut] || 'help_outline';
  }

  getStatutClass(statut: StatutAbsence): string {
    const classes = {
      [StatutAbsence.EN_ATTENTE]: 'statut-attente',
      [StatutAbsence.APPROUVEE]: 'statut-approuvee',
      [StatutAbsence.REJETEE]: 'statut-rejetee'
    };
    return classes[statut] || 'statut-attente';
  }
}
