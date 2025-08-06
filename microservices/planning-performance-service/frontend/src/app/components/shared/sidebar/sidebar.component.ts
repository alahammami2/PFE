import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface MenuItem {
  label: string;
  icon: string;
  route?: string;
  children?: MenuItem[];
  badge?: string;
  badgeColor?: string;
  expanded?: boolean;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <aside class="sidebar">
      <div class="sidebar-content">
        <nav class="sidebar-nav">
          <ul class="nav-list">
            <li *ngFor="let item of menuItems" class="nav-item">
              <ng-container *ngIf="!item.children">
                <a 
                  [routerLink]="item.route" 
                  routerLinkActive="active"
                  class="nav-link"
                  [class.has-badge]="item.badge"
                >
                  <i class="material-icons">{{ item.icon }}</i>
                  <span class="nav-text">{{ item.label }}</span>
                  <span 
                    *ngIf="item.badge" 
                    class="badge"
                    [class]="'bg-' + (item.badgeColor || 'primary')"
                  >
                    {{ item.badge }}
                  </span>
                </a>
              </ng-container>
              
              <ng-container *ngIf="item.children">
                <div class="nav-group">
                  <div class="nav-group-header" (click)="toggleGroup(item)">
                    <i class="material-icons">{{ item.icon }}</i>
                    <span class="nav-text">{{ item.label }}</span>
                    <i class="material-icons expand-icon" [class.expanded]="item.expanded">
                      expand_more
                    </i>
                  </div>
                  <ul class="nav-sublist" [class.expanded]="item.expanded">
                    <li *ngFor="let child of item.children" class="nav-subitem">
                      <a 
                        [routerLink]="child.route" 
                        routerLinkActive="active"
                        class="nav-sublink"
                      >
                        <i class="material-icons">{{ child.icon }}</i>
                        <span class="nav-text">{{ child.label }}</span>
                        <span 
                          *ngIf="child.badge" 
                          class="badge"
                          [class]="'bg-' + (child.badgeColor || 'primary')"
                        >
                          {{ child.badge }}
                        </span>
                      </a>
                    </li>
                  </ul>
                </div>
              </ng-container>
            </li>
          </ul>
        </nav>
        
        <div class="sidebar-footer">
          <div class="quick-stats">
            <div class="stat-item">
              <i class="material-icons text-success">event_available</i>
              <div class="stat-info">
                <span class="stat-number">12</span>
                <span class="stat-label">Entraînements ce mois</span>
              </div>
            </div>
            <div class="stat-item">
              <i class="material-icons text-warning">people</i>
              <div class="stat-info">
                <span class="stat-number">85%</span>
                <span class="stat-label">Taux de présence</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </aside>
  `,
  styles: [`
    .sidebar {
      width: 250px;
      height: calc(100vh - 60px);
      background: white;
      border-right: 1px solid #e0e0e0;
      position: fixed;
      left: 0;
      top: 60px;
      z-index: 1020;
      box-shadow: 2px 0 4px rgba(0,0,0,0.1);
      transition: transform 0.3s ease;
    }

    .sidebar-content {
      height: 100%;
      display: flex;
      flex-direction: column;
      padding: 1rem 0;
    }

    .sidebar-nav {
      flex: 1;
      overflow-y: auto;
    }

    .nav-list {
      list-style: none;
      padding: 0;
      margin: 0;
    }

    .nav-item {
      margin-bottom: 0.25rem;
    }

    .nav-link, .nav-sublink {
      display: flex;
      align-items: center;
      padding: 0.75rem 1rem;
      color: var(--secondary-color);
      text-decoration: none;
      transition: all 0.3s ease;
      border-radius: 0 25px 25px 0;
      margin-right: 1rem;
      position: relative;
      
      &:hover {
        background-color: rgba(25, 118, 210, 0.1);
        color: var(--primary-color);
        transform: translateX(5px);
      }
      
      &.active {
        background: linear-gradient(135deg, var(--primary-color), #1565c0);
        color: white;
        font-weight: 500;
        
        .material-icons {
          color: white;
        }
      }
      
      .material-icons {
        margin-right: 0.75rem;
        font-size: 1.3rem;
        color: var(--secondary-color);
        transition: color 0.3s ease;
      }
      
      .nav-text {
        flex: 1;
        font-size: 0.9rem;
      }
      
      .badge {
        font-size: 0.7rem;
        padding: 0.25rem 0.5rem;
        border-radius: 12px;
      }
    }

    .nav-group-header {
      display: flex;
      align-items: center;
      padding: 0.75rem 1rem;
      color: var(--secondary-color);
      cursor: pointer;
      transition: all 0.3s ease;
      border-radius: 0 25px 25px 0;
      margin-right: 1rem;
      
      &:hover {
        background-color: rgba(25, 118, 210, 0.1);
        color: var(--primary-color);
        transform: translateX(5px);
      }
      
      .material-icons {
        margin-right: 0.75rem;
        font-size: 1.3rem;
        color: var(--secondary-color);
        transition: color 0.3s ease;
      }
      
      .nav-text {
        flex: 1;
        font-size: 0.9rem;
        font-weight: 500;
      }
      
      .expand-icon {
        font-size: 1.2rem;
        transition: transform 0.3s ease;
        
        &.expanded {
          transform: rotate(180deg);
        }
      }
    }

    .nav-sublist {
      list-style: none;
      padding: 0;
      margin: 0;
      max-height: 0;
      overflow: hidden;
      transition: max-height 0.3s ease;
      
      &.expanded {
        max-height: 300px;
      }
    }

    .nav-subitem {
      margin-left: 1rem;
    }

    .nav-sublink {
      padding: 0.5rem 1rem;
      font-size: 0.85rem;
      
      .material-icons {
        font-size: 1.1rem;
        margin-right: 0.5rem;
      }
    }

    .sidebar-footer {
      border-top: 1px solid #e0e0e0;
      padding: 1rem;
    }

    .quick-stats {
      .stat-item {
        display: flex;
        align-items: center;
        padding: 0.5rem 0;
        
        .material-icons {
          font-size: 1.5rem;
          margin-right: 0.75rem;
        }
        
        .stat-info {
          display: flex;
          flex-direction: column;
          
          .stat-number {
            font-weight: bold;
            font-size: 0.9rem;
            color: var(--secondary-color);
          }
          
          .stat-label {
            font-size: 0.7rem;
            color: #999;
          }
        }
      }
    }

    @media (max-width: 768px) {
      .sidebar {
        transform: translateX(-100%);
        
        &.mobile-open {
          transform: translateX(0);
        }
      }
    }

    /* Scrollbar personnalisée */
    .sidebar-nav::-webkit-scrollbar {
      width: 4px;
    }

    .sidebar-nav::-webkit-scrollbar-track {
      background: transparent;
    }

    .sidebar-nav::-webkit-scrollbar-thumb {
      background: #ccc;
      border-radius: 2px;
      
      &:hover {
        background: #999;
      }
    }
  `]
})
export class SidebarComponent {
  menuItems: MenuItem[] = [
    {
      label: 'Tableau de bord',
      icon: 'dashboard',
      route: '/dashboard'
    },
    {
      label: 'Entraînements',
      icon: 'fitness_center',
      children: [
        {
          label: 'Liste des entraînements',
          icon: 'list',
          route: '/entrainements'
        },
        {
          label: 'Nouvel entraînement',
          icon: 'add_circle',
          route: '/entrainements/nouveau'
        },
        {
          label: 'Calendrier',
          icon: 'calendar_today',
          route: '/calendrier'
        }
      ]
    },
    {
      label: 'Participations',
      icon: 'people',
      children: [
        {
          label: 'Gestion des présences',
          icon: 'how_to_reg',
          route: '/participations'
        },
        {
          label: 'Marquer les présences',
          icon: 'fact_check',
          route: '/participations/presence'
        }
      ]
    },
    {
      label: 'Performances',
      icon: 'trending_up',
      children: [
        {
          label: 'Évaluations',
          icon: 'assessment',
          route: '/performances'
        },
        {
          label: 'Analyses',
          icon: 'analytics',
          route: '/performances/analytics'
        }
      ]
    },
    {
      label: 'Absences',
      icon: 'event_busy',
      children: [
        {
          label: 'Liste des absences',
          icon: 'list_alt',
          route: '/absences'
        },
        {
          label: 'Déclarer une absence',
          icon: 'report_problem',
          route: '/absences/declarer'
        },
        {
          label: 'Analyses',
          icon: 'insights',
          route: '/absences/analytics'
        }
      ]
    },
    {
      label: 'Objectifs',
      icon: 'flag',
      children: [
        {
          label: 'Objectifs individuels',
          icon: 'track_changes',
          route: '/objectifs'
        },
        {
          label: 'Nouvel objectif',
          icon: 'add_task',
          route: '/objectifs/nouveau'
        }
      ]
    },
    {
      label: 'Statistiques',
      icon: 'bar_chart',
      children: [
        {
          label: 'Tableau de bord',
          icon: 'dashboard',
          route: '/statistiques'
        },
        {
          label: 'Statistiques équipe',
          icon: 'groups',
          route: '/statistiques/equipe'
        }
      ]
    }
  ];

  toggleGroup(item: MenuItem): void {
    if (item.children) {
      item.expanded = !item.expanded;
    }
  }
}
