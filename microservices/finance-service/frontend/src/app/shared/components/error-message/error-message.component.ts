import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-error-message',
  template: `
    <div class="error-container" [ngClass]="type">
      <mat-icon class="error-icon">{{ getIcon() }}</mat-icon>
      <div class="error-content">
        <h3 class="error-title">{{ title }}</h3>
        <p class="error-message">{{ message }}</p>
        <div class="error-actions" *ngIf="showRetry || showClose">
          <button *ngIf="showRetry" mat-raised-button color="primary" (click)="onRetry()">
            <mat-icon>refresh</mat-icon>
            Réessayer
          </button>
          <button *ngIf="showClose" mat-button (click)="onClose()">
            Fermer
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .error-container {
      display: flex;
      align-items: flex-start;
      padding: 20px;
      border-radius: 8px;
      margin: 16px 0;
      
      &.error {
        background-color: #ffebee;
        border-left: 4px solid #f44336;
        
        .error-icon {
          color: #f44336;
        }
      }
      
      &.warning {
        background-color: #fff3e0;
        border-left: 4px solid #ff9800;
        
        .error-icon {
          color: #ff9800;
        }
      }
      
      &.info {
        background-color: #e3f2fd;
        border-left: 4px solid #2196f3;
        
        .error-icon {
          color: #2196f3;
        }
      }
      
      .error-icon {
        font-size: 24px;
        width: 24px;
        height: 24px;
        margin-right: 16px;
        margin-top: 4px;
      }
      
      .error-content {
        flex: 1;
        
        .error-title {
          margin: 0 0 8px 0;
          font-size: 16px;
          font-weight: 500;
        }
        
        .error-message {
          margin: 0 0 16px 0;
          color: #666;
          line-height: 1.5;
        }
        
        .error-actions {
          display: flex;
          gap: 12px;
        }
      }
    }
  `]
})
export class ErrorMessageComponent {
  @Input() type: 'error' | 'warning' | 'info' = 'error';
  @Input() title: string = 'Erreur';
  @Input() message: string = '';
  @Input() showRetry: boolean = false;
  @Input() showClose: boolean = false;
  
  @Output() retry = new EventEmitter<void>();
  @Output() close = new EventEmitter<void>();
  
  getIcon(): string {
    switch (this.type) {
      case 'error': return 'error';
      case 'warning': return 'warning';
      case 'info': return 'info';
      default: return 'error';
    }
  }
  
  onRetry(): void {
    this.retry.emit();
  }
  
  onClose(): void {
    this.close.emit();
  }
}
