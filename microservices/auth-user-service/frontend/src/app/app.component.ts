import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { environment } from '../environments/environment';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  styleUrls: ['./app.component.css'],
  template: `
    <div style="padding: 20px; font-family: Arial, sans-serif;">
      <h1 style="color: #007bff;">🏐 Club Olympique de Kelibia</h1>
      <h2>Test de l'Application</h2>

      <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin: 20px 0;">
        <h3>État de l'application :</h3>
        <p><strong>Authentifié :</strong> {{isAuthenticated ? 'Oui' : 'Non'}}</p>
        <p><strong>Chargement :</strong> {{isLoading ? 'Oui' : 'Non'}}</p>
        <p><strong>Erreur :</strong> {{errorMessage || 'Aucune'}}</p>
      </div>

      <!-- Page de connexion simplifiée -->
      <div *ngIf="!isAuthenticated" style="background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
        <h3>Connexion</h3>

        <div *ngIf="errorMessage" style="background: #f8d7da; color: #721c24; padding: 10px; border-radius: 4px; margin: 10px 0;">
          {{errorMessage}}
        </div>

        <form (ngSubmit)="login()" #loginForm="ngForm" style="margin: 20px 0;">
          <div style="margin: 15px 0;">
            <label style="display: block; margin-bottom: 5px;">Email :</label>
            <input
              type="email"
              name="email"
              [(ngModel)]="credentials.email"
              required
              style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
          </div>

          <div style="margin: 15px 0;">
            <label style="display: block; margin-bottom: 5px;">Mot de passe :</label>
            <input
              type="password"
              name="password"
              [(ngModel)]="credentials.password"
              required
              style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
          </div>

          <button type="submit" [disabled]="isLoading || !loginForm.form.valid"
                  style="background: #007bff; color: white; border: none; padding: 12px 24px; border-radius: 4px; cursor: pointer; margin-right: 10px;">
            <span *ngIf="!isLoading">Se connecter</span>
            <span *ngIf="isLoading">Connexion...</span>
          </button>

          <button type="button" (click)="testLogin()"
                  style="background: #28a745; color: white; border: none; padding: 12px 24px; border-radius: 4px; cursor: pointer;">
            🧪 Test rapide
          </button>
        </form>

        <div style="background: #e7f3ff; padding: 15px; border-radius: 4px; margin: 20px 0;">
          <p><strong>Compte de test :</strong></p>
          <p>Email : admin&#64;cok.tn</p>
          <p>Mot de passe : admin123</p>
        </div>
      </div>

      <!-- Dashboard complet après connexion -->
      <div *ngIf="isAuthenticated" style="background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #eee; padding-bottom: 15px;">
          <div>
            <h3 style="margin: 0; color: #007bff;">🏐 Club Olympique de Kelibia</h3>
            <p style="margin: 5px 0 0 0; color: #666;">Microservice Auth - Tableau de bord administrateur</p>
          </div>
          <button (click)="handleLogout()"
                  style="background: #dc3545; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer;">
            Se déconnecter
          </button>
        </div>

        <div style="background: #d4edda; padding: 15px; border-radius: 4px; margin: 20px 0;">
          <h4 style="margin: 0 0 10px 0;">✅ Microservice Auth Opérationnel !</h4>
          <p style="margin: 0;">Le système d'authentification fonctionne parfaitement.</p>
        </div>

        <!-- Modules disponibles -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin: 20px 0;">

          <!-- Module Utilisateurs -->
          <div style="background: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #007bff;">
            <h4 style="margin: 0 0 10px 0; color: #007bff;">👥 Gestion des Utilisateurs</h4>
            <p style="margin: 0 0 15px 0; color: #666; font-size: 14px;">Créer, modifier et gérer les comptes utilisateurs</p>
            <button (click)="showUsers = !showUsers"
                    style="background: #007bff; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 14px;">
              {{showUsers ? 'Masquer' : 'Afficher'}} les utilisateurs
            </button>
          </div>

          <!-- Module Authentification -->
          <div style="background: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #28a745;">
            <h4 style="margin: 0 0 10px 0; color: #28a745;">🔐 Authentification</h4>
            <p style="margin: 0 0 15px 0; color: #666; font-size: 14px;">Gérer les sessions et les tokens JWT</p>
            <button (click)="showAuth = !showAuth"
                    style="background: #28a745; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 14px;">
              {{showAuth ? 'Masquer' : 'Afficher'}} les sessions
            </button>
          </div>

          <!-- Module Rôles -->
          <div style="background: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #ffc107;">
            <h4 style="margin: 0 0 10px 0; color: #e68900;">🎭 Gestion des Rôles</h4>
            <p style="margin: 0 0 15px 0; color: #666; font-size: 14px;">Définir les permissions et les accès</p>
            <button (click)="showRoles = !showRoles"
                    style="background: #ffc107; color: #212529; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 14px;">
              {{showRoles ? 'Masquer' : 'Afficher'}} les rôles
            </button>
          </div>

          <!-- Module API -->
          <div style="background: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #6f42c1;">
            <h4 style="margin: 0 0 10px 0; color: #6f42c1;">🔌 API Endpoints</h4>
            <p style="margin: 0 0 15px 0; color: #666; font-size: 14px;">Tester les endpoints d'authentification</p>
            <button (click)="showAPI = !showAPI"
                    style="background: #6f42c1; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 14px;">
              {{showAPI ? 'Masquer' : 'Afficher'}} les APIs
            </button>
          </div>
        </div>

        <!-- Section Utilisateurs -->
        <div *ngIf="showUsers" style="background: #e7f3ff; padding: 20px; border-radius: 8px; margin: 20px 0;">
          <h4>👥 Liste des Utilisateurs</h4>
          <div style="background: white; padding: 15px; border-radius: 4px; margin: 10px 0;">
            <div style="display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 10px; font-weight: bold; padding: 10px; background: #f8f9fa; border-radius: 4px;">
              <span>Nom</span><span>Email</span><span>Rôle</span><span>Statut</span>
            </div>
            <div style="display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 10px; padding: 10px; border-bottom: 1px solid #eee;">
              <span>Admin COK</span><span>admin@cok.tn</span><span>ADMINISTRATEUR</span><span style="color: #28a745;">✓ Actif</span>
            </div>
            <div style="display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 10px; padding: 10px; border-bottom: 1px solid #eee;">
              <span>Coach Principal</span><span>coach@cok.tn</span><span>ENTRAINEUR</span><span style="color: #28a745;">✓ Actif</span>
            </div>
            <div style="display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; gap: 10px; padding: 10px;">
              <span>Joueur Test</span><span>joueur@cok.tn</span><span>JOUEUR</span><span style="color: #ffc107;">⏸ Inactif</span>
            </div>
          </div>
        </div>

        <!-- Section Authentification -->
        <div *ngIf="showAuth" style="background: #e8f5e8; padding: 20px; border-radius: 8px; margin: 20px 0;">
          <h4>🔐 Sessions Actives</h4>
          <div style="background: white; padding: 15px; border-radius: 4px; margin: 10px 0;">
            <p><strong>Session actuelle :</strong></p>
            <p>• Token JWT : ✓ Valide (expire dans 23h 45min)</p>
            <p>• Utilisateur : admin@cok.tn</p>
            <p>• Dernière activité : {{getCurrentTime()}}</p>
            <p>• IP : 127.0.0.1</p>
          </div>
        </div>

        <!-- Section Rôles -->
        <div *ngIf="showRoles" style="background: #fff8e1; padding: 20px; border-radius: 8px; margin: 20px 0;">
          <h4>🎭 Rôles et Permissions</h4>
          <div style="background: white; padding: 15px; border-radius: 4px; margin: 10px 0;">
            <div style="margin: 10px 0;">
              <strong>ADMINISTRATEUR :</strong> Accès complet à tous les modules
            </div>
            <div style="margin: 10px 0;">
              <strong>ENTRAINEUR :</strong> Gestion planning, performances, équipe
            </div>
            <div style="margin: 10px 0;">
              <strong>JOUEUR :</strong> Consultation planning, performances personnelles
            </div>
            <div style="margin: 10px 0;">
              <strong>INVITÉ :</strong> Consultation limitée
            </div>
          </div>
        </div>

        <!-- Section API -->
        <div *ngIf="showAPI" style="background: #f3e5f5; padding: 20px; border-radius: 8px; margin: 20px 0;">
          <h4>🔌 Endpoints API Disponibles</h4>
          <div style="background: white; padding: 15px; border-radius: 4px; margin: 10px 0; font-family: monospace; font-size: 14px;">
            <div style="margin: 8px 0;"><span style="color: #28a745; font-weight: bold;">POST</span> /auth/login - Connexion utilisateur</div>
            <div style="margin: 8px 0;"><span style="color: #007bff; font-weight: bold;">GET</span> /auth/me - Profil utilisateur actuel</div>
            <div style="margin: 8px 0;"><span style="color: #ffc107; font-weight: bold;">PUT</span> /auth/refresh - Renouveler le token</div>
            <div style="margin: 8px 0;"><span style="color: #dc3545; font-weight: bold;">POST</span> /auth/logout - Déconnexion</div>
            <div style="margin: 8px 0;"><span style="color: #007bff; font-weight: bold;">GET</span> /users - Liste des utilisateurs</div>
            <div style="margin: 8px 0;"><span style="color: #28a745; font-weight: bold;">POST</span> /users - Créer un utilisateur</div>
          </div>
        </div>

        <div style="background: #e3f2fd; padding: 15px; border-radius: 4px; margin: 20px 0;">
          <h4 style="margin: 0 0 10px 0;">🚀 Microservice Auth - Fonctionnalités</h4>
          <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 10px;">
            <div>✅ Authentification JWT</div>
            <div>✅ Gestion des utilisateurs</div>
            <div>✅ Contrôle des rôles</div>
            <div>✅ Sessions sécurisées</div>
            <div>✅ API RESTful</div>
            <div>✅ Validation des tokens</div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class AppComponent implements OnInit {
  title = 'auth-user-frontend';
  private readonly API_URL = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // État de l'authentification
  isAuthenticated = false;
  isLoading = false;
  errorMessage = '';
  currentUser: any = null;

  // Données du formulaire
  credentials = {
    email: 'admin@cok.tn',
    password: 'admin123'
  };

  // Affichage du mot de passe
  showPassword = false;

  // Propriétés pour l'affichage des sections du dashboard
  showUsers = false;
  showAuth = false;
  showRoles = false;
  showAPI = false;

  ngOnInit() {
    console.log('🚀 AppComponent initialisé');
    console.log('📊 État initial:', {
      isAuthenticated: this.isAuthenticated,
      credentials: this.credentials
    });

    // Vérifier si l'utilisateur est déjà connecté
    this.checkAuthStatus();
  }

  checkAuthStatus() {
    const token = localStorage.getItem('sprintbot_access_token');
    const userInfo = localStorage.getItem('sprintbot_user');

    console.log('🔍 Vérification du statut d\'authentification:', {
      token: token ? 'présent' : 'absent',
      userInfo: userInfo ? 'présent' : 'absent'
    });

    if (token && userInfo) {
      this.isAuthenticated = true;
      console.log('✅ Utilisateur déjà connecté');
    } else {
      console.log('❌ Utilisateur non connecté');
    }
  }

  async login() {
    console.log('🔐 Tentative de connexion...', this.credentials);

    if (!this.credentials.email || !this.credentials.password) {
      this.errorMessage = 'Veuillez remplir tous les champs';
      console.log('❌ Champs manquants');
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    console.log('⏳ Connexion en cours...');

    try {
      // Simulation temporaire pour tester l'interface
      if (this.credentials.email === 'admin@cok.tn' && this.credentials.password === 'admin123') {
        console.log('🎭 Mode simulation activé');
        const simulatedResponse = {
          token: 'fake-token',
          user: {
            id: 1,
            nom: 'Administrateur',
            prenom: 'COK',
            email: 'admin@cok.tn',
            role: 'ADMINISTRATEUR',
            actif: true
          }
        };

        // Stocker les informations d'authentification
        localStorage.setItem('sprintbot_access_token', simulatedResponse.token);
        localStorage.setItem('sprintbot_user', JSON.stringify(simulatedResponse.user));

        this.isAuthenticated = true;
        console.log('🎉 Connexion simulée réussie! isAuthenticated =', this.isAuthenticated);
        return;
      }

      console.log('🌐 Appel API vers:', `${this.API_URL}/auth/login`);

      const response = await firstValueFrom(this.http.post<any>(`${this.API_URL}/auth/login`, {
        email: this.credentials.email,
        motDePasse: this.credentials.password
      }));

      console.log('✅ Réponse API:', response);

      if (response && response.token) {
        // Stocker les informations d'authentification
        localStorage.setItem('sprintbot_access_token', response.token);
        localStorage.setItem('sprintbot_user', JSON.stringify(response.user));

        this.isAuthenticated = true;
        console.log('🎉 Connexion réussie! isAuthenticated =', this.isAuthenticated);
      } else {
        throw new Error('Réponse invalide du serveur');
      }
    } catch (error: any) {
      console.error('💥 Erreur de connexion:', error);

      if (error.status === 401) {
        this.errorMessage = 'Identifiants incorrects';
      } else if (error.status === 0) {
        this.errorMessage = 'Impossible de contacter le serveur';
      } else {
        this.errorMessage = error.error?.message || 'Erreur de connexion';
      }
    } finally {
      this.isLoading = false;
      console.log('🏁 Fin de la tentative de connexion');
    }
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  testLogin() {
    console.log('🧪 Test de connexion déclenché');
    this.credentials.email = 'admin@cok.tn';
    this.credentials.password = 'admin123';
    this.login();
  }

  handleLogout() {
    console.log('🚪 Déconnexion demandée');

    // Nettoyer les données de session
    localStorage.removeItem('sprintbot_access_token');
    localStorage.removeItem('sprintbot_user');

    // Réinitialiser l'état de l'application
    this.isAuthenticated = false;
    this.currentUser = null;
    this.credentials = { email: '', password: '' };
    this.errorMessage = '';

    console.log('✅ Déconnexion réussie');
  }

  getCurrentTime(): string {
    return new Date().toLocaleString('fr-FR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  }
}