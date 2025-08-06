import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, finalize } from 'rxjs/operators';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';

// Services
import { FinanceApiService } from '../../core/services/finance-api.service';

// Models
import { RapportFinancier } from '../../core/models/rapport-financier.model';
import { Budget } from '../../core/models/budget.model';
import { Transaction } from '../../core/models/transaction.model';
import { Sponsor } from '../../core/models/sponsor.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  
  // État du composant
  loading = true;
  error: string | null = null;
  
  // Données du dashboard
  rapportDashboard: any = null;
  budgetsEnAlerte: Budget[] = [];
  transactionsEnAttente: Transaction[] = [];
  sponsorsExpirants: Sponsor[] = [];
  
  // Indicateurs clés
  indicateurs = {
    totalRecettes: 0,
    totalDepenses: 0,
    solde: 0,
    budgetUtilise: 0,
    nombreTransactionsEnAttente: 0,
    nombreBudgetsEnAlerte: 0
  };
  
  // Configuration des graphiques
  chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom'
      }
    }
  };
  
  // Graphique d'évolution de trésorerie
  tresorerieChartData: ChartData<'line'> = {
    labels: [],
    datasets: []
  };
  tresorerieChartType: ChartType = 'line';
  
  // Graphique de répartition des recettes
  recettesChartData: ChartData<'doughnut'> = {
    labels: [],
    datasets: []
  };
  recettesChartType: ChartType = 'doughnut';
  
  // Graphique de répartition des dépenses
  depensesChartData: ChartData<'doughnut'> = {
    labels: [],
    datasets: []
  };
  depensesChartType: ChartType = 'doughnut';
  
  // Graphique des budgets
  budgetsChartData: ChartData<'bar'> = {
    labels: [],
    datasets: []
  };
  budgetsChartType: ChartType = 'bar';
  
  private destroy$ = new Subject<void>();

  constructor(
    private financeApiService: FinanceApiService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Charge les données du dashboard
   */
  loadDashboardData(): void {
    this.loading = true;
    this.error = null;
    
    this.financeApiService.genererRapportDashboard()
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: (rapport) => {
          this.rapportDashboard = rapport;
          this.processRapportData(rapport);
          this.loadAdditionalData();
        },
        error: (error) => {
          console.error('Erreur lors du chargement du dashboard:', error);
          this.error = 'Erreur lors du chargement des données du dashboard';
        }
      });
  }

  /**
   * Traite les données du rapport
   */
  private processRapportData(rapport: any): void {
    // Mise à jour des indicateurs
    this.indicateurs = {
      totalRecettes: rapport.transactions?.totalRecettes || 0,
      totalDepenses: rapport.transactions?.totalDepenses || 0,
      solde: rapport.transactions?.solde || 0,
      budgetUtilise: rapport.budgets?.tauxUtilisation || 0,
      nombreTransactionsEnAttente: rapport.transactions?.transactionsEnAttente || 0,
      nombreBudgetsEnAlerte: rapport.budgets?.budgetsEnAlerte?.length || 0
    };
    
    // Configuration des graphiques
    this.setupTresorerieChart(rapport.tresorerie);
    this.setupRecettesChart(rapport.transactions);
    this.setupDepensesChart(rapport.transactions);
    this.setupBudgetsChart(rapport.budgets);
  }

  /**
   * Configure le graphique de trésorerie
   */
  private setupTresorerieChart(tresorerie: any): void {
    if (!tresorerie?.fluxParMois) return;
    
    this.tresorerieChartData = {
      labels: tresorerie.fluxParMois.map((flux: any) => flux.mois),
      datasets: [
        {
          label: 'Entrées',
          data: tresorerie.fluxParMois.map((flux: any) => flux.entrees),
          borderColor: '#4CAF50',
          backgroundColor: 'rgba(76, 175, 80, 0.1)',
          tension: 0.4
        },
        {
          label: 'Sorties',
          data: tresorerie.fluxParMois.map((flux: any) => flux.sorties),
          borderColor: '#F44336',
          backgroundColor: 'rgba(244, 67, 54, 0.1)',
          tension: 0.4
        },
        {
          label: 'Solde',
          data: tresorerie.fluxParMois.map((flux: any) => flux.solde),
          borderColor: '#2196F3',
          backgroundColor: 'rgba(33, 150, 243, 0.1)',
          tension: 0.4
        }
      ]
    };
  }

  /**
   * Configure le graphique des recettes
   */
  private setupRecettesChart(transactions: any): void {
    if (!transactions?.repartitionParCategorie) return;
    
    const recettes = transactions.repartitionParCategorie.filter((cat: any) => cat.type === 'RECETTE');
    
    this.recettesChartData = {
      labels: recettes.map((cat: any) => cat.categorie),
      datasets: [{
        data: recettes.map((cat: any) => cat.montant),
        backgroundColor: [
          '#4CAF50', '#2196F3', '#FF9800', '#9C27B0', 
          '#00BCD4', '#8BC34A', '#FFC107', '#E91E63'
        ]
      }]
    };
  }

  /**
   * Configure le graphique des dépenses
   */
  private setupDepensesChart(transactions: any): void {
    if (!transactions?.repartitionParCategorie) return;
    
    const depenses = transactions.repartitionParCategorie.filter((cat: any) => cat.type === 'DEPENSE');
    
    this.depensesChartData = {
      labels: depenses.map((cat: any) => cat.categorie),
      datasets: [{
        data: depenses.map((cat: any) => cat.montant),
        backgroundColor: [
          '#F44336', '#FF5722', '#E91E63', '#9C27B0',
          '#673AB7', '#3F51B5', '#2196F3', '#00BCD4'
        ]
      }]
    };
  }

  /**
   * Configure le graphique des budgets
   */
  private setupBudgetsChart(budgets: any): void {
    if (!budgets?.repartitionParCategorie) return;
    
    this.budgetsChartData = {
      labels: budgets.repartitionParCategorie.map((cat: any) => cat.categorie),
      datasets: [
        {
          label: 'Montant Total',
          data: budgets.repartitionParCategorie.map((cat: any) => cat.montantTotal),
          backgroundColor: 'rgba(33, 150, 243, 0.6)',
          borderColor: '#2196F3',
          borderWidth: 1
        },
        {
          label: 'Montant Utilisé',
          data: budgets.repartitionParCategorie.map((cat: any) => cat.montantUtilise),
          backgroundColor: 'rgba(244, 67, 54, 0.6)',
          borderColor: '#F44336',
          borderWidth: 1
        }
      ]
    };
  }

  /**
   * Charge les données additionnelles
   */
  private loadAdditionalData(): void {
    // Chargement des budgets en alerte
    this.financeApiService.getBudgetsActifs()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (budgets) => {
          this.budgetsEnAlerte = budgets.filter(budget => {
            const pourcentageUtilise = (budget.montantUtilise / budget.montantTotal) * 100;
            return pourcentageUtilise >= budget.seuilAlerte;
          });
        },
        error: (error) => console.error('Erreur lors du chargement des budgets:', error)
      });
    
    // Chargement des transactions en attente
    this.financeApiService.getTransactionsEnAttente()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (transactions) => {
          this.transactionsEnAttente = transactions;
        },
        error: (error) => console.error('Erreur lors du chargement des transactions:', error)
      });
  }

  /**
   * Actualise les données
   */
  refresh(): void {
    this.loadDashboardData();
  }

  /**
   * Obtient la couleur d'un indicateur selon sa valeur
   */
  getIndicatorColor(value: number, type: 'positive' | 'negative' | 'neutral' = 'neutral'): string {
    if (type === 'positive') {
      return value > 0 ? 'success' : value < 0 ? 'warn' : 'neutral';
    } else if (type === 'negative') {
      return value < 0 ? 'success' : value > 0 ? 'warn' : 'neutral';
    }
    return 'neutral';
  }

  /**
   * Formate un montant en devise
   */
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }

  /**
   * Formate un pourcentage
   */
  formatPercentage(value: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'percent',
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    }).format(value / 100);
  }
}
