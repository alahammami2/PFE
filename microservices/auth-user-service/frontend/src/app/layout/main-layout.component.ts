import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminDashboardComponent } from '../admin-dashboard/admin-dashboard.component';
import { PlanningPerformanceService, Entrainement } from '../services/planning-performance.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, AdminDashboardComponent],
  template: `
    <div class="main-layout">
      <!-- Header -->
      <header class="header">
        <div class="header-left">
          <img src="assets/logo.png" alt="COK Logo" class="header-logo">
          <span class="header-title">Club Olympique de Kelibia</span>
        </div>
        <div class="header-right">
          <div class="user-info">
            <span class="user-icon">👤</span>
            <span class="user-name">{{currentUser?.prenom}} {{currentUser?.nom}}</span>
            <button class="btn-logout" (click)="logout()">Se déconnecter</button>
          </div>
        </div>
      </header>

      <!-- Main Container -->
      <div class="main-container">
        <!-- Sidebar -->
        <aside class="sidebar">
          <nav class="nav-menu">
            <div class="nav-item" [class.active]="activeModule === 'dashboard'" (click)="setActiveModule('dashboard')" title="Accueil">
              <span class="nav-icon">🏠</span>
              <span class="nav-text">Accueil</span>
            </div>

            <div class="nav-item" [class.active]="activeModule === 'planning'" (click)="setActiveModule('planning')" title="Planning">
              <span class="nav-icon">📋</span>
              <span class="nav-text">Planning</span>
            </div>

            <div class="nav-item" [class.active]="activeModule === 'finances'" (click)="setActiveModule('finances')" title="Finances">
              <span class="nav-icon">💰</span>
              <span class="nav-text">Finances</span>
            </div>
            <div class="nav-item" [class.active]="activeModule === 'calendrier'" (click)="setActiveModule('calendrier')" title="Calendrier">
              <span class="nav-icon">📆</span>
              <span class="nav-text">Calendrier</span>
            </div>
            <div class="nav-item" [class.active]="activeModule === 'effectif'" (click)="setActiveModule('effectif')" title="Effectif">
              <span class="nav-icon">👥</span>
              <span class="nav-text">Effectif</span>
            </div>
            <div class="nav-item" [class.active]="activeModule === 'messagerie'" (click)="setActiveModule('messagerie')" title="Messagerie">
              <span class="nav-icon">💬</span>
              <span class="nav-text">Messagerie</span>
            </div>
            <div class="nav-item" [class.active]="activeModule === 'parametres'" (click)="setActiveModule('parametres')" title="Paramètres">
              <span class="nav-icon">⚙️</span>
              <span class="nav-text">Paramètres</span>
            </div>
            <div class="nav-item" [class.active]="activeModule === 'profile'" (click)="setActiveModule('profile')" title="Profile">
              <span class="nav-icon">👤</span>
              <span class="nav-text">Profile</span>
            </div>
            <div class="nav-item" [class.active]="activeModule === 'championnats'" (click)="setActiveModule('championnats')" title="Championnats">
              <span class="nav-icon">🏆</span>
              <span class="nav-text">Championnats</span>
            </div>
            <div class="nav-item" [class.active]="activeModule === 'demandes'" (click)="setActiveModule('demandes')" title="Demandes administratives">
              <span class="nav-icon">📋</span>
              <span class="nav-text">Demandes</span>
            </div>
            <div class="nav-item" [class.active]="activeModule === 'administration'" (click)="setActiveModule('administration')" title="Administration">
              <span class="nav-icon">🔧</span>
              <span class="nav-text">Administration</span>
            </div>
          </nav>
        </aside>

        <!-- Content Area -->
        <main class="content">
          <!-- Dashboard Module -->
          <div *ngIf="activeModule === 'dashboard'" class="module-content">
            <div class="welcome-section">
              <div class="welcome-header">
                <img src="assets/logo.png" alt="COK Logo" class="welcome-logo">
                <div class="welcome-text">
                  <h1>Bonjour, {{currentUser?.prenom}} {{currentUser?.nom}}</h1>
                  <p class="welcome-subtitle">Bienvenue sur votre espace Club Olympique de Kelibia</p>
                </div>
              </div>
            </div>

            <div class="dashboard-section">
              <h2 class="section-title">Tableau de bord</h2>
              <p class="section-subtitle">Consultez l'actualité de votre équipe.</p>

              <div class="dashboard-grid">
                <div class="dashboard-card">
                  <h3>Dernier événement</h3>
                  <div class="event-placeholder">
                    <span class="calendar-icon">📅</span>
                    <p>Aucun événement passé</p>
                    <a href="#" class="link-calendar" (click)="setActiveModule('calendrier')">Voir le calendrier</a>
                  </div>
                </div>

                <div class="dashboard-card">
                  <h3>Prochain événement</h3>
                  <div class="event-placeholder">
                    <span class="calendar-icon">📅</span>
                    <p>Aucun événement à venir</p>
                    <a href="#" class="link-calendar" (click)="setActiveModule('calendrier')">Voir le calendrier</a>
                  </div>
                </div>
              </div>

              <div class="classement-section">
                <h3>Classement</h3>
                <div class="classement-placeholder">
                  <div class="classement-icon">📊</div>
                  <p>Données de classement non disponibles</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Planning Module -->
          <div *ngIf="activeModule === 'planning'" class="module-content">
            <h2>📋 Planning & Performance</h2>

            <div class="planning-tabs">
              <button class="tab-btn" [class.active]="planningTab === 'entrainements'" (click)="planningTab = 'entrainements'">
                Entraînements
              </button>
              <button class="tab-btn" [class.active]="planningTab === 'performances'" (click)="planningTab = 'performances'">
                Performances
              </button>
              <button class="tab-btn" [class.active]="planningTab === 'objectifs'" (click)="planningTab = 'objectifs'">
                Objectifs
              </button>
              <button class="tab-btn" [class.active]="planningTab === 'statistiques'" (click)="planningTab = 'statistiques'">
                Statistiques
              </button>
            </div>

            <!-- Entraînements Tab -->
            <div *ngIf="planningTab === 'entrainements'" class="tab-content">
              <div class="section-header">
                <h3>Entraînements</h3>
                <button class="btn-primary" (click)="loadEntrainements()">Actualiser</button>
              </div>

              <div *ngIf="entrainements.length === 0" class="placeholder-content">
                <p>Aucun entraînement trouvé. Connectez-vous au microservice Planning Performance.</p>
                <button class="btn-secondary" (click)="loadEntrainements()">Charger les entraînements</button>
              </div>

              <div *ngIf="entrainements.length > 0" class="entrainements-list">
                <div *ngFor="let entrainement of entrainements" class="entrainement-card">
                  <div class="entrainement-header">
                    <h4>{{entrainement.titre}}</h4>
                    <span class="entrainement-type">{{entrainement.type}}</span>
                  </div>
                  <div class="entrainement-details">
                    <p><strong>Date:</strong> {{entrainement.date}}</p>
                    <p><strong>Heure:</strong> {{entrainement.heureDebut}} - {{entrainement.heureFin}}</p>
                    <p><strong>Lieu:</strong> {{entrainement.lieu}}</p>
                    <p><strong>Statut:</strong> {{entrainement.statut}}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Autres tabs -->
            <div *ngIf="planningTab === 'performances'" class="tab-content">
              <h3>Performances</h3>
              <div class="placeholder-content">
                <p>Module Performances - Intégration avec le microservice Planning Performance</p>
              </div>
            </div>

            <div *ngIf="planningTab === 'objectifs'" class="tab-content">
              <h3>Objectifs</h3>
              <div class="placeholder-content">
                <p>Module Objectifs - Intégration avec le microservice Planning Performance</p>
              </div>
            </div>

            <div *ngIf="planningTab === 'statistiques'" class="tab-content">
              <h3>Statistiques</h3>
              <div class="placeholder-content">
                <p>Module Statistiques - Intégration avec le microservice Planning Performance</p>
              </div>
            </div>
          </div>

          <!-- Finances Module -->
          <div *ngIf="activeModule === 'finances'" class="module-content">
            <h2>💰 Finances</h2>
            <div class="placeholder-content">
              <p>Module Finances en cours de développement...</p>
            </div>
          </div>

          <!-- Calendrier Module -->
          <div *ngIf="activeModule === 'calendrier'" class="module-content">
            <h2>� Calendrier</h2>
            <div class="placeholder-content">
              <p>Module Calendrier en cours de développement...</p>
            </div>
          </div>

          <!-- Effectif Module -->
          <div *ngIf="activeModule === 'effectif'" class="module-content">
            <h2>👥 Effectif</h2>
            <div class="placeholder-content">
              <p>Module Effectif en cours de développement...</p>
            </div>
          </div>

          <!-- Messagerie Module -->
          <div *ngIf="activeModule === 'messagerie'" class="module-content">
            <h2>� Messagerie</h2>
            <div class="placeholder-content">
              <p>Module Messagerie en cours de développement...</p>
            </div>
          </div>

          <!-- Paramètres Module -->
          <div *ngIf="activeModule === 'parametres'" class="module-content">
            <h2>⚙️ Paramètres</h2>
            <div class="placeholder-content">
              <p>Module Paramètres en cours de développement...</p>
            </div>
          </div>

          <!-- Profile Module -->
          <div *ngIf="activeModule === 'profile'" class="module-content">
            <h2>� Profile</h2>
            <div class="placeholder-content">
              <p>Module Profile en cours de développement...</p>
            </div>
          </div>

          <!-- Championnats Module -->
          <div *ngIf="activeModule === 'championnats'" class="module-content">
            <h2>� Championnats</h2>
            <div class="placeholder-content">
              <p>Module Championnats en cours de développement...</p>
            </div>
          </div>

          <!-- Demandes administratives Module -->
          <div *ngIf="activeModule === 'demandes'" class="module-content">
            <h2>� Demandes administratives</h2>
            <div class="placeholder-content">
              <p>Module Demandes administratives en cours de développement...</p>
            </div>
          </div>

          <!-- Administration Module -->
          <div *ngIf="activeModule === 'administration'" class="module-content">
            <app-admin-dashboard></app-admin-dashboard>
          </div>
        </main>
      </div>
    </div>
  `,
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent implements OnInit {
  @Output() logoutEvent = new EventEmitter<void>();

  activeModule = 'dashboard';
  currentUser: any = null;

  // Planning module properties
  planningTab = 'entrainements';
  entrainements: Entrainement[] = [];
  isLoadingEntrainements = false;

  constructor(private planningService: PlanningPerformanceService) {}

  ngOnInit() {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    const userInfo = localStorage.getItem('sprintbot_user');
    if (userInfo) {
      this.currentUser = JSON.parse(userInfo);
    }
  }

  setActiveModule(module: string) {
    this.activeModule = module;
  }

  logout() {
    if (confirm('Êtes-vous sûr de vouloir vous déconnecter ?')) {
      this.logoutEvent.emit();
    }
  }

  // Planning methods
  loadEntrainements() {
    this.isLoadingEntrainements = true;
    this.planningService.getEntrainements().subscribe({
      next: (response) => {
        this.entrainements = response.content || response || [];
        this.isLoadingEntrainements = false;
        console.log('Entraînements chargés:', this.entrainements);
      },
      error: (error) => {
        console.error('Erreur lors du chargement des entraînements:', error);
        this.isLoadingEntrainements = false;
        // Afficher un message d'erreur à l'utilisateur
        alert('Erreur de connexion au microservice Planning Performance. Vérifiez que le service est démarré.');
      }
    });
  }
}
