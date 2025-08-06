import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterModule } from '@angular/router';
import { NavbarComponent } from './components/shared/navbar/navbar.component';
import { SidebarComponent } from './components/shared/sidebar/sidebar.component';
import { LoadingComponent } from './shared/components/loading/loading.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterModule,
    NavbarComponent,
    SidebarComponent,
    LoadingComponent
  ],
  template: `
    <div class="app-container">
      <app-navbar></app-navbar>

      <div class="main-content">
        <app-sidebar></app-sidebar>

        <main class="content-area">
          <router-outlet></router-outlet>
        </main>
      </div>

      <!-- Composant de chargement global -->
      <app-loading></app-loading>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }

    .main-content {
      flex: 1;
      display: flex;
      background-color: var(--light-color);
    }

    .content-area {
      flex: 1;
      padding: 1.5rem;
      margin-left: 250px;
      transition: margin-left 0.3s ease;
      min-height: calc(100vh - 60px);
    }

    @media (max-width: 768px) {
      .content-area {
        margin-left: 0;
        padding: 1rem;
      }
    }

    /* Animation pour le chargement des pages */
    :host ::ng-deep router-outlet + * {
      animation: fadeIn 0.3s ease-in;
    }

    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: translateY(10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
  `]
})
export class AppComponent {
  title = 'SprintBot - Planning & Performance';
}
