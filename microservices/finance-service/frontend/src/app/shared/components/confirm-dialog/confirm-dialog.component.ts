import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  type?: 'info' | 'warning' | 'error';
}

@Component({
  selector: 'app-confirm-dialog',
  template: `
    <div class="confirm-dialog">
      <h2 mat-dialog-title class="dialog-title">
        <mat-icon [ngClass]="'icon-' + data.type">{{ getIcon() }}</mat-icon>
        {{ data.title }}
      </h2>
      
      <mat-dialog-content class="dialog-content">
        <p>{{ data.message }}</p>
      </mat-dialog-content>
      
      <mat-dialog-actions class="dialog-actions">
        <button mat-button (click)="onCancel()">
          {{ data.cancelText || 'Annuler' }}
        </button>
        <button 
          mat-raised-button 
          [color]="getButtonColor()" 
          (click)="onConfirm()"
          cdkFocusInitial>
          {{ data.confirmText || 'Confirmer' }}
        </button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    .confirm-dialog {
      .dialog-title {
        display: flex;
        align-items: center;
        margin-bottom: 16px;
        
        mat-icon {
          margin-right: 12px;
          font-size: 24px;
          width: 24px;
          height: 24px;
          
          &.icon-info { color: #2196f3; }
          &.icon-warning { color: #ff9800; }
          &.icon-error { color: #f44336; }
        }
      }
      
      .dialog-content {
        margin-bottom: 16px;
        
        p {
          margin: 0;
          line-height: 1.5;
          color: #666;
        }
      }
      
      .dialog-actions {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        margin: 0;
        padding: 0;
      }
    }
  `]
})
export class ConfirmDialogComponent {
  
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData
  ) {
    // Valeurs par défaut
    this.data.type = this.data.type || 'info';
  }
  
  getIcon(): string {
    switch (this.data.type) {
      case 'warning': return 'warning';
      case 'error': return 'error';
      default: return 'help';
    }
  }
  
  getButtonColor(): string {
    switch (this.data.type) {
      case 'warning': return 'accent';
      case 'error': return 'warn';
      default: return 'primary';
    }
  }
  
  onConfirm(): void {
    this.dialogRef.close(true);
  }
  
  onCancel(): void {
    this.dialogRef.close(false);
  }
}
