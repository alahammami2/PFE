import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { ToastrService } from 'ngx-toastr';

import { StatistiqueService } from '../../../services/statistique.service';

@Component({
  selector: 'app-statistique-equipe',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTabsModule,
    MatChipsModule,
    MatProgressBarModule,
    MatTableModule
  ],
  template: `
    <div class="container-fluid py-4">
      <div class="row">
        
        <!-- En-tête -->
        <div class="col-12 mb-4">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <h2 class="mb-1">
                <mat-icon class="me-2 text-primary">groups</mat-icon>
                Statistiques d'équipe
              </h2>
              <p class="text-muted mb-0">Analyse collective des performances et de la cohésion</p>
            </div>
            
            <div class="d-flex gap-2">
              <form [formGroup]="filterForm" class="d-flex gap-2">
                <mat-form-field appearance="outline">
                  <mat-label>Période</mat-label>
                  <mat-select formControlName="periode" (selectionChange)="onPeriodeChange()">
                    <mat-option value="30">30 derniers jours</mat-option>
                    <mat-option value="90">3 derniers mois</mat-option>
                    <mat-option value="180">6 derniers mois</mat-option>
                    <mat-option value="365">Année complète</mat-option>
                  </mat-select>
                </mat-form-field>
              </form>
              
              <button 
                mat-stroked-button 
                color="primary"
                (click)="goBack()">
                <mat-icon>arrow_back</mat-icon>
                Retour
              </button>
            </div>
          </div>
        </div>

        <!-- Métriques principales -->
        <div class="col-12 mb-4">
          <div class="row">
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-primary">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">group</mat-icon>
                  <div class="metric-value">{{ equipeStats?.nombreJoueurs || 0 }}</div>
                  <div class="metric-label">Joueurs actifs</div>
                  <div class="metric-subtitle">Dans l'équipe</div>
                </mat-card-content>
              </mat-card>
            </div>
            
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-success">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">event_available</mat-icon>
                  <div class="metric-value">{{ equipeStats?.tauxPresenceMoyen || 0 }}%</div>
                  <div class="metric-label">Taux de présence</div>
                  <div class="metric-subtitle">Moyenne équipe</div>
                </mat-card-content>
              </mat-card>
            </div>
            
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-warning">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">trending_up</mat-icon>
                  <div class="metric-value">{{ equipeStats?.performanceMoyenne || 0 }}/5</div>
                  <div class="metric-label">Performance moyenne</div>
                  <div class="metric-subtitle">Note globale</div>
                </mat-card-content>
              </mat-card>
            </div>
            
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-info">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">psychology</mat-icon>
                  <div class="metric-value">{{ equipeStats?.cohesionEquipe || 0 }}%</div>
                  <div class="metric-label">Cohésion d'équipe</div>
                  <div class="metric-subtitle">Indice calculé</div>
                </mat-card-content>
              </mat-card>
            </div>
          </div>
        </div>

        <!-- Contenu principal -->
        <div class="col-12">
          <mat-tab-group>
            
            <!-- Onglet Vue d'ensemble -->
            <mat-tab label="Vue d'ensemble">
              <div class="tab-content py-4">
                <div class="row">
                  
                  <!-- Répartition des performances -->
                  <div class="col-md-6 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Répartition des performances</mat-card-title>
                        <mat-card-subtitle>Par catégorie d'évaluation</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="performance-distribution" *ngIf="performanceDistribution">
                          <div class="category-item" *ngFor="let category of performanceCategories">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                              <span class="category-name">
                                <mat-icon class="me-2">{{ category.icon }}</mat-icon>
                                {{ category.label }}
                              </span>
                              <span class="category-score">{{ getPerformanceScore(category.key) }}/5</span>
                            </div>
                            <mat-progress-bar 
                              mode="determinate" 
                              [value]="getPerformancePercentage(category.key)"
                              [class]="getPerformanceBarClass(category.key)">
                            </mat-progress-bar>
                            <div class="category-details mt-1">
                              <small class="text-muted">
                                {{ getPerformanceDetails(category.key) }}
                              </small>
                            </div>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                  <!-- Évolution temporelle -->
                  <div class="col-md-6 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Évolution de l'équipe</mat-card-title>
                        <mat-card-subtitle>Tendances sur la période</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="chart-container">
                          <!-- TODO: Intégrer Chart.js -->
                          <div class="chart-placeholder">
                            <mat-icon class="chart-icon">show_chart</mat-icon>
                            <p>Graphique d'évolution</p>
                            <small class="text-muted">Chart.js sera intégré prochainement</small>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                </div>
              </div>
            </mat-tab>

            <!-- Onglet Classement -->
            <mat-tab label="Classement">
              <div class="tab-content py-4">
                <div class="row">
                  
                  <!-- Classement général -->
                  <div class="col-md-8 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Classement des joueurs</mat-card-title>
                        <mat-card-subtitle>Basé sur les performances globales</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="table-responsive">
                          <table mat-table [dataSource]="classementJoueurs" class="w-100">
                            
                            <!-- Colonne Position -->
                            <ng-container matColumnDef="position">
                              <th mat-header-cell *matHeaderCellDef>Position</th>
                              <td mat-cell *matCellDef="let element; let i = index">
                                <div class="position-cell">
                                  <span class="position-number" [class]="getPositionClass(i)">{{ i + 1 }}</span>
                                  <mat-icon *ngIf="i < 3" [class]="getMedalClass(i)">emoji_events</mat-icon>
                                </div>
                              </td>
                            </ng-container>

                            <!-- Colonne Joueur -->
                            <ng-container matColumnDef="joueur">
                              <th mat-header-cell *matHeaderCellDef>Joueur</th>
                              <td mat-cell *matCellDef="let element">
                                <div class="player-info">
                                  <div class="player-name">{{ element.nom }} {{ element.prenom }}</div>
                                  <div class="player-role">{{ element.poste }}</div>
                                </div>
                              </td>
                            </ng-container>

                            <!-- Colonne Performance -->
                            <ng-container matColumnDef="performance">
                              <th mat-header-cell *matHeaderCellDef>Performance</th>
                              <td mat-cell *matCellDef="let element">
                                <div class="performance-cell">
                                  <span class="performance-score">{{ element.noteGlobale }}/5</span>
                                  <mat-progress-bar 
                                    mode="determinate" 
                                    [value]="(element.noteGlobale / 5) * 100"
                                    class="performance-bar">
                                  </mat-progress-bar>
                                </div>
                              </td>
                            </ng-container>

                            <!-- Colonne Assiduité -->
                            <ng-container matColumnDef="assiduite">
                              <th mat-header-cell *matHeaderCellDef>Assiduité</th>
                              <td mat-cell *matCellDef="let element">
                                <mat-chip [class]="getAssiduitéClass(element.tauxPresence)">
                                  {{ element.tauxPresence }}%
                                </mat-chip>
                              </td>
                            </ng-container>

                            <!-- Colonne Évolution -->
                            <ng-container matColumnDef="evolution">
                              <th mat-header-cell *matHeaderCellDef>Évolution</th>
                              <td mat-cell *matCellDef="let element">
                                <div class="evolution-cell" [class]="getEvolutionClass(element.evolution)">
                                  <mat-icon>{{ getEvolutionIcon(element.evolution) }}</mat-icon>
                                  <span>{{ getEvolutionText(element.evolution) }}</span>
                                </div>
                              </td>
                            </ng-container>

                            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
                            <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
                          </table>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                  <!-- Statistiques du classement -->
                  <div class="col-md-4 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Statistiques</mat-card-title>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="classement-stats" *ngIf="classementStats">
                          <div class="stat-item">
                            <div class="stat-label">Meilleur joueur</div>
                            <div class="stat-value text-success">{{ classementStats.meilleurJoueur }}</div>
                          </div>
                          
                          <div class="stat-item">
                            <div class="stat-label">Note la plus élevée</div>
                            <div class="stat-value text-primary">{{ classementStats.meilleureNote }}/5</div>
                          </div>
                          
                          <div class="stat-item">
                            <div class="stat-label">Écart type</div>
                            <div class="stat-value text-info">{{ classementStats.ecartType }}</div>
                          </div>
                          
                          <div class="stat-item">
                            <div class="stat-label">Joueurs en progression</div>
                            <div class="stat-value text-success">{{ classementStats.joueursEnProgression }}</div>
                          </div>
                          
                          <div class="stat-item">
                            <div class="stat-label">Joueurs en difficulté</div>
                            <div class="stat-value text-warning">{{ classementStats.joueursEnDifficulte }}</div>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                </div>
              </div>
            </mat-tab>

            <!-- Onglet Analyse -->
            <mat-tab label="Analyse">
              <div class="tab-content py-4">
                <div class="row">
                  
                  <!-- Points forts -->
                  <div class="col-md-6 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title class="text-success">
                          <mat-icon class="me-2">thumb_up</mat-icon>
                          Points forts de l'équipe
                        </mat-card-title>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="points-list" *ngIf="pointsForts && pointsForts.length > 0">
                          <div *ngFor="let point of pointsForts" class="point-item point-fort">
                            <mat-icon class="point-icon text-success">check_circle</mat-icon>
                            <div class="point-content">
                              <div class="point-title">{{ point.titre }}</div>
                              <div class="point-description">{{ point.description }}</div>
                            </div>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                  <!-- Points d'amélioration -->
                  <div class="col-md-6 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title class="text-warning">
                          <mat-icon class="me-2">trending_up</mat-icon>
                          Points d'amélioration
                        </mat-card-title>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="points-list" *ngIf="pointsAmelioration && pointsAmelioration.length > 0">
                          <div *ngFor="let point of pointsAmelioration" class="point-item point-amelioration">
                            <mat-icon class="point-icon text-warning">warning</mat-icon>
                            <div class="point-content">
                              <div class="point-title">{{ point.titre }}</div>
                              <div class="point-description">{{ point.description }}</div>
                            </div>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                  <!-- Recommandations -->
                  <div class="col-12 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title class="text-primary">
                          <mat-icon class="me-2">lightbulb</mat-icon>
                          Recommandations
                        </mat-card-title>
                        <mat-card-subtitle>Suggestions pour améliorer les performances de l'équipe</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="recommandations-list" *ngIf="recommandations && recommandations.length > 0">
                          <div *ngFor="let reco of recommandations" class="recommandation-item">
                            <mat-icon class="reco-icon text-primary">{{ reco.icon }}</mat-icon>
                            <div class="reco-content">
                              <div class="reco-title">{{ reco.titre }}</div>
                              <div class="reco-description">{{ reco.description }}</div>
                              <mat-chip [class]="getPrioriteClass(reco.priorite)">
                                {{ reco.priorite }}
                              </mat-chip>
                            </div>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                </div>
              </div>
            </mat-tab>

          </mat-tab-group>
        </div>

      </div>
    </div>

    <!-- Loading overlay -->
    <div *ngIf="isLoading" class="loading-overlay">
      <mat-spinner diameter="50"></mat-spinner>
    </div>
  `,
  styles: [`
    .metric-card {
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      transition: transform 0.2s ease;
    }

    .metric-card:hover {
      transform: translateY(-2px);
    }

    .metric-card.metric-primary {
      border-left: 4px solid #007bff;
    }

    .metric-card.metric-success {
      border-left: 4px solid #28a745;
    }

    .metric-card.metric-warning {
      border-left: 4px solid #ffc107;
    }

    .metric-card.metric-info {
      border-left: 4px solid #17a2b8;
    }

    .metric-icon {
      font-size: 2.5rem;
      color: #666;
      margin-bottom: 0.5rem;
    }

    .metric-value {
      font-size: 2rem;
      font-weight: bold;
      color: #333;
      margin-bottom: 0.25rem;
    }

    .metric-label {
      font-size: 1rem;
      color: #666;
      font-weight: 500;
    }

    .metric-subtitle {
      font-size: 0.875rem;
      color: #999;
    }

    .performance-distribution {
      padding: 1rem 0;
    }

    .category-item {
      margin-bottom: 2rem;
    }

    .category-name {
      font-weight: 500;
      color: #333;
    }

    .category-score {
      font-weight: bold;
      color: #007bff;
    }

    .category-details {
      text-align: right;
    }

    .chart-container {
      height: 300px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background: #f8f9fa;
      border-radius: 8px;
      border: 2px dashed #dee2e6;
    }

    .chart-icon {
      font-size: 3rem;
      color: #6c757d;
      margin-bottom: 1rem;
    }

    /* Table styles */
    .position-cell {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    .position-number {
      font-weight: bold;
      font-size: 1.1rem;
    }

    .position-number.top-1 {
      color: #ffd700;
    }

    .position-number.top-2 {
      color: #c0c0c0;
    }

    .position-number.top-3 {
      color: #cd7f32;
    }

    .player-info {
      display: flex;
      flex-direction: column;
    }

    .player-name {
      font-weight: 500;
      color: #333;
    }

    .player-role {
      font-size: 0.875rem;
      color: #666;
    }

    .performance-cell {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .performance-score {
      font-weight: bold;
      color: #007bff;
    }

    .performance-bar {
      width: 100px;
    }

    .evolution-cell {
      display: flex;
      align-items: center;
      gap: 0.25rem;
    }

    .evolution-cell mat-icon {
      font-size: 1.2rem;
    }

    .classement-stats {
      padding: 1rem 0;
    }

    .stat-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 0;
      border-bottom: 1px solid #e9ecef;
    }

    .stat-item:last-child {
      border-bottom: none;
    }

    .stat-label {
      font-weight: 500;
      color: #666;
    }

    .stat-value {
      font-weight: bold;
    }

    .points-list {
      max-height: 300px;
      overflow-y: auto;
    }

    .point-item {
      display: flex;
      align-items: flex-start;
      padding: 1rem;
      margin-bottom: 0.5rem;
      border-radius: 8px;
      background: #f8f9fa;
    }

    .point-fort {
      border-left: 4px solid #28a745;
    }

    .point-amelioration {
      border-left: 4px solid #ffc107;
    }

    .point-icon {
      margin-right: 1rem;
      margin-top: 0.25rem;
    }

    .point-content {
      flex: 1;
    }

    .point-title {
      font-weight: 500;
      color: #333;
      margin-bottom: 0.25rem;
    }

    .point-description {
      font-size: 0.875rem;
      color: #666;
    }

    .recommandations-list {
      max-height: 400px;
      overflow-y: auto;
    }

    .recommandation-item {
      display: flex;
      align-items: flex-start;
      padding: 1.5rem;
      margin-bottom: 1rem;
      border-radius: 8px;
      background: #f8f9fa;
      border: 1px solid #e9ecef;
    }

    .reco-icon {
      margin-right: 1rem;
      margin-top: 0.25rem;
      font-size: 1.5rem;
    }

    .reco-content {
      flex: 1;
    }

    .reco-title {
      font-weight: 600;
      color: #333;
      margin-bottom: 0.5rem;
    }

    .reco-description {
      color: #666;
      margin-bottom: 0.75rem;
    }

    .tab-content {
      min-height: 400px;
    }

    .loading-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(255, 255, 255, 0.8);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
    }

    .gap-2 {
      gap: 0.5rem;
    }

    .text-primary {
      color: #007bff !important;
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

    .text-info {
      color: #17a2b8 !important;
    }

    .text-muted {
      color: #6c757d !important;
    }

    /* Chips styles */
    mat-chip.assiduite-excellente {
      background-color: #d1edff;
      color: #0f5132;
    }

    mat-chip.assiduite-bonne {
      background-color: #d1ecf1;
      color: #0c5460;
    }

    mat-chip.assiduite-moyenne {
      background-color: #fff3cd;
      color: #856404;
    }

    mat-chip.assiduite-faible {
      background-color: #f8d7da;
      color: #721c24;
    }

    mat-chip.priorite-haute {
      background-color: #f8d7da;
      color: #721c24;
    }

    mat-chip.priorite-moyenne {
      background-color: #fff3cd;
      color: #856404;
    }

    mat-chip.priorite-faible {
      background-color: #d1ecf1;
      color: #0c5460;
    }

    .gold {
      color: #ffd700;
    }

    .silver {
      color: #c0c0c0;
    }

    .bronze {
      color: #cd7f32;
    }

    mat-icon {
      vertical-align: middle;
    }
  `]
})
export class StatistiqueEquipeComponent implements OnInit {
  private router = inject(Router);
  private statistiqueService = inject(StatistiqueService);
  private toastr = inject(ToastrService);
  private fb = inject(FormBuilder);

