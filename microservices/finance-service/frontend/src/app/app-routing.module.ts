import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Guards
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';

// Components
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { BudgetListComponent } from './components/budget/budget-list/budget-list.component';
import { BudgetFormComponent } from './components/budget/budget-form/budget-form.component';
import { BudgetDetailComponent } from './components/budget/budget-detail/budget-detail.component';
import { TransactionListComponent } from './components/transaction/transaction-list/transaction-list.component';
import { TransactionFormComponent } from './components/transaction/transaction-form/transaction-form.component';
import { TransactionDetailComponent } from './components/transaction/transaction-detail/transaction-detail.component';
import { SponsorListComponent } from './components/sponsor/sponsor-list/sponsor-list.component';
import { SponsorFormComponent } from './components/sponsor/sponsor-form/sponsor-form.component';
import { SponsorDetailComponent } from './components/sponsor/sponsor-detail/sponsor-detail.component';
import { SalaireListComponent } from './components/salaire/salaire-list/salaire-list.component';
import { SalaireFormComponent } from './components/salaire/salaire-form/salaire-form.component';
import { SalaireDetailComponent } from './components/salaire/salaire-detail/salaire-detail.component';
import { RapportComponent } from './components/rapport/rapport.component';

const routes: Routes = [
  // Redirection par défaut
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  
  // Dashboard - Accessible à tous les utilisateurs authentifiés
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    data: { 
      title: 'Tableau de bord financier',
      breadcrumb: 'Dashboard'
    }
  },
  
  // Gestion des budgets
  {
    path: 'budgets',
    canActivate: [AuthGuard, RoleGuard],
    data: { 
      roles: ['ADMIN', 'FINANCE_MANAGER', 'FINANCE_USER'],
      breadcrumb: 'Budgets'
    },
    children: [
      {
        path: '',
        component: BudgetListComponent,
        data: { title: 'Liste des budgets' }
      },
      {
        path: 'nouveau',
        component: BudgetFormComponent,
        canActivate: [RoleGuard],
        data: { 
          title: 'Nouveau budget',
          roles: ['ADMIN', 'FINANCE_MANAGER'],
          breadcrumb: 'Nouveau'
        }
      },
      {
        path: ':id',
        component: BudgetDetailComponent,
        data: { 
          title: 'Détail du budget',
          breadcrumb: 'Détail'
        }
      },
      {
        path: ':id/modifier',
        component: BudgetFormComponent,
        canActivate: [RoleGuard],
        data: { 
          title: 'Modifier le budget',
          roles: ['ADMIN', 'FINANCE_MANAGER'],
          breadcrumb: 'Modifier'
        }
      }
    ]
  },
  
  // Gestion des transactions
  {
    path: 'transactions',
    canActivate: [AuthGuard, RoleGuard],
    data: { 
      roles: ['ADMIN', 'FINANCE_MANAGER', 'FINANCE_USER'],
      breadcrumb: 'Transactions'
    },
    children: [
      {
        path: '',
        component: TransactionListComponent,
        data: { title: 'Liste des transactions' }
      },
      {
        path: 'nouvelle',
        component: TransactionFormComponent,
        data: { 
          title: 'Nouvelle transaction',
          breadcrumb: 'Nouvelle'
        }
      },
      {
        path: ':id',
        component: TransactionDetailComponent,
        data: { 
          title: 'Détail de la transaction',
          breadcrumb: 'Détail'
        }
      },
      {
        path: ':id/modifier',
        component: TransactionFormComponent,
        canActivate: [RoleGuard],
        data: { 
          title: 'Modifier la transaction',
          roles: ['ADMIN', 'FINANCE_MANAGER'],
          breadcrumb: 'Modifier'
        }
      }
    ]
  },
  
  // Gestion des sponsors
  {
    path: 'sponsors',
    canActivate: [AuthGuard, RoleGuard],
    data: { 
      roles: ['ADMIN', 'FINANCE_MANAGER', 'FINANCE_USER'],
      breadcrumb: 'Sponsors'
    },
    children: [
      {
        path: '',
        component: SponsorListComponent,
        data: { title: 'Liste des sponsors' }
      },
      {
        path: 'nouveau',
        component: SponsorFormComponent,
        canActivate: [RoleGuard],
        data: { 
          title: 'Nouveau sponsor',
          roles: ['ADMIN', 'FINANCE_MANAGER'],
          breadcrumb: 'Nouveau'
        }
      },
      {
        path: ':id',
        component: SponsorDetailComponent,
        data: { 
          title: 'Détail du sponsor',
          breadcrumb: 'Détail'
        }
      },
      {
        path: ':id/modifier',
        component: SponsorFormComponent,
        canActivate: [RoleGuard],
        data: { 
          title: 'Modifier le sponsor',
          roles: ['ADMIN', 'FINANCE_MANAGER'],
          breadcrumb: 'Modifier'
        }
      }
    ]
  },
  
  // Gestion des salaires
  {
    path: 'salaires',
    canActivate: [AuthGuard, RoleGuard],
    data: { 
      roles: ['ADMIN', 'FINANCE_MANAGER', 'HR_MANAGER'],
      breadcrumb: 'Salaires'
    },
    children: [
      {
        path: '',
        component: SalaireListComponent,
        data: { title: 'Liste des salaires' }
      },
      {
        path: 'nouveau',
        component: SalaireFormComponent,
        data: { 
          title: 'Nouveau salaire',
          breadcrumb: 'Nouveau'
        }
      },
      {
        path: ':id',
        component: SalaireDetailComponent,
        data: { 
          title: 'Détail du salaire',
          breadcrumb: 'Détail'
        }
      },
      {
        path: ':id/modifier',
        component: SalaireFormComponent,
        data: { 
          title: 'Modifier le salaire',
          breadcrumb: 'Modifier'
        }
      }
    ]
  },
  
  // Rapports financiers
  {
    path: 'rapports',
    component: RapportComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { 
      title: 'Rapports financiers',
      roles: ['ADMIN', 'FINANCE_MANAGER'],
      breadcrumb: 'Rapports'
    }
  },
  
  // Route de fallback pour les pages non trouvées
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    enableTracing: false, // Mettre à true pour le debug
    scrollPositionRestoration: 'top',
    anchorScrolling: 'enabled',
    scrollOffset: [0, 64] // Offset pour la toolbar
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
