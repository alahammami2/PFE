import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Angular Material
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

// Chart.js
import { NgChartsModule } from 'ng2-charts';

// Composants partagés
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';
import { ErrorMessageComponent } from './components/error-message/error-message.component';
import { CurrencyDisplayComponent } from './components/currency-display/currency-display.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { StatusChipComponent } from './components/status-chip/status-chip.component';
import { ChartComponent } from './components/chart/chart.component';

// Pipes partagés
import { CurrencyFormatPipe } from './pipes/currency-format.pipe';
import { DateFormatPipe } from './pipes/date-format.pipe';
import { StatusTranslatePipe } from './pipes/status-translate.pipe';

const COMPONENTS = [
  LoadingSpinnerComponent,
  ErrorMessageComponent,
  CurrencyDisplayComponent,
  ConfirmDialogComponent,
  StatusChipComponent,
  ChartComponent
];

const PIPES = [
  CurrencyFormatPipe,
  DateFormatPipe,
  StatusTranslatePipe
];

const MATERIAL_MODULES = [
  MatButtonModule,
  MatCardModule,
  MatChipsModule,
  MatDialogModule,
  MatIconModule,
  MatProgressBarModule,
  MatProgressSpinnerModule
];

@NgModule({
  declarations: [
    ...COMPONENTS,
    ...PIPES
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ...MATERIAL_MODULES,
    NgChartsModule
  ],
  exports: [
    // Modules Angular
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    
    // Modules Material
    ...MATERIAL_MODULES,
    
    // Chart.js
    NgChartsModule,
    
    // Composants partagés
    ...COMPONENTS,
    
    // Pipes partagés
    ...PIPES
  ]
})
export class SharedModule { }