  filterForm: FormGroup;
  isLoading = false;

  // Données
  equipeStats: any = null;
  performanceDistribution: any = null;
  classementJoueurs: any[] = [];
  classementStats: any = null;
  pointsForts: any[] = [];
  pointsAmelioration: any[] = [];
  recommandations: any[] = [];

  performanceCategories = [
    { key: 'technique', label: 'Technique', icon: 'sports_volleyball' },
    { key: 'physique', label: 'Physique', icon: 'fitness_center' },
    { key: 'tactique', label: 'Tactique', icon: 'psychology' },
    { key: 'mental', label: 'Mental', icon: 'psychology_alt' }
  ];

  displayedColumns: string[] = ['position', 'joueur', 'performance', 'assiduite', 'evolution'];

  constructor() {
    this.filterForm = this.fb.group({
      periode: [90] // 3 mois par défaut
    });
  }

  ngOnInit(): void {
    this.loadEquipeStats();
  }

  loadEquipeStats(): void {
    this.isLoading = true;
    const periode = this.filterForm.get('periode')?.value || 90;

    // Simuler des données d'équipe pour la démo
    this.equipeStats = {
      nombreJoueurs: 12,
      tauxPresenceMoyen: 87,
      performanceMoyenne: 3.8,
      cohesionEquipe: 82
    };

    this.performanceDistribution = {
      technique: { score: 4.2, details: 'Excellent niveau technique général' },
      physique: { score: 3.5, details: 'Condition physique à améliorer' },
      tactique: { score: 3.8, details: 'Bonne compréhension tactique' },
      mental: { score: 4.0, details: 'Mental solide et combatif' }
    };

    this.classementJoueurs = [
      { nom: 'Dupont', prenom: 'Jean', poste: 'Attaquant', noteGlobale: 4.5, tauxPresence: 95, evolution: 'positive' },
      { nom: 'Martin', prenom: 'Marie', poste: 'Libero', noteGlobale: 4.3, tauxPresence: 92, evolution: 'positive' },
      { nom: 'Bernard', prenom: 'Pierre', poste: 'Central', noteGlobale: 4.1, tauxPresence: 88, evolution: 'stable' },
      { nom: 'Durand', prenom: 'Sophie', poste: 'Passeuse', noteGlobale: 3.9, tauxPresence: 90, evolution: 'positive' },
      { nom: 'Moreau', prenom: 'Lucas', poste: 'Réceptionneur', noteGlobale: 3.7, tauxPresence: 85, evolution: 'stable' },
      { nom: 'Petit', prenom: 'Emma', poste: 'Attaquant', noteGlobale: 3.5, tauxPresence: 82, evolution: 'negative' }
    ];

    this.calculateClassementStats();

    // Charger l'analyse de l'équipe
    this.loadAnalyseEquipe();

    this.isLoading = false;
  }

