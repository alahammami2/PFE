import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-status-chip',
  template: `
    <mat-chip 
      [ngClass]="getChipClass()" 
      [color]="getChipColor()"
      [selected]="true">
      <mat-icon *ngIf="showIcon" class="chip-icon">{{ getIcon() }}</mat-icon>
      {{ getDisplayText() }}
    </mat-chip>
  `,
  styles: [`
    .mat-chip {
      font-size: 12px;
      font-weight: 500;
      
      .chip-icon {
        font-size: 14px;
        width: 14px;
        height: 14px;
        margin-right: 4px;
      }
      
      &.status-actif { background-color: #e8f5e8; color: #2e7d32; }
      &.status-inactif { background-color: #f5f5f5; color: #757575; }
      &.status-en-attente { background-color: #fff3e0; color: #f57c00; }
      &.status-valide { background-color: #e8f5e8; color: #2e7d32; }
      &.status-rejete { background-color: #ffebee; color: #c62828; }
      &.status-expire { background-color: #ffebee; color: #c62828; }
      &.status-suspendu { background-color: #fff3e0; color: #f57c00; }
      &.status-paye { background-color: #e8f5e8; color: #2e7d32; }
      &.status-calcule { background-color: #e3f2fd; color: #1565c0; }
    }
  `]
})
export class StatusChipComponent {
  @Input() status: string = '';
  @Input() type: 'budget' | 'transaction' | 'sponsor' | 'salaire' | 'generic' = 'generic';
  @Input() showIcon: boolean = true;
  @Input() customText?: string;
  
  getDisplayText(): string {
    if (this.customText) {
      return this.customText;
    }
    
    const translations: { [key: string]: string } = {
      'ACTIF': 'Actif',
      'INACTIF': 'Inactif',
      'CLOTURE': 'Clôturé',
      'SUSPENDU': 'Suspendu',
      'EN_ATTENTE': 'En attente',
      'VALIDEE': 'Validée',
      'REJETEE': 'Rejetée',
      'ANNULEE': 'Annulée',
      'EXPIRE': 'Expiré',
      'RESILIE': 'Résilié',
      'CALCULE': 'Calculé',
      'VALIDE': 'Validé',
      'PAYE': 'Payé',
      'ATTENDU': 'Attendu',
      'RECU': 'Reçu',
      'EN_RETARD': 'En retard'
    };
    
    return translations[this.status.toUpperCase()] || this.status;
  }
  
  getChipClass(): string {
    const statusKey = this.status.toLowerCase().replace('_', '-');
    return `status-${statusKey}`;
  }
  
  getChipColor(): 'primary' | 'accent' | 'warn' | undefined {
    switch (this.status.toUpperCase()) {
      case 'ACTIF':
      case 'VALIDEE':
      case 'VALIDE':
      case 'PAYE':
      case 'RECU':
        return 'primary';
      case 'EN_ATTENTE':
      case 'CALCULE':
      case 'ATTENDU':
        return 'accent';
      case 'REJETEE':
      case 'ANNULEE':
      case 'EXPIRE':
      case 'EN_RETARD':
        return 'warn';
      default:
        return undefined;
    }
  }
  
  getIcon(): string {
    switch (this.status.toUpperCase()) {
      case 'ACTIF':
      case 'VALIDEE':
      case 'VALIDE':
        return 'check_circle';
      case 'EN_ATTENTE':
      case 'CALCULE':
        return 'schedule';
      case 'REJETEE':
      case 'ANNULEE':
        return 'cancel';
      case 'EXPIRE':
        return 'event_busy';
      case 'SUSPENDU':
        return 'pause_circle';
      case 'PAYE':
      case 'RECU':
        return 'payment';
      case 'EN_RETARD':
        return 'warning';
      default:
        return 'info';
    }
  }
}
