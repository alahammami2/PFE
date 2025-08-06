import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
      <div class="container-fluid">
        <a class="navbar-brand d-flex align-items-center" routerLink="/dashboard">
          <i class="material-icons me-2">sports_volleyball</i>
          <span class="fw-bold">SprintBot</span>
          <span class="ms-2 badge bg-light text-primary">Planning & Performance</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <a class="nav-link" routerLink="/dashboard" routerLinkActive="active">
                <i class="material-icons me-1">dashboard</i>
                Tableau de bord
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" routerLink="/entrainements" routerLinkActive="active">
                <i class="material-icons me-1">fitness_center</i>
                Entraînements
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" routerLink="/calendrier" routerLinkActive="active">
                <i class="material-icons me-1">calendar_today</i>
                Calendrier
              </a>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                <i class="material-icons me-1">analytics</i>
                Analyses
              </a>
              <ul class="dropdown-menu">
                <li><a class="dropdown-item" routerLink="/performances/analytics">
                  <i class="material-icons me-2">trending_up</i>Performances
                </a></li>
                <li><a class="dropdown-item" routerLink="/absences/analytics">
                  <i class="material-icons me-2">event_busy</i>Absences
                </a></li>
                <li><a class="dropdown-item" routerLink="/statistiques">
                  <i class="material-icons me-2">bar_chart</i>Statistiques
                </a></li>
              </ul>
            </li>
          </ul>

          <ul class="navbar-nav">
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" role="button" data-bs-toggle="dropdown">
                <div class="user-avatar me-2">
                  <i class="material-icons">account_circle</i>
                </div>
                <span>Coach Admin</span>
              </a>
              <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="#">
                  <i class="material-icons me-2">person</i>Profil
                </a></li>
                <li><a class="dropdown-item" href="#">
                  <i class="material-icons me-2">settings</i>Paramètres
                </a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item text-danger" href="#">
                  <i class="material-icons me-2">logout</i>Déconnexion
                </a></li>
              </ul>
            </li>
            
            <li class="nav-item">
              <button class="btn btn-outline-light btn-sm ms-2" type="button">
                <i class="material-icons me-1">notifications</i>
                <span class="notification-badge">3</span>
              </button>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      z-index: 1030;
    }

    .navbar-brand {
      font-size: 1.5rem;
      
      .material-icons {
        font-size: 1.8rem;
      }
    }

    .nav-link {
      display: flex;
      align-items: center;
      padding: 0.5rem 1rem;
      border-radius: 0.375rem;
      margin: 0 0.25rem;
      transition: all 0.3s ease;
      
      &:hover {
        background-color: rgba(255,255,255,0.1);
        transform: translateY(-1px);
      }
      
      &.active {
        background-color: rgba(255,255,255,0.2);
        font-weight: 500;
      }
      
      .material-icons {
        font-size: 1.2rem;
      }
    }

    .dropdown-menu {
      border: none;
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      border-radius: 0.5rem;
      
      .dropdown-item {
        display: flex;
        align-items: center;
        padding: 0.5rem 1rem;
        transition: all 0.2s ease;
        
        &:hover {
          background-color: var(--light-color);
          transform: translateX(5px);
        }
        
        .material-icons {
          font-size: 1.1rem;
        }
      }
    }

    .user-avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: rgba(255,255,255,0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      
      .material-icons {
        font-size: 1.5rem;
        color: white;
      }
    }

    .notification-badge {
      position: absolute;
      top: -5px;
      right: -5px;
      background-color: var(--danger-color);
      color: white;
      border-radius: 50%;
      width: 18px;
      height: 18px;
      font-size: 0.7rem;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
    }

    .btn-outline-light {
      position: relative;
      border-color: rgba(255,255,255,0.3);
      
      &:hover {
        background-color: rgba(255,255,255,0.1);
        border-color: rgba(255,255,255,0.5);
      }
    }

    @media (max-width: 768px) {
      .navbar-nav {
        margin-top: 1rem;
        
        .nav-link {
          margin: 0.25rem 0;
        }
      }
      
      .user-avatar {
        width: 28px;
        height: 28px;
        
        .material-icons {
          font-size: 1.3rem;
        }
      }
    }
  `]
})
export class NavbarComponent {}