  loadAnalyseEquipe(): void {
    // Simuler des données d'analyse pour la démo
    this.pointsForts = [
      {
        titre: 'Excellente assiduité',
        description: 'L\'équipe maintient un taux de présence élevé de 92%'
      },
      {
        titre: 'Progression technique',
        description: 'Amélioration notable des techniques de service et d\'attaque'
      },
      {
        titre: 'Cohésion d\'équipe',
        description: 'Bonne communication et entraide entre les joueurs'
      }
    ];

    this.pointsAmelioration = [
      {
        titre: 'Condition physique',
        description: 'Certains joueurs montrent des signes de fatigue en fin d\'entraînement'
      },
      {
        titre: 'Tactiques défensives',
        description: 'Les placements défensifs peuvent être améliorés'
      },
      {
        titre: 'Régularité',
        description: 'Quelques joueurs ont des performances irrégulières'
      }
    ];

    this.recommandations = [
      {
        titre: 'Renforcement physique',
        description: 'Intégrer des séances de préparation physique spécifiques',
        priorite: 'Haute',
        icon: 'fitness_center'
      },
      {
        titre: 'Travail tactique',
        description: 'Organiser des séances dédiées aux schémas défensifs',
        priorite: 'Moyenne',
        icon: 'psychology'
      },
      {
        titre: 'Suivi individuel',
        description: 'Mettre en place un accompagnement personnalisé pour les joueurs en difficulté',
        priorite: 'Moyenne',
        icon: 'person'
      }
    ];
  }

