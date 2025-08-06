import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ToastrService } from 'ngx-toastr';

import { StatistiqueService } from '../../../services/statistique.service';
import { ChartDataService } from '../../../shared/services/chart-data.service';
import {
  LineChartComponent,
  BarChartComponent,
  RadarChartComponent,
  ChartData,
  ChartOptions
} from '../../../shared/components/chart/chart.component';

@Component({
  selector: 'app-statistique-joueur',
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
    LineChartComponent,
    BarChartComponent,
    RadarChartComponent
  ],
  template: `
    <div class="container-fluid py-4">
      <div class="row">
        
        <!-- En-tête -->
        <div class="col-12 mb-4">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <h2 class="mb-1">
                <mat-icon class="me-2 text-primary">person</mat-icon>
                Statistiques joueur
              </h2>
              <p class="text-muted mb-0" *ngIf="selectedJoueur">
                Analyse détaillée des performances de {{ selectedJoueur.nom }}
              </p>
            </div>
            
            <div class="d-flex gap-2">
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

        <!-- Sélection du joueur -->
        <div class="col-12 mb-4">
          <mat-card>
            <mat-card-content>
              <form [formGroup]="filterForm" class="d-flex gap-3 align-items-end">
                <mat-form-field appearance="outline" class="flex-grow-1">
                  <mat-label>Sélectionner un joueur</mat-label>
                  <mat-select formControlName="joueurId" (selectionChange)="onJoueurChange()">
                    <mat-option *ngFor="let joueur of joueurs" [value]="joueur.id">
                      {{ joueur.nom }} {{ joueur.prenom }}
                    </mat-option>
                  </mat-select>
                </mat-form-field>
                
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
            </mat-card-content>
          </mat-card>
        </div>

        <!-- Contenu principal -->
        <div class="col-12" *ngIf="selectedJoueur">
          
          <!-- Métriques principales -->
          <div class="row mb-4">
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-primary">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">event_available</mat-icon>
                  <div class="metric-value">{{ joueurStats?.participations || 0 }}</div>
                  <div class="metric-label">Participations</div>
                  <div class="metric-subtitle">{{ getPeriodeLabel() }}</div>
                </mat-card-content>
              </mat-card>
            </div>
            
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-success">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">check_circle</mat-icon>
                  <div class="metric-value">{{ joueurStats?.tauxPresence || 0 }}%</div>
                  <div class="metric-label">Taux de présence</div>
                  <div class="metric-subtitle">Assiduité</div>
                </mat-card-content>
              </mat-card>
            </div>
            
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-warning">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">schedule</mat-icon>
                  <div class="metric-value">{{ joueurStats?.heuresEntrainement || 0 }}h</div>
                  <div class="metric-label">Heures d'entraînement</div>
                  <div class="metric-subtitle">Temps total</div>
                </mat-card-content>
              </mat-card>
            </div>
            
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-info">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">trending_up</mat-icon>
                  <div class="metric-value">{{ joueurStats?.noteGlobale || 0 }}/5</div>
                  <div class="metric-label">Note globale</div>
                  <div class="metric-subtitle">Performance moyenne</div>
                </mat-card-content>
              </mat-card>
            </div>
          </div>

          <mat-tab-group>
            
            <!-- Onglet Performances -->
            <mat-tab label="Performances">
              <div class="tab-content py-4">
                <div class="row">
                  
                  <!-- Évaluation par catégorie -->
                  <div class="col-md-6 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Évaluation par catégorie</mat-card-title>
                        <mat-card-subtitle>Dernières performances enregistrées</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="performance-categories" *ngIf="performanceDetails">
                          <div class="category-item" *ngFor="let category of performanceCategories">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                              <span class="category-name">
                                <mat-icon class="me-2">{{ category.icon }}</mat-icon>
                                {{ category.label }}
                              </span>
                              <span class="category-score">
                                {{ getPerformanceScore(category.key) }}/5
                              </span>
                            </div>
                            <mat-progress-bar 
                              mode="determinate" 
                              [value]="getPerformancePercentage(category.key)"
                              [class]="getPerformanceBarClass(category.key)">
                            </mat-progress-bar>
                            <div class="category-evolution mt-1">
                              <small [class]="getEvolutionClass(category.key)">
                                <mat-icon class="evolution-icon">{{ getEvolutionIcon(category.key) }}</mat-icon>
                                {{ getEvolutionText(category.key) }}
                              </small>
                            </div>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                  <!-- Historique des notes -->
                  <div class="col-md-6 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Évolution des performances</mat-card-title>
                        <mat-card-subtitle>Tendance sur la période</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <app-radar-chart
                          [data]="performanceRadarData"
                          [options]="performanceRadarOptions"
                          height="300px">
                        </app-radar-chart>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                </div>
              </div>
            </mat-tab>

            <!-- Onglet Assiduité -->
            <mat-tab label="Assiduité">
              <div class="tab-content py-4">
                <div class="row">
                  
                  <!-- Calendrier de présence -->
                  <div class="col-md-8 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Calendrier de présence</mat-card-title>
                        <mat-card-subtitle>Historique des participations</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="presence-calendar">
                          <!-- TODO: Implémenter un calendrier de présence -->
                          <div class="calendar-placeholder">
                            <mat-icon class="calendar-icon">calendar_month</mat-icon>
                            <p>Calendrier de présence</p>
                            <small class="text-muted">Calendrier interactif à venir</small>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                  <!-- Statistiques d'assiduité -->
                  <div class="col-md-4 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Détails assiduité</mat-card-title>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="assiduite-details" *ngIf="assiduitéStats">
                          <div class="detail-item">
                            <div class="detail-label">Présences</div>
                            <div class="detail-value text-success">{{ assiduitéStats.presences }}</div>
                          </div>
                          
                          <div class="detail-item">
                            <div class="detail-label">Absences</div>
                            <div class="detail-value text-warning">{{ assiduitéStats.absences }}</div>
                          </div>
                          
                          <div class="detail-item">
                            <div class="detail-label">Retards</div>
                            <div class="detail-value text-danger">{{ assiduitéStats.retards }}</div>
                          </div>
                          
                          <div class="detail-item">
                            <div class="detail-label">Série actuelle</div>
                            <div class="detail-value text-primary">
                              {{ assiduitéStats.serieActuelle }} {{ assiduitéStats.typeSerieActuelle }}
                            </div>
                          </div>
                          
                          <div class="detail-item">
                            <div class="detail-label">Meilleure série</div>
                            <div class="detail-value text-info">
                              {{ assiduitéStats.meilleureSerie }} présences
                            </div>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                </div>
              </div>
            </mat-tab>

            <!-- Onglet Objectifs -->
            <mat-tab label="Objectifs">
              <div class="tab-content py-4">
                <div class="row">
                  
                  <!-- Objectifs actifs -->
                  <div class="col-md-8 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Objectifs personnels</mat-card-title>
                        <mat-card-subtitle>Progression des objectifs actifs</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="objectifs-list" *ngIf="objectifsJoueur && objectifsJoueur.length > 0">
                          <div *ngFor="let objectif of objectifsJoueur" class="objectif-item">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                              <span class="objectif-title">{{ objectif.titre }}</span>
                              <mat-chip [class]="getObjectifStatutClass(objectif.statut)">
                                {{ objectif.statut }}
                              </mat-chip>
                            </div>
                            <div class="d-flex justify-content-between align-items-center mb-1">
                              <small class="text-muted">{{ objectif.type }}</small>
                              <small class="text-primary">{{ objectif.progression }}%</small>
                            </div>
                            <mat-progress-bar 
                              mode="determinate" 
                              [value]="objectif.progression"
                              [class]="getObjectifProgressClass(objectif.progression)">
                            </mat-progress-bar>
                            <div class="objectif-meta mt-1">
                              <small class="text-muted">
                                Échéance: {{ objectif.dateFin | date:'dd/MM/yyyy' }}
                              </small>
                            </div>
                          </div>
                        </div>
                        <div *ngIf="!objectifsJoueur || objectifsJoueur.length === 0" class="text-center text-muted py-4">
                          <mat-icon class="mb-2">flag_outline</mat-icon>
                          <p>Aucun objectif défini</p>
                          <button mat-raised-button color="primary" (click)="createObjectif()">
                            <mat-icon>add</mat-icon>
                            Créer un objectif
                          </button>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                  <!-- Résumé objectifs -->
                  <div class="col-md-4 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Résumé</mat-card-title>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="objectifs-summary" *ngIf="objectifsSummary">
                          <div class="summary-item">
                            <div class="summary-label">Total objectifs</div>
                            <div class="summary-value">{{ objectifsSummary.total }}</div>
                          </div>
                          
                          <div class="summary-item">
                            <div class="summary-label">En cours</div>
                            <div class="summary-value text-primary">{{ objectifsSummary.enCours }}</div>
                          </div>
                          
                          <div class="summary-item">
                            <div class="summary-label">Terminés</div>
                            <div class="summary-value text-success">{{ objectifsSummary.termines }}</div>
                          </div>
                          
                          <div class="summary-item">
                            <div class="summary-label">En retard</div>
                            <div class="summary-value text-danger">{{ objectifsSummary.enRetard }}</div>
                          </div>
                          
                          <div class="summary-item">
                            <div class="summary-label">Progression moyenne</div>
                            <div class="summary-value text-info">{{ objectifsSummary.progressionMoyenne }}%</div>
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

        <!-- Message si aucun joueur sélectionné -->
        <div class="col-12" *ngIf="!selectedJoueur">
          <mat-card class="text-center py-5">
            <mat-card-content>
              <mat-icon class="mb-3" style="font-size: 4rem; color: #ccc;">person_outline</mat-icon>
              <h4 class="text-muted">Sélectionnez un joueur</h4>
              <p class="text-muted">Choisissez un joueur dans la liste pour voir ses statistiques détaillées</p>
            </mat-card-content>
          </mat-card>
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

    .performance-categories {
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

    .category-evolution {
      text-align: right;
    }

    .evolution-icon {
      font-size: 1rem;
      vertical-align: middle;
    }

    .chart-container, .calendar-placeholder {
      height: 300px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background: #f8f9fa;
      border-radius: 8px;
      border: 2px dashed #dee2e6;
    }

    .chart-icon, .calendar-icon {
      font-size: 3rem;
      color: #6c757d;
      margin-bottom: 1rem;
    }

    .assiduite-details, .objectifs-summary {
      padding: 1rem 0;
    }

    .detail-item, .summary-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 0;
      border-bottom: 1px solid #e9ecef;
    }

    .detail-item:last-child, .summary-item:last-child {
      border-bottom: none;
    }

    .detail-label, .summary-label {
      font-weight: 500;
      color: #666;
    }

    .detail-value, .summary-value {
      font-weight: bold;
    }

    .objectifs-list {
      max-height: 400px;
      overflow-y: auto;
    }

    .objectif-item {
      margin-bottom: 1.5rem;
      padding: 1rem;
      background: #f8f9fa;
      border-radius: 8px;
    }

    .objectif-title {
      font-weight: 500;
      color: #333;
    }

    .objectif-meta {
      margin-top: 0.5rem;
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

    .gap-3 {
      gap: 1rem;
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
    mat-chip.objectif-en-cours {
      background-color: #d1ecf1;
      color: #0c5460;
    }

    mat-chip.objectif-termine {
      background-color: #d1edff;
      color: #0f5132;
    }

    mat-chip.objectif-en-retard {
      background-color: #f8d7da;
      color: #721c24;
    }

    mat-chip.objectif-suspendu {
      background-color: #e2e3e5;
      color: #383d41;
    }

    mat-icon {
      vertical-align: middle;
    }
  `]
})
export class StatistiqueJoueurComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private statistiqueService = inject(StatistiqueService);
  private chartDataService = inject(ChartDataService);
  private toastr = inject(ToastrService);
  private fb = inject(FormBuilder);

  filterForm: FormGroup;
  isLoading = false;

  // Données
  joueurs: any[] = [];
  selectedJoueur: any = null;
  joueurStats: any = null;
  performanceDetails: any = null;
  assiduitéStats: any = null;
  objectifsJoueur: any[] = [];
  objectifsSummary: any = null;

  // Données des graphiques
  performanceRadarData: ChartData = { labels: [], datasets: [] };
  performanceRadarOptions: ChartOptions = {};
  performanceEvolutionData: ChartData = { labels: [], datasets: [] };
  performanceEvolutionOptions: ChartOptions = {};
  presenceData: ChartData = { labels: [], datasets: [] };
  presenceOptions: ChartOptions = {};

  performanceCategories = [
    { key: 'technique', label: 'Technique', icon: 'sports_volleyball' },
    { key: 'physique', label: 'Physique', icon: 'fitness_center' },
    { key: 'tactique', label: 'Tactique', icon: 'psychology' },
    { key: 'mental', label: 'Mental', icon: 'psychology_alt' }
  ];

  constructor() {
    this.filterForm = this.fb.group({
      joueurId: [''],
      periode: [90] // 3 mois par défaut
    });
  }

  ngOnInit(): void {
    this.loadJoueurs();
    
    // Vérifier si un joueur est spécifié dans l'URL
    const joueurId = this.route.snapshot.paramMap.get('id');
    if (joueurId) {
      this.filterForm.patchValue({ joueurId: +joueurId });
      this.onJoueurChange();
    }
  }

  loadJoueurs(): void {
    // TODO: Charger la liste des joueurs depuis le service
    // Pour la démo, utiliser des données simulées
    this.joueurs = [
      { id: 1, nom: 'Dupont', prenom: 'Jean' },
      { id: 2, nom: 'Martin', prenom: 'Marie' },
      { id: 3, nom: 'Bernard', prenom: 'Pierre' },
      { id: 4, nom: 'Durand', prenom: 'Sophie' },
      { id: 5, nom: 'Moreau', prenom: 'Lucas' }
    ];
  }

  onJoueurChange(): void {
    const joueurId = this.filterForm.get('joueurId')?.value;
    if (joueurId) {
      this.selectedJoueur = this.joueurs.find(j => j.id === joueurId);
      this.loadJoueurStats();
      this.initializePlayerCharts();
    } else {
      this.selectedJoueur = null;
    }
  }

  onPeriodeChange(): void {
    if (this.selectedJoueur) {
      this.loadJoueurStats();
    }
  }

  loadJoueurStats(): void {
    if (!this.selectedJoueur) return;

    this.isLoading = true;
    const periode = this.filterForm.get('periode')?.value || 90;

    // Charger les statistiques du joueur
    this.statistiqueService.getStatistiquesJoueur(this.selectedJoueur.id, periode).subscribe({
      next: (stats) => {
        this.joueurStats = stats;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des statistiques:', error);
      }
    });

    // Charger les détails de performance
    this.statistiqueService.getPerformanceJoueur(this.selectedJoueur.id, periode).subscribe({
      next: (performance) => {
        this.performanceDetails = performance;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des performances:', error);
      }
    });

    // Charger les statistiques d'assiduité
    this.statistiqueService.getAssiduitéJoueur(this.selectedJoueur.id, periode).subscribe({
      next: (assiduité) => {
        this.assiduitéStats = assiduité;
      },
      error: (error) => {
        console.error('Erreur lors du chargement de l\'assiduité:', error);
      }
    });

    // TODO: Charger les objectifs du joueur
    this.loadObjectifsJoueur();

    this.isLoading = false;
  }

  loadObjectifsJoueur(): void {
    // Simuler des données d'objectifs pour la démo
    this.objectifsJoueur = [
      {
        titre: 'Améliorer le service',
        type: 'Technique',
        statut: 'EN_COURS',
        progression: 75,
        dateFin: new Date('2024-12-31')
      },
      {
        titre: 'Condition physique',
        type: 'Physique',
        statut: 'EN_COURS',
        progression: 60,
        dateFin: new Date('2024-11-30')
      }
    ];

    this.objectifsSummary = {
      total: 5,
      enCours: 2,
      termines: 2,
      enRetard: 1,
      progressionMoyenne: 68
    };
  }

  goBack(): void {
    this.router.navigate(['/statistiques']);
  }

  createObjectif(): void {
    this.router.navigate(['/objectifs/new']);
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
    if (!this.performanceDetails) return 0;
    return this.performanceDetails[category]?.score || 0;
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

  getEvolutionIcon(category: string): string {
    if (!this.performanceDetails) return 'trending_flat';
    const evolution = this.performanceDetails[category]?.evolution || 'stable';
    switch (evolution) {
      case 'positive': return 'trending_up';
      case 'negative': return 'trending_down';
      default: return 'trending_flat';
    }
  }

  getEvolutionClass(category: string): string {
    if (!this.performanceDetails) return 'text-muted';
    const evolution = this.performanceDetails[category]?.evolution || 'stable';
    switch (evolution) {
      case 'positive': return 'text-success';
      case 'negative': return 'text-danger';
      default: return 'text-muted';
    }
  }

  getEvolutionText(category: string): string {
    if (!this.performanceDetails) return 'Stable';
    const evolution = this.performanceDetails[category]?.evolution || 'stable';
    switch (evolution) {
      case 'positive': return 'En progression';
      case 'negative': return 'En baisse';
      default: return 'Stable';
    }
  }

  getObjectifStatutClass(statut: string): string {
    switch (statut) {
      case 'EN_COURS': return 'objectif-en-cours';
      case 'TERMINE': return 'objectif-termine';
      case 'EN_RETARD': return 'objectif-en-retard';
      case 'SUSPENDU': return 'objectif-suspendu';
      default: return 'objectif-en-cours';
    }
  }

  getObjectifProgressClass(progression: number): string {
    if (progression >= 75) return 'primary';
    if (progression >= 50) return 'accent';
    if (progression >= 25) return 'warn';
    return 'warn';
  }

  private initializePlayerCharts(): void {
    if (!this.selectedJoueur) return;

    // Graphique radar des performances actuelles
    this.performanceRadarData = this.chartDataService.generatePerformanceRadarData({
      technique: 3.8,
      physique: 3.5,
      tactique: 4.0,
      mental: 3.7
    });
    this.performanceRadarOptions = this.chartDataService.getRadarChartOptions();

    // Graphique d'évolution des performances
    const demoData = this.chartDataService.generateDemoData();
    this.performanceEvolutionData = this.chartDataService.generatePerformanceEvolutionData(demoData.performances);
    this.performanceEvolutionOptions = this.chartDataService.getPerformanceChartOptions();

    // Graphique de présence
    this.presenceData = this.chartDataService.generatePresenceData(demoData.presences);
    this.presenceOptions = this.chartDataService.getBarChartOptions('Évolution de la présence');
  }
}
