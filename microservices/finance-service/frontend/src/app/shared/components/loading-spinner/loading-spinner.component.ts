import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading-spinner',
  template: `
    <div class="loading-container" [ngClass]="{'overlay': overlay}">
      <mat-spinner [diameter]="diameter" [color]="color"></mat-spinner>
      <p *ngIf="message" class="loading-message">{{ message }}</p>
    </div>
  `,
  styles: [`
    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 20px;
      
      &.overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: rgba(255, 255, 255, 0.8);
        z-index: 9999;
      }
      
      .loading-message {
        margin-top: 16px;
        color: #666;
        font-size: 14px;
        text-align: center;
      }
    }
  `]
})
export class LoadingSpinnerComponent {
  @Input() diameter: number = 40;
  @Input() color: 'primary' | 'accent' | 'warn' = 'primary';
  @Input() message: string = '';
  @Input() overlay: boolean = false;
}
