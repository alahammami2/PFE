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
import { MatGridListModule } from '@angular/material/grid-list';
import { MatChipsModule } from '@angular/material/chips';
import { ToastrService } from 'ngx-toastr';

import { StatistiqueService } from '../../../services/statistique.service';
import { ChartDataService } from '../../../shared/services/chart-data.service';
import {
  LineChartComponent,
  BarChartComponent,
  PieChartComponent,
  RadarChartComponent,
  ChartData,
  ChartOptions
} from '../../../shared/components/chart/chart.component';
import { EntrainementService } from '../../../services/entrainement.service';

@Component({
  selector: 'app-statistique-dashboard',
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
    MatGridListModule,
    MatChipsModule,
    LineChartComponent,
    BarChartComponent,
    PieChartComponent,
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
                <mat-icon class="me-2 text-primary">analytics</mat-icon>
                Tableau de bord statistiques
              </h2>
              <p class="text-muted mb-0">Analyse des performances et tendances d'entraînement</p>
            </div>
            
            <div class="d-flex gap-2">
              <form [formGroup]="filterForm" class="d-flex gap-2">
                <mat-form-field appearance="outline">
                  <mat-label>Période</mat-label>
                  <mat-select formControlName="periode" (selectionChange)="onPeriodeChange()">
                    <mat-option value="7">7 derniers jours</mat-option>
                    <mat-option value="30">30 derniers jours</mat-option>
                    <mat-option value="90">3 derniers mois</mat-option>
                    <mat-option value="365">Année complète</mat-option>
                  </mat-select>
                </mat-form-field>
              </form>
            </div>
          </div>
        </div>

        <!-- Métriques principales -->
        <div class="col-12 mb-4">
          <div class="row">
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-primary">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">fitness_center</mat-icon>
                  <div class="metric-value">{{ globalStats?.totalEntrainements || 0 }}</div>
                  <div class="metric-label">Entraînements</div>
                  <div class="metric-subtitle">{{ getPeriodeLabel() }}</div>
                </mat-card-content>
              </mat-card>
            </div>
            
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-success">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">group</mat-icon>
                  <div class="metric-value">{{ globalStats?.totalParticipations || 0 }}</div>
                  <div class="metric-label">Participations</div>
                  <div class="metric-subtitle">{{ getPeriodeLabel() }}</div>
                </mat-card-content>
              </mat-card>
            </div>
            
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-warning">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">schedule</mat-icon>
                  <div class="metric-value">{{ globalStats?.dureeMoyenne || 0 }}h</div>
                  <div class="metric-label">Durée moyenne</div>
                  <div class="metric-subtitle">Par entraînement</div>
                </mat-card-content>
              </mat-card>
            </div>
            
            <div class="col-md-3 mb-3">
              <mat-card class="metric-card metric-info">
                <mat-card-content class="text-center">
                  <mat-icon class="metric-icon">trending_up</mat-icon>
                  <div class="metric-value">{{ globalStats?.tauxPresence || 0 }}%</div>
                  <div class="metric-label">Taux de présence</div>
                  <div class="metric-subtitle">Moyenne équipe</div>
                </mat-card-content>
              </mat-card>
            </div>
          </div>
        </div>

        <!-- Graphiques et analyses -->
        <div class="col-12">
          <mat-tab-group>
            
            <!-- Onglet Vue d'ensemble -->
            <mat-tab label="Vue d'ensemble">
              <div class="tab-content py-4">
                <div class="row">
                  
                  <!-- Graphique des entraînements -->
                  <div class="col-md-8 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Évolution des entraînements</mat-card-title>
                        <mat-card-subtitle>Nombre d'entraînements par semaine</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <app-line-chart
                          [data]="performanceEvolutionData"
                          [options]="performanceEvolutionOptions"
                          height="300px">
                        </app-line-chart>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                  <!-- Top joueurs -->
                  <div class="col-md-4 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Top joueurs</mat-card-title>
                        <mat-card-subtitle>Meilleure assiduité</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="top-players" *ngIf="topJoueurs && topJoueurs.length > 0">
                          <div 
                            *ngFor="let joueur of topJoueurs; let i = index" 
                            class="player-item"
                            [class]="getPlayerRankClass(i)">
                            <div class="player-rank">{{ i + 1 }}</div>
                            <div class="player-info">
                              <div class="player-name">{{ joueur.nom }}</div>
                              <div class="player-stats">{{ joueur.tauxPresence }}% présence</div>
                            </div>
                            <mat-icon class="player-medal" [class]="getMedalClass(i)">
                              {{ getMedalIcon(i) }}
                            </mat-icon>
                          </div>
                        </div>
                        <div *ngIf="!topJoueurs || topJoueurs.length === 0" class="text-center text-muted py-4">
                          <mat-icon class="mb-2">person_outline</mat-icon>
                          <p>Aucune donnée disponible</p>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                </div>
              </div>
            </mat-tab>

            <!-- Onglet Performances -->
            <mat-tab label="Performances">
              <div class="tab-content py-4">
                <div class="row">
                  
                  <!-- Analyse des performances -->
                  <div class="col-md-6 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Analyse des performances</mat-card-title>
                        <mat-card-subtitle>Répartition par catégorie</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="performance-analysis" *ngIf="performanceStats">
                          <div class="performance-category" *ngFor="let cat of performanceCategories">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                              <span class="category-name">
                                <mat-icon class="me-2">{{ cat.icon }}</mat-icon>
                                {{ cat.label }}
                              </span>
                              <span class="category-score">{{ getPerformanceScore(cat.key) }}/5</span>
                            </div>
                            <div class="progress">
                              <div 
                                class="progress-bar" 
                                [style.width.%]="getPerformancePercentage(cat.key)"
                                [class]="getPerformanceBarClass(cat.key)">
                              </div>
                            </div>
                          </div>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                  <!-- Tendances -->
                  <div class="col-md-6 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Tendances</mat-card-title>
                        <mat-card-subtitle>Évolution récente</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="trends-list" *ngIf="tendances && tendances.length > 0">
                          <div *ngFor="let tendance of tendances" class="trend-item">
                            <mat-icon [class]="getTrendIconClass(tendance.type)">
                              {{ getTrendIcon(tendance.type) }}
                            </mat-icon>
                            <div class="trend-content">
                              <div class="trend-title">{{ tendance.titre }}</div>
                              <div class="trend-description">{{ tendance.description }}</div>
                            </div>
                            <mat-chip [class]="getTrendChipClass(tendance.type)">
                              {{ tendance.valeur }}
                            </mat-chip>
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
                  
                  <!-- Progression des objectifs -->
                  <div class="col-md-8 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Progression des objectifs</mat-card-title>
                        <mat-card-subtitle>Objectifs actifs de l'équipe</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="objectifs-progress" *ngIf="objectifsStats && objectifsStats.length > 0">
                          <div *ngFor="let objectif of objectifsStats" class="objectif-item">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                              <span class="objectif-title">{{ objectif.titre }}</span>
                              <span class="objectif-progress">{{ objectif.progression }}%</span>
                            </div>
                            <div class="progress">
                              <div 
                                class="progress-bar" 
                                [style.width.%]="objectif.progression"
                                [class]="getObjectifProgressClass(objectif.progression)">
                              </div>
                            </div>
                            <div class="objectif-meta">
                              <small class="text-muted">
                                {{ objectif.type }} • {{ objectif.echeance | date:'dd/MM/yyyy' }}
                              </small>
                            </div>
                          </div>
                        </div>
                        <div *ngIf="!objectifsStats || objectifsStats.length === 0" class="text-center text-muted py-4">
                          <mat-icon class="mb-2">flag_outline</mat-icon>
                          <p>Aucun objectif actif</p>
                        </div>
                      </mat-card-content>
                    </mat-card>
                  </div>
                  
                  <!-- Répartition par type -->
                  <div class="col-md-4 mb-4">
                    <mat-card>
                      <mat-card-header>
                        <mat-card-title>Répartition</mat-card-title>
                        <mat-card-subtitle>Par type d'objectif</mat-card-subtitle>
                      </mat-card-header>
                      <mat-card-content>
                        <div class="objectifs-repartition" *ngIf="objectifsRepartition">
                          <div *ngFor="let item of objectifsRepartition" class="repartition-item">
                            <div class="d-flex justify-content-between align-items-center">
                              <span class="type-name">
                                <mat-icon class="me-2">{{ getObjectifTypeIcon(item.type) }}</mat-icon>
                                {{ item.type }}
                              </span>
                              <span class="type-count">{{ item.count }}</span>
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

    .chart-container {
      height: 300px;
      position: relative;
    }

    .chart-placeholder {
      height: 100%;
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

    .top-players {
      max-height: 300px;
      overflow-y: auto;
    }

    .player-item {
      display: flex;
      align-items: center;
      padding: 0.75rem;
      margin-bottom: 0.5rem;
      border-radius: 8px;
      background: #f8f9fa;
      transition: background-color 0.2s ease;
    }

    .player-item:hover {
      background: #e9ecef;
    }

    .player-item.rank-1 {
      background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
    }

    .player-item.rank-2 {
      background: linear-gradient(135deg, #c0c0c0 0%, #e5e5e5 100%);
    }

    .player-item.rank-3 {
      background: linear-gradient(135deg, #cd7f32 0%, #daa520 100%);
    }

    .player-rank {
      font-size: 1.25rem;
      font-weight: bold;
      margin-right: 1rem;
      min-width: 30px;
    }

    .player-info {
      flex: 1;
    }

    .player-name {
      font-weight: 600;
      color: #333;
    }

    .player-stats {
      font-size: 0.875rem;
      color: #666;
    }

    .player-medal {
      font-size: 1.5rem;
    }

    .player-medal.gold {
      color: #ffd700;
    }

    .player-medal.silver {
      color: #c0c0c0;
    }

    .player-medal.bronze {
      color: #cd7f32;
    }

    .performance-analysis {
      padding: 1rem 0;
    }

    .performance-category {
      margin-bottom: 1.5rem;
    }

    .category-name {
      font-weight: 500;
      color: #333;
    }

    .category-score {
      font-weight: bold;
      color: #007bff;
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

    .progress-bar.bg-excellent {
      background-color: #28a745;
    }

    .progress-bar.bg-good {
      background-color: #17a2b8;
    }

    .progress-bar.bg-average {
      background-color: #ffc107;
    }

    .progress-bar.bg-poor {
      background-color: #dc3545;
    }

    .trends-list {
      max-height: 300px;
      overflow-y: auto;
    }

    .trend-item {
      display: flex;
      align-items: center;
      padding: 0.75rem;
      margin-bottom: 0.5rem;
      border-radius: 8px;
      background: #f8f9fa;
    }

    .trend-content {
      flex: 1;
      margin: 0 1rem;
    }

    .trend-title {
      font-weight: 500;
      color: #333;
    }

    .trend-description {
      font-size: 0.875rem;
      color: #666;
    }

    .objectifs-progress {
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

    .objectif-progress {
      font-weight: bold;
      color: #007bff;
    }

    .objectif-meta {
      margin-top: 0.5rem;
    }

    .repartition-item {
      padding: 0.5rem 0;
      border-bottom: 1px solid #e9ecef;
    }

    .repartition-item:last-child {
      border-bottom: none;
    }

    .type-name {
      font-weight: 500;
      color: #333;
    }

    .type-count {
      font-weight: bold;
      color: #007bff;
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

    .text-muted {
      color: #6c757d !important;
    }

    mat-icon {
      vertical-align: middle;
    }

    /* Chips styles */
    mat-chip.trend-positive {
      background-color: #d1edff;
      color: #0f5132;
    }

    mat-chip.trend-negative {
      background-color: #f8d7da;
      color: #721c24;
    }

    mat-chip.trend-stable {
      background-color: #fff3cd;
      color: #856404;
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
  `]
})
export class StatistiqueDashboardComponent implements OnInit {
  private statistiqueService = inject(StatistiqueService);
  private entrainementService = inject(EntrainementService);
  private chartDataService = inject(ChartDataService);
  private toastr = inject(ToastrService);
  private fb = inject(FormBuilder);
  private router = inject(Router);

  filterForm: FormGroup;
  isLoading = false;

  // Données statistiques
  globalStats: any = null;
  topJoueurs: any[] = [];
  performanceStats: any = null;
  tendances: any[] = [];
  objectifsStats: any[] = [];
  objectifsRepartition: any[] = [];

  // Données des graphiques
  performanceEvolutionData: ChartData = { labels: [], datasets: [] };
  performanceEvolutionOptions: ChartOptions = {};
  presenceData: ChartData = { labels: [], datasets: [] };
  presenceOptions: ChartOptions = {};
  absenceDistributionData: ChartData = { labels: [], datasets: [] };
  absenceDistributionOptions: ChartOptions = {};
  objectifsProgressData: ChartData = { labels: [], datasets: [] };
  objectifsProgressOptions: ChartOptions = {};

  performanceCategories = [
    { key: 'technique', label: 'Technique', icon: 'sports_volleyball' },
    { key: 'physique', label: 'Physique', icon: 'fitness_center' },
    { key: 'tactique', label: 'Tactique', icon: 'psychology' },
    { key: 'mental', label: 'Mental', icon: 'psychology_alt' }
  ];

  constructor() {
    this.filterForm = this.fb.group({
      periode: [30] // 30 jours par défaut
    });
  }

  ngOnInit(): void {
    this.loadDashboardData();
    this.initializeCharts();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    const periode = this.filterForm.get('periode')?.value || 30;

    // Charger les statistiques globales
    this.statistiqueService.getStatistiquesGlobales(periode).subscribe({
      next: (stats) => {
        this.globalStats = stats;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des statistiques globales:', error);
      }
    });

    // Charger le top des joueurs
    this.statistiqueService.getTopJoueurs(periode, 5).subscribe({
      next: (joueurs) => {
        this.topJoueurs = joueurs;
      },
      error: (error) => {
        console.error('Erreur lors du chargement du top joueurs:', error);
      }
    });

    // Charger les statistiques de performance
    this.statistiqueService.getAnalysePerformanceEquipe(periode).subscribe({
      next: (stats) => {
        this.performanceStats = stats;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des performances:', error);
      }
    });

    // Charger les tendances
    this.statistiqueService.getTendancesEquipe(periode).subscribe({
      next: (tendances) => {
        this.tendances = tendances;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des tendances:', error);
      }
    });

    // Simuler des données d'objectifs pour la démo
    this.objectifsStats = [
      { titre: 'Améliorer le service', progression: 75, type: 'Technique', echeance: new Date('2024-12-31') },
      { titre: 'Condition physique', progression: 60, type: 'Physique', echeance: new Date('2024-11-30') },
      { titre: 'Tactiques défensives', progression: 45, type: 'Tactique', echeance: new Date('2024-10-15') }
    ];

    this.objectifsRepartition = [
      { type: 'Technique', count: 8 },
      { type: 'Physique', count: 5 },
      { type: 'Tactique', count: 3 },
      { type: 'Mental', count: 2 }
    ];

    this.isLoading = false;
  }

  onPeriodeChange(): void {
    this.loadDashboardData();
  }

  getPeriodeLabel(): string {
    const periode = this.filterForm.get('periode')?.value;
    switch (periode) {
      case 7: return '7 derniers jours';
      case 30: return '30 derniers jours';
      case 90: return '3 derniers mois';
      case 365: return 'Année complète';
      default: return '';
    }
  }

  getPlayerRankClass(index: number): string {
    if (index === 0) return 'rank-1';
    if (index === 1) return 'rank-2';
    if (index === 2) return 'rank-3';
    return '';
  }

  getMedalIcon(index: number): string {
    if (index === 0) return 'emoji_events';
    if (index === 1) return 'emoji_events';
    if (index === 2) return 'emoji_events';
    return 'star';
  }

  getMedalClass(index: number): string {
    if (index === 0) return 'gold';
    if (index === 1) return 'silver';
    if (index === 2) return 'bronze';
    return '';
  }

  getPerformanceScore(category: string): number {
    if (!this.performanceStats) return 0;
    return this.performanceStats[category]?.score || 0;
  }

  getPerformancePercentage(category: string): number {
    return (this.getPerformanceScore(category) / 5) * 100;
  }

  getPerformanceBarClass(category: string): string {
    const score = this.getPerformanceScore(category);
    if (score >= 4) return 'bg-excellent';
    if (score >= 3) return 'bg-good';
    if (score >= 2) return 'bg-average';
    return 'bg-poor';
  }

  getTrendIcon(type: string): string {
    switch (type) {
      case 'positive': return 'trending_up';
      case 'negative': return 'trending_down';
      case 'stable': return 'trending_flat';
      default: return 'help';
    }
  }

  getTrendIconClass(type: string): string {
    switch (type) {
      case 'positive': return 'text-success';
      case 'negative': return 'text-danger';
      case 'stable': return 'text-warning';
      default: return 'text-muted';
    }
  }

  getTrendChipClass(type: string): string {
    switch (type) {
      case 'positive': return 'trend-positive';
      case 'negative': return 'trend-negative';
      case 'stable': return 'trend-stable';
      default: return '';
    }
  }

  getObjectifProgressClass(progression: number): string {
    if (progression >= 75) return 'bg-excellent';
    if (progression >= 50) return 'bg-good';
    if (progression >= 25) return 'bg-average';
    return 'bg-poor';
  }

  getObjectifTypeIcon(type: string): string {
    switch (type.toLowerCase()) {
      case 'technique': return 'sports_volleyball';
      case 'physique': return 'fitness_center';
      case 'tactique': return 'psychology';
      case 'mental': return 'psychology_alt';
      case 'personnel': return 'person';
      default: return 'flag';
    }
  }

  private initializeCharts(): void {
    // Générer des données de démonstration
    const demoData = this.chartDataService.generateDemoData();

    // Graphique d'évolution des performances
    this.performanceEvolutionData = this.chartDataService.generatePerformanceEvolutionData(demoData.performances);
    this.performanceEvolutionOptions = this.chartDataService.getPerformanceChartOptions();

    // Graphique de présence
    this.presenceData = this.chartDataService.generatePresenceData(demoData.presences);
    this.presenceOptions = this.chartDataService.getBarChartOptions('Évolution de la présence');

    // Graphique de répartition des absences
    this.absenceDistributionData = this.chartDataService.generateAbsenceDistributionData([
      { type: 'MALADIE' }, { type: 'MALADIE' }, { type: 'BLESSURE' },
      { type: 'URGENCE_FAMILIALE' }, { type: 'OBLIGATIONS_PROFESSIONNELLES' },
      { type: 'CONGES' }, { type: 'AUTRE' }
    ]);
    this.absenceDistributionOptions = this.chartDataService.getPieChartOptions();

    // Graphique de progression des objectifs
    this.objectifsProgressData = this.chartDataService.generateObjectifsProgressData([
      { titre: 'Améliorer le service', progression: 75 },
      { titre: 'Condition physique', progression: 60 },
      { titre: 'Tactiques défensives', progression: 45 },
      { titre: 'Mental et concentration', progression: 80 }
    ]);
    this.objectifsProgressOptions = this.chartDataService.getBarChartOptions('Progression des objectifs');
  }
}
