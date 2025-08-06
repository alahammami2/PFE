import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent),
    title: 'Tableau de bord - Planning & Performance'
  },
  {
    path: 'entrainements',
    children: [
      {
        path: '',
        loadComponent: () => import('./components/entrainements/entrainement-list/entrainement-list.component').then(m => m.EntrainementListComponent),
        title: 'Liste des entraînements'
      },
      {
        path: 'nouveau',
        loadComponent: () => import('./components/entrainements/entrainement-form/entrainement-form.component').then(m => m.EntrainementFormComponent),
        title: 'Nouvel entraînement'
      },
      {
        path: ':id',
        loadComponent: () => import('./components/entrainements/entrainement-detail/entrainement-detail.component').then(m => m.EntrainementDetailComponent),
        title: 'Détail de l\'entraînement'
      },
      {
        path: ':id/modifier',
        loadComponent: () => import('./components/entrainements/entrainement-form/entrainement-form.component').then(m => m.EntrainementFormComponent),
        title: 'Modifier l\'entraînement'
      }
    ]
  },
  {
    path: 'calendrier',
    loadComponent: () => import('./components/calendrier/calendrier.component').then(m => m.CalendrierComponent),
    title: 'Calendrier des entraînements'
  },
  {
    path: 'participations',
    children: [
      {
        path: '',
        loadComponent: () => import('./components/participations/participation-list/participation-list.component').then(m => m.ParticipationListComponent),
        title: 'Gestion des participations'
      },
      {
        path: 'presence/:entrainementId',
        loadComponent: () => import('./components/participations/presence-form/presence-form.component').then(m => m.PresenceFormComponent),
        title: 'Marquer les présences'
      }
    ]
  },
  {
    path: 'performances',
    children: [
      {
        path: '',
        loadComponent: () => import('./components/performances/performance-list/performance-list.component').then(m => m.PerformanceListComponent),
        title: 'Évaluations de performance'
      },
      {
        path: 'evaluer/:entrainementId',
        loadComponent: () => import('./components/performances/performance-form/performance-form.component').then(m => m.PerformanceFormComponent),
        title: 'Évaluer les performances'
      },
      {
        path: 'analytics',
        loadComponent: () => import('./components/performances/performance-analytics/performance-analytics.component').then(m => m.PerformanceAnalyticsComponent),
        title: 'Analyses de performance'
      }
    ]
  },
  {
    path: 'absences',
    children: [
      {
        path: '',
        loadComponent: () => import('./components/absences/absence-list/absence-list.component').then(m => m.AbsenceListComponent),
        title: 'Gestion des absences'
      },
      {
        path: 'declarer',
        loadComponent: () => import('./components/absences/absence-form/absence-form.component').then(m => m.AbsenceFormComponent),
        title: 'Déclarer une absence'
      },
      {
        path: 'analytics',
        loadComponent: () => import('./components/absences/absence-analytics/absence-analytics.component').then(m => m.AbsenceAnalyticsComponent),
        title: 'Analyses des absences'
      }
    ]
  },
  {
    path: 'objectifs',
    children: [
      {
        path: '',
        loadComponent: () => import('./components/objectifs/objectif-list/objectif-list.component').then(m => m.ObjectifListComponent),
        title: 'Objectifs individuels'
      },
      {
        path: 'nouveau',
        loadComponent: () => import('./components/objectifs/objectif-form/objectif-form.component').then(m => m.ObjectifFormComponent),
        title: 'Nouvel objectif'
      },
      {
        path: ':id',
        loadComponent: () => import('./components/objectifs/objectif-detail/objectif-detail.component').then(m => m.ObjectifDetailComponent),
        title: 'Détail de l\'objectif'
      }
    ]
  },
  {
    path: 'statistiques',
    children: [
      {
        path: '',
        loadComponent: () => import('./components/statistiques/statistique-dashboard/statistique-dashboard.component').then(m => m.StatistiqueDashboardComponent),
        title: 'Tableau de bord statistiques'
      },
      {
        path: 'joueur/:joueurId',
        loadComponent: () => import('./components/statistiques/statistique-joueur/statistique-joueur.component').then(m => m.StatistiqueJoueurComponent),
        title: 'Statistiques du joueur'
      },
      {
        path: 'equipe',
        loadComponent: () => import('./components/statistiques/statistique-equipe/statistique-equipe.component').then(m => m.StatistiqueEquipeComponent),
        title: 'Statistiques de l\'équipe'
      }
    ]
  },
  {
    path: '**',
    loadComponent: () => import('./components/shared/not-found/not-found.component').then(m => m.NotFoundComponent),
    title: 'Page non trouvée'
  }
];
