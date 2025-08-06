import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, filter } from 'rxjs/operators';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'SprintBot Finance';
  
  // Navigation
  isHandset = false;
  sidenavOpened = true;
  currentRoute = '';
  
  // Menu items
  menuItems = [
    {
      label: 'Tableau de bord',
      icon: 'dashboard',
      route: '/dashboard',
      roles: ['ADMIN', 'FINANCE_MANAGER', 'FINANCE_USER', 'HR_MANAGER']
    },
    {
      label: 'Budgets',
      icon: 'account_balance_wallet',
      route: '/budgets',
      roles: ['ADMIN', 'FINANCE_MANAGER', 'FINANCE_USER']
    },
    {
      label: 'Transactions',
      icon: 'receipt_long',
      route: '/transactions',
      roles: ['ADMIN', 'FINANCE_MANAGER', 'FINANCE_USER']
    },
    {
      label: 'Sponsors',
      icon: 'handshake',
      route: '/sponsors',
      roles: ['ADMIN', 'FINANCE_MANAGER', 'FINANCE_USER']
    },
    {
      label: 'Salaires',
      icon: 'payments',
      route: '/salaires',
      roles: ['ADMIN', 'FINANCE_MANAGER', 'HR_MANAGER']
    },
    {
      label: 'Rapports',
      icon: 'assessment',
      route: '/rapports',
      roles: ['ADMIN', 'FINANCE_MANAGER']
    }
  ];
  
  // User info
  currentUser: any = null;
  userRoles: string[] = [];
  
  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    private breakpointObserver: BreakpointObserver
  ) {}

  ngOnInit(): void {
    this.initializeResponsiveLayout();
    this.initializeRouterEvents();
    this.loadUserInfo();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Initialise la gestion responsive
   */
  private initializeResponsiveLayout(): void {
    this.breakpointObserver
      .observe([Breakpoints.Handset])
      .pipe(takeUntil(this.destroy$))
      .subscribe(result => {
        this.isHandset = result.matches;
        this.sidenavOpened = !this.isHandset;
      });
  }

  /**
   * Initialise les événements du router
   */
  private initializeRouterEvents(): void {
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe((event: NavigationEnd) => {
        this.currentRoute = event.url;
      });
  }

  /**
   * Charge les informations utilisateur
   */
  private loadUserInfo(): void {
    // TODO: Récupérer les informations utilisateur depuis le service d'authentification
    // Pour l'instant, on simule un utilisateur
    this.currentUser = {
      id: 1,
      nom: 'Admin',
      prenom: 'Finance',
      email: 'admin@sprintbot.com'
    };
    this.userRoles = ['ADMIN', 'FINANCE_MANAGER'];
  }

  /**
   * Vérifie si l'utilisateur a accès à un menu
   */
  hasAccess(menuItem: any): boolean {
    if (!menuItem.roles || menuItem.roles.length === 0) {
      return true;
    }
    
    return menuItem.roles.some((role: string) => this.userRoles.includes(role));
  }

  /**
   * Vérifie si un menu est actif
   */
  isMenuActive(route: string): boolean {
    return this.currentRoute.startsWith(route);
  }

  /**
   * Navigation vers une route
   */
  navigateTo(route: string): void {
    this.router.navigate([route]);
    
    // Ferme le sidenav sur mobile après navigation
    if (this.isHandset) {
      this.sidenavOpened = false;
    }
  }

  /**
   * Toggle du sidenav
   */
  toggleSidenav(): void {
    this.sidenavOpened = !this.sidenavOpened;
  }

  /**
   * Déconnexion
   */
  logout(): void {
    // TODO: Implémenter la déconnexion
    console.log('Déconnexion...');
    this.router.navigate(['/login']);
  }

  /**
   * Obtient le titre de la page courante
   */
  getPageTitle(): string {
    const menuItem = this.menuItems.find(item => this.isMenuActive(item.route));
    return menuItem ? menuItem.label : 'SprintBot Finance';
  }

  /**
   * Obtient l'icône de la page courante
   */
  getPageIcon(): string {
    const menuItem = this.menuItems.find(item => this.isMenuActive(item.route));
    return menuItem ? menuItem.icon : 'account_balance';
  }
}
