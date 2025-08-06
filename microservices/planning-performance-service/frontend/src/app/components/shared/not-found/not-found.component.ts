import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="not-found-container">
      <div class="not-found-content">
        <div class="error-icon">
          <i class="material-icons">error_outline</i>
        </div>
        
        <h1 class="error-code">404</h1>
        <h2 class="error-title">Page non trouvée</h2>
        <p class="error-message">
          Désolé, la page que vous recherchez n'existe pas ou a été déplacée.
        </p>
        
        <div class="error-actions">
          <a routerLink="/dashboard" class="btn btn-primary">
            <i class="material-icons me-1">home</i>
            Retour au tableau de bord
          </a>
          <button class="btn btn-outline-secondary" onclick="history.back()">
            <i class="material-icons me-1">arrow_back</i>
            Page précédente
          </button>
        </div>
        
        <div class="helpful-links">
          <h6>Liens utiles :</h6>
          <ul>
            <li><a routerLink="/entrainements">Gestion des entraînements</a></li>
            <li><a routerLink="/calendrier">Calendrier</a></li>
            <li><a routerLink="/performances">Performances</a></li>
            <li><a routerLink="/statistiques">Statistiques</a></li>
          </ul>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .not-found-container {
      min-height: calc(100vh - 120px);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 2rem;
      background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
    }

    .not-found-content {
      text-align: center;
      max-width: 500px;
      background: white;
      padding: 3rem 2rem;
      border-radius: 1rem;
      box-shadow: 0 10px 30px rgba(0,0,0,0.1);
    }

    .error-icon {
      margin-bottom: 1.5rem;
      
      .material-icons {
        font-size: 4rem;
        color: var(--warning-color);
      }
    }

    .error-code {
      font-size: 4rem;
      font-weight: 700;
      color: var(--primary-color);
      margin-bottom: 0.5rem;
      line-height: 1;
    }

    .error-title {
      font-size: 1.5rem;
      color: var(--secondary-color);
      margin-bottom: 1rem;
      font-weight: 500;
    }

    .error-message {
      color: #666;
      margin-bottom: 2rem;
      line-height: 1.6;
    }

    .error-actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
      margin-bottom: 2rem;
      flex-wrap: wrap;
    }

    .helpful-links {
      border-top: 1px solid #eee;
      padding-top: 1.5rem;
      
      h6 {
        color: var(--secondary-color);
        margin-bottom: 1rem;
        font-weight: 500;
      }
      
      ul {
        list-style: none;
        padding: 0;
        margin: 0;
        
        li {
          margin-bottom: 0.5rem;
          
          a {
            color: var(--primary-color);
            text-decoration: none;
            transition: var(--transition);
            
            &:hover {
              color: var(--primary-dark);
              text-decoration: underline;
            }
          }
        }
      }
    }

    @media (max-width: 768px) {
      .not-found-content {
        padding: 2rem 1.5rem;
        margin: 1rem;
      }
      
      .error-code {
        font-size: 3rem;
      }
      
      .error-title {
        font-size: 1.25rem;
      }
      
      .error-actions {
        flex-direction: column;
        align-items: center;
        
        .btn {
          width: 100%;
          max-width: 250px;
        }
      }
    }
  `]
})
export class NotFoundComponent {}
