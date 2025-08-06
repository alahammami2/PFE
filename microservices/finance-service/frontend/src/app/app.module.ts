import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

// Angular Material
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioModule } from '@angular/material/radio';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatStepperModule } from '@angular/material/stepper';

// Third party
import { NgChartsModule } from 'ng2-charts';
import { ToastrModule } from 'ngx-toastr';
import { NgxLoadingModule } from 'ngx-loading';
import { NgxPaginationModule } from 'ngx-pagination';
import { NgxMaskDirective, NgxMaskPipe, provideNgxMask } from 'ngx-mask';
import { NgxCurrencyDirective } from 'ngx-currency';

// App components
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Core components
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { BudgetListComponent } from './components/budget/budget-list/budget-list.component';
import { BudgetFormComponent } from './components/budget/budget-form/budget-form.component';
import { BudgetDetailComponent } from './components/budget/budget-detail/budget-detail.component';
import { TransactionListComponent } from './components/transaction/transaction-list/transaction-list.component';
import { TransactionFormComponent } from './components/transaction/transaction-form/transaction-form.component';
import { TransactionDetailComponent } from './components/transaction/transaction-detail/transaction-detail.component';
import { SponsorListComponent } from './components/sponsor/sponsor-list/sponsor-list.component';
import { SponsorFormComponent } from './components/sponsor/sponsor-form/sponsor-form.component';
import { SponsorDetailComponent } from './components/sponsor/sponsor-detail/sponsor-detail.component';
import { SalaireListComponent } from './components/salaire/salaire-list/salaire-list.component';
import { SalaireFormComponent } from './components/salaire/salaire-form/salaire-form.component';
import { SalaireDetailComponent } from './components/salaire/salaire-detail/salaire-detail.component';
import { RapportComponent } from './components/rapport/rapport.component';

// Shared components
import { ConfirmDialogComponent } from './shared/components/confirm-dialog/confirm-dialog.component';
import { LoadingSpinnerComponent } from './shared/components/loading-spinner/loading-spinner.component';
import { ErrorMessageComponent } from './shared/components/error-message/error-message.component';
import { CurrencyDisplayComponent } from './shared/components/currency-display/currency-display.component';
import { StatusChipComponent } from './shared/components/status-chip/status-chip.component';
import { ChartComponent } from './shared/components/chart/chart.component';

// Pipes
import { CurrencyFormatPipe } from './shared/pipes/currency-format.pipe';
import { DateFormatPipe } from './shared/pipes/date-format.pipe';
import { StatusTranslatePipe } from './shared/pipes/status-translate.pipe';

// Interceptors
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { ErrorInterceptor } from './core/interceptors/error.interceptor';
import { LoadingInterceptor } from './core/interceptors/loading.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    
    // Core components
    DashboardComponent,
    BudgetListComponent,
    BudgetFormComponent,
    BudgetDetailComponent,
    TransactionListComponent,
    TransactionFormComponent,
    TransactionDetailComponent,
    SponsorListComponent,
    SponsorFormComponent,
    SponsorDetailComponent,
    SalaireListComponent,
    SalaireFormComponent,
    SalaireDetailComponent,
    RapportComponent,
    
    // Shared components
    ConfirmDialogComponent,
    LoadingSpinnerComponent,
    ErrorMessageComponent,
    CurrencyDisplayComponent,
    StatusChipComponent,
    ChartComponent,
    
    // Pipes
    CurrencyFormatPipe,
    DateFormatPipe,
    StatusTranslatePipe
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    AppRoutingModule,
    
    // Angular Material
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatChipsModule,
    MatBadgeModule,
    MatTabsModule,
    MatExpansionModule,
    MatSlideToggleModule,
    MatCheckboxModule,
    MatRadioModule,
    MatMenuModule,
    MatTooltipModule,
    MatStepperModule,
    
    // Third party
    NgChartsModule,
    ToastrModule.forRoot({
      timeOut: 3000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
      progressBar: true,
      closeButton: true
    }),
    NgxLoadingModule.forRoot({}),
    NgxPaginationModule,
    NgxMaskDirective,
    NgxMaskPipe,
    NgxCurrencyDirective
  ],
  providers: [
    provideNgxMask(),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: LoadingInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
