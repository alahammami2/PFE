import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-currency-display',
  template: `
    <span class="currency-display" [ngClass]="getColorClass()">
      <mat-icon *ngIf="showIcon" class="currency-icon">{{ getIcon() }}</mat-icon>
      {{ formatCurrency(value) }}
    </span>
  `,
  styles: [`
    .currency-display {
      display: inline-flex;
      align-items: center;
      font-weight: 500;
      
      &.positive {
        color: #4caf50;
      }
      
      &.negative {
        color: #f44336;
      }
      
      &.neutral {
        color: #333;
      }
      
      .currency-icon {
        font-size: 16px;
        width: 16px;
        height: 16px;
        margin-right: 4px;
      }
    }
  `]
})
export class CurrencyDisplayComponent {
  @Input() value: number = 0;
  @Input() currency: string = 'EUR';
  @Input() locale: string = 'fr-FR';
  @Input() showIcon: boolean = false;
  @Input() colorMode: 'auto' | 'positive' | 'negative' | 'neutral' = 'auto';
  
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat(this.locale, {
      style: 'currency',
      currency: this.currency
    }).format(amount);
  }
  
  getColorClass(): string {
    if (this.colorMode !== 'auto') {
      return this.colorMode;
    }
    
    if (this.value > 0) {
      return 'positive';
    } else if (this.value < 0) {
      return 'negative';
    } else {
      return 'neutral';
    }
  }
  
  getIcon(): string {
    if (this.value > 0) {
      return 'trending_up';
    } else if (this.value < 0) {
      return 'trending_down';
    } else {
      return 'trending_flat';
    }
  }
}