  calculateClassementStats(): void {
    if (this.classementJoueurs.length === 0) return;

    const notes = this.classementJoueurs.map(j => j.noteGlobale);
    const meilleureNote = Math.max(...notes);
    const moyenne = notes.reduce((a, b) => a + b, 0) / notes.length;
    const ecartType = Math.sqrt(notes.reduce((sq, n) => sq + Math.pow(n - moyenne, 2), 0) / notes.length);

    this.classementStats = {
      meilleurJoueur: this.classementJoueurs[0]?.nom + ' ' + this.classementJoueurs[0]?.prenom,
      meilleureNote: meilleureNote.toFixed(1),
      ecartType: ecartType.toFixed(2),
      joueursEnProgression: this.classementJoueurs.filter(j => j.evolution === 'positive').length,
      joueursEnDifficulte: this.classementJoueurs.filter(j => j.evolution === 'negative').length
    };
  }

  onPeriodeChange(): void {
    this.loadEquipeStats();
  }

  goBack(): void {
    this.router.navigate(['/statistiques']);
  }

  getPeriodeLabel(): string {
    const periode = this.filterForm.get('periode')?.value;
    switch (periode) {
      case 30: return '30 derniers jours';
      case 90: return '3 derniers mois';
      case 180: return '6 derniers mois';
      case 365: return 'Année complète';
      default: return '';
    }
  }

