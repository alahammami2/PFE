import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface User {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
  actif: boolean;
  dateCreation?: string;
  derniereConnexion?: string;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="admin-dashboard">
      <!-- Header -->
      <header class="dashboard-header">
        <div class="header-content">
          <div class="logo-section">
            <img src="assets/logo.png" alt="COK Logo" class="header-logo">
            <h1>Club Olympique de Kelibia - Administration</h1>
          </div>
          <div class="user-section">
            <span class="welcome-text">Bienvenue, {{currentUser?.prenom}} {{currentUser?.nom}}</span>
            <button class="btn-logout" (click)="logout()">Déconnexion</button>
          </div>
        </div>
      </header>

      <!-- Main Content -->
      <div class="dashboard-content">
        <!-- Sidebar -->
        <aside class="sidebar">
          <nav class="nav-menu">
            <ul>
              <li class="nav-item" [class.active]="activeTab === 'dashboard'" (click)="setActiveTab('dashboard')">
                <span class="nav-icon">📊</span>
                <span class="nav-text">Tableau de bord</span>
              </li>
              <li class="nav-item" [class.active]="activeTab === 'users'" (click)="setActiveTab('users')">
                <span class="nav-icon">👥</span>
                <span class="nav-text">Gestion des utilisateurs</span>
              </li>
              <li class="nav-item" [class.active]="activeTab === 'add-user'" (click)="setActiveTab('add-user')">
                <span class="nav-icon">➕</span>
                <span class="nav-text">Ajouter un utilisateur</span>
              </li>
              <li class="nav-item" [class.active]="activeTab === 'settings'" (click)="setActiveTab('settings')">
                <span class="nav-icon">⚙️</span>
                <span class="nav-text">Paramètres</span>
              </li>
            </ul>
          </nav>
        </aside>

        <!-- Main Panel -->
        <main class="main-panel">
          <!-- Dashboard Overview -->
          <div *ngIf="activeTab === 'dashboard'" class="tab-content">
            <h2>Tableau de bord</h2>
            <div class="stats-grid">
              <div class="stat-card">
                <div class="stat-icon">👥</div>
                <div class="stat-info">
                  <h3>{{totalUsers}}</h3>
                  <p>Utilisateurs totaux</p>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">🏐</div>
                <div class="stat-info">
                  <h3>{{totalPlayers}}</h3>
                  <p>Joueurs</p>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">👨‍🏫</div>
                <div class="stat-info">
                  <h3>{{totalCoaches}}</h3>
                  <p>Entraîneurs</p>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">⚕️</div>
                <div class="stat-info">
                  <h3>{{totalMedical}}</h3>
                  <p>Staff médical</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Users Management -->
          <div *ngIf="activeTab === 'users'" class="tab-content">
            <h2>Gestion des utilisateurs</h2>
            <div class="users-table-container">
              <table class="users-table">
                <thead>
                  <tr>
                    <th>Nom</th>
                    <th>Prénom</th>
                    <th>Email</th>
                    <th>Rôle</th>
                    <th>Statut</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let user of users" [class.inactive]="!user.actif">
                    <td>{{user.nom}}</td>
                    <td>{{user.prenom}}</td>
                    <td>{{user.email}}</td>
                    <td>
                      <span class="role-badge" [class]="'role-' + user.role.toLowerCase()">
                        {{getRoleLabel(user.role)}}
                      </span>
                    </td>
                    <td>
                      <span class="status-badge" [class.active]="user.actif" [class.inactive]="!user.actif">
                        {{user.actif ? 'Actif' : 'Inactif'}}
                      </span>
                    </td>
                    <td class="actions">
                      <button class="btn-edit" (click)="editUser(user)">✏️</button>
                      <button class="btn-toggle" (click)="toggleUserStatus(user)">
                        {{user.actif ? '🚫' : '✅'}}
                      </button>
                      <button class="btn-delete" (click)="deleteUser(user)" *ngIf="user.email !== currentUser?.email">🗑️</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Add User -->
          <div *ngIf="activeTab === 'add-user'" class="tab-content">
            <h2>Ajouter un nouvel utilisateur</h2>
            <form class="add-user-form" (ngSubmit)="addUser()" #userForm="ngForm">
              <div class="form-row">
                <div class="form-group">
                  <label for="nom">Nom *</label>
                  <input type="text" id="nom" name="nom" [(ngModel)]="newUser.nom" required>
                </div>
                <div class="form-group">
                  <label for="prenom">Prénom *</label>
                  <input type="text" id="prenom" name="prenom" [(ngModel)]="newUser.prenom" required>
                </div>
              </div>
              
              <div class="form-group">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" [(ngModel)]="newUser.email" required>
              </div>
              
              <div class="form-group">
                <label for="role">Rôle *</label>
                <select id="role" name="role" [(ngModel)]="newUser.role" required>
                  <option value="">Sélectionner un rôle</option>
                  <option value="JOUEUR">Joueur</option>
                  <option value="COACH">Entraîneur</option>
                  <option value="STAFF_MEDICAL">Staff médical</option>
                  <option value="RESPONSABLE_FINANCIER">Responsable financier</option>
                  <option value="ADMINISTRATEUR">Administrateur</option>
                </select>
              </div>
              
              <div class="form-actions">
                <button type="submit" class="btn-primary" [disabled]="!userForm.form.valid">
                  Créer l'utilisateur
                </button>
                <button type="button" class="btn-secondary" (click)="resetForm()">
                  Annuler
                </button>
              </div>
            </form>
          </div>

          <!-- Settings -->
          <div *ngIf="activeTab === 'settings'" class="tab-content">
            <h2>Paramètres du système</h2>
            <div class="settings-section">
              <h3>Configuration générale</h3>
              <p>Fonctionnalités à venir...</p>
            </div>
          </div>
        </main>
      </div>
    </div>
  `,
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  @Output() logoutEvent = new EventEmitter<void>();

  activeTab = 'dashboard';
  currentUser: any = null;
  users: User[] = [];
  totalUsers = 0;
  totalPlayers = 0;
  totalCoaches = 0;
  totalMedical = 0;

  newUser: User = {
    nom: '',
    prenom: '',
    email: '',
    role: '',
    actif: true
  };

  constructor() {}

  ngOnInit() {
    this.loadCurrentUser();
    this.loadUsers();
    this.calculateStats();
  }

  loadCurrentUser() {
    // Simuler le chargement de l'utilisateur actuel
    this.currentUser = {
      nom: 'COK',
      prenom: 'Administrateur',
      email: 'admin@cok.tn',
      role: 'ADMINISTRATEUR'
    };
  }

  loadUsers() {
    // Simuler le chargement des utilisateurs
    this.users = [
      {
        id: 1,
        nom: 'COK',
        prenom: 'Administrateur',
        email: 'admin@cok.tn',
        role: 'ADMINISTRATEUR',
        actif: true,
        dateCreation: '2024-01-01'
      }
    ];
  }

  calculateStats() {
    this.totalUsers = this.users.length;
    this.totalPlayers = this.users.filter(u => u.role === 'JOUEUR').length;
    this.totalCoaches = this.users.filter(u => u.role === 'COACH').length;
    this.totalMedical = this.users.filter(u => u.role === 'STAFF_MEDICAL').length;
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  getRoleLabel(role: string): string {
    const roleLabels: { [key: string]: string } = {
      'ADMINISTRATEUR': 'Administrateur',
      'COACH': 'Entraîneur',
      'JOUEUR': 'Joueur',
      'STAFF_MEDICAL': 'Staff médical',
      'RESPONSABLE_FINANCIER': 'Resp. financier'
    };
    return roleLabels[role] || role;
  }

  addUser() {
    if (this.newUser.nom && this.newUser.prenom && this.newUser.email && this.newUser.role) {
      // Ici, on appellerait l'API pour créer l'utilisateur
      console.log('Création utilisateur:', this.newUser);
      
      // Simuler l'ajout
      const user = { ...this.newUser, id: Date.now() };
      this.users.push(user);
      this.calculateStats();
      this.resetForm();
      this.setActiveTab('users');
      
      alert('Utilisateur créé avec succès !');
    }
  }

  editUser(user: User) {
    console.log('Édition utilisateur:', user);
    // Implémenter l'édition
  }

  toggleUserStatus(user: User) {
    user.actif = !user.actif;
    console.log('Statut modifié:', user);
    // Ici, on appellerait l'API pour mettre à jour le statut
  }

  deleteUser(user: User) {
    if (confirm(`Êtes-vous sûr de vouloir supprimer ${user.prenom} ${user.nom} ?`)) {
      this.users = this.users.filter(u => u.id !== user.id);
      this.calculateStats();
      console.log('Utilisateur supprimé:', user);
    }
  }

  resetForm() {
    this.newUser = {
      nom: '',
      prenom: '',
      email: '',
      role: '',
      actif: true
    };
  }

  logout() {
    if (confirm('Êtes-vous sûr de vouloir vous déconnecter ?')) {
      // Nettoyer les données de session
      localStorage.removeItem('sprintbot_access_token');
      localStorage.removeItem('sprintbot_user');

      // Émettre l'événement de déconnexion vers le composant parent
      this.logoutEvent.emit();
    }
  }
}
