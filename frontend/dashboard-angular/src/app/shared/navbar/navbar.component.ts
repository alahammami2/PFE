import { Component, OnInit, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, Utilisateur } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'navbar-cmp',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  currentUser: Utilisateur | null = null;
  isCollapsed = true;

  constructor(
    private element: ElementRef,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  sidebarToggle(): void {
    // Toggle sidebar logic
    const body = document.getElementsByTagName('body')[0];
    if (body.classList.contains('sidebar-mini')) {
      body.classList.remove('sidebar-mini');
    } else {
      body.classList.add('sidebar-mini');
    }
  }

  collapse(): void {
    this.isCollapsed = !this.isCollapsed;
  }

  getTitle(): string {
    const path = this.router.url;
    switch (path) {
      case '/dashboard':
        return 'Tableau de Bord';
      case '/user':
        return 'Profil Utilisateur';
      case '/table':
        return 'Liste des Utilisateurs';
      case '/typography':
        return 'Typographie';
      case '/icons':
        return 'Icônes';
      case '/maps':
        return 'Cartes';
      case '/notifications':
        return 'Notifications';
      default:
        return 'SprintBot';
    }
  }

  logout(): void {
    this.authService.logout();
    this.toastr.info('Vous avez été déconnecté', 'Déconnexion');
    this.router.navigate(['/login']);
  }
}