  getPerformanceScore(category: string): number {
    if (!this.performanceDistribution) return 0;
    return this.performanceDistribution[category]?.score || 0;
  }

  getPerformancePercentage(category: string): number {
    return (this.getPerformanceScore(category) / 5) * 100;
  }

  getPerformanceBarClass(category: string): string {
    const score = this.getPerformanceScore(category);
    if (score >= 4) return 'primary';
    if (score >= 3) return 'accent';
    if (score >= 2) return 'warn';
    return 'warn';
  }

  getPerformanceDetails(category: string): string {
    if (!this.performanceDistribution) return '';
    return this.performanceDistribution[category]?.details || '';
  }

  getPositionClass(index: number): string {
    if (index === 0) return 'top-1';
    if (index === 1) return 'top-2';
    if (index === 2) return 'top-3';
    return '';
  }

  getMedalClass(index: number): string {
    if (index === 0) return 'gold';
    if (index === 1) return 'silver';
    if (index === 2) return 'bronze';
    return '';
  }

  getAssiduitéClass(tauxPresence: number): string {
    if (tauxPresence >= 90) return 'assiduite-excellente';
    if (tauxPresence >= 80) return 'assiduite-bonne';
    if (tauxPresence >= 70) return 'assiduite-moyenne';
    return 'assiduite-faible';
  }

  getEvolutionIcon(evolution: string): string {
    switch (evolution) {
      case 'positive': return 'trending_up';
      case 'negative': return 'trending_down';
      case 'stable': return 'trending_flat';
      default: return 'help';
    }
  }

  getEvolutionClass(evolution: string): string {
    switch (evolution) {
      case 'positive': return 'text-success';
      case 'negative': return 'text-danger';
      case 'stable': return 'text-muted';
      default: return 'text-muted';
    }
  }

  getEvolutionText(evolution: string): string {
    switch (evolution) {
      case 'positive': return 'En progression';
      case 'negative': return 'En baisse';
      case 'stable': return 'Stable';
      default: return 'Inconnu';
    }
  }

  getPrioriteClass(priorite: string): string {
    switch (priorite.toLowerCase()) {
      case 'haute': return 'priorite-haute';
      case 'moyenne': return 'priorite-moyenne';
      case 'faible': return 'priorite-faible';
      default: return 'priorite-moyenne';
    }
  }
}
