import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { EntrainementService } from '../../services/entrainement.service';
import { 
  Entrainement,
  TypeEntrainement,
  StatutEntrainement,
  TypeEntrainementLabels,
  StatutEntrainementLabels,
  TypeEntrainementColors,
  StatutEntrainementColors
} from '../../models/entrainement.model';

interface CalendarDay {
  date: Date;
  isCurrentMonth: boolean;
  isToday: boolean;
  entrainements: Entrainement[];
}

interface CalendarWeek {
  days: CalendarDay[];
}

@Component({
  selector: 'app-calendrier',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="calendrier-container fade-in">
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <i class="material-icons me-2">calendar_today</i>
            Calendrier des Entraînements
          </h1>
          <p class="page-subtitle">Planification et vue d'ensemble des séances</p>
        </div>
        <div class="header-actions">
          <a routerLink="/entrainements/nouveau" class="btn btn-primary">
            <i class="material-icons me-1">add</i>
            Nouvel Entraînement
          </a>
        </div>
      </div>

      <!-- Navigation du calendrier -->
      <div class="calendar-navigation">
        <div class="nav-controls">
          <button class="btn btn-outline-primary" (click)="previousMonth()">
            <i class="material-icons">chevron_left</i>
          </button>
          
          <div class="current-month">
            <h3>{{ getCurrentMonthLabel() }}</h3>
            <button class="btn btn-sm btn-outline-secondary" (click)="goToToday()">
              Aujourd'hui
            </button>
          </div>
          
          <button class="btn btn-outline-primary" (click)="nextMonth()">
            <i class="material-icons">chevron_right</i>
          </button>
        </div>

        <div class="view-options">
          <div class="btn-group" role="group">
            <button 
              class="btn"
              [class.btn-primary]="viewMode === 'month'"
              [class.btn-outline-primary]="viewMode !== 'month'"
              (click)="viewMode = 'month'"
            >
              Mois
            </button>
            <button 
              class="btn"
              [class.btn-primary]="viewMode === 'week'"
              [class.btn-outline-primary]="viewMode !== 'week'"
              (click)="viewMode = 'week'"
            >
              Semaine
            </button>
            <button 
              class="btn"
              [class.btn-primary]="viewMode === 'list'"
              [class.btn-outline-primary]="viewMode !== 'list'"
              (click)="viewMode = 'list'"
            >
              Liste
            </button>
          </div>
        </div>
      </div>

      <!-- Filtres -->
      <div class="calendar-filters">
        <div class="filter-group">
          <label class="form-label">Type d'entraînement</label>
          <select class="form-select form-select-sm" [(ngModel)]="selectedType" (change)="applyFilters()">
            <option value="">Tous les types</option>
            <option *ngFor="let type of typeOptions" [value]="type.value">
              {{ type.label }}
            </option>
          </select>
        </div>

        <div class="filter-group">
          <label class="form-label">Statut</label>
          <select class="form-select form-select-sm" [(ngModel)]="selectedStatut" (change)="applyFilters()">
            <option value="">Tous les statuts</option>
            <option *ngFor="let statut of statutOptions" [value]="statut.value">
              {{ statut.label }}
            </option>
          </select>
        </div>

        <div class="filter-group">
          <button class="btn btn-sm btn-outline-secondary" (click)="clearFilters()">
            <i class="material-icons me-1">clear</i>
            Effacer
          </button>
        </div>
      </div>

      <!-- Vue mensuelle -->
      <div *ngIf="viewMode === 'month'" class="calendar-month">
        <div class="calendar-header">
          <div class="day-header" *ngFor="let day of dayLabels">{{ day }}</div>
        </div>
        
        <div class="calendar-body">
          <div class="calendar-week" *ngFor="let week of calendarWeeks">
            <div 
              class="calendar-day"
              *ngFor="let day of week.days"
              [class.other-month]="!day.isCurrentMonth"
              [class.today]="day.isToday"
              [class.has-events]="day.entrainements.length > 0"
            >
              <div class="day-number">{{ day.date.getDate() }}</div>
              
              <div class="day-events">
                <div 
                  *ngFor="let entrainement of day.entrainements | slice:0:3" 
                  class="event-item"
                  [style.background-color]="getTypeColor(entrainement.type)"
                  [routerLink]="['/entrainements', entrainement.id]"
                  [title]="entrainement.titre + ' - ' + entrainement.heureDebut"
                >
                  <span class="event-time">{{ entrainement.heureDebut }}</span>
                  <span class="event-title">{{ entrainement.titre | slice:0:15 }}{{ entrainement.titre.length > 15 ? '...' : '' }}</span>
                </div>
                
                <div *ngIf="day.entrainements.length > 3" class="more-events">
                  +{{ day.entrainements.length - 3 }} autres
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Vue hebdomadaire -->
      <div *ngIf="viewMode === 'week'" class="calendar-week-view">
        <div class="week-header">
          <div class="time-column"></div>
          <div class="day-column" *ngFor="let day of currentWeekDays">
            <div class="day-label">{{ getDayLabel(day.date) }}</div>
            <div class="day-date" [class.today]="day.isToday">{{ day.date.getDate() }}</div>
          </div>
        </div>
        
        <div class="week-body">
          <div class="time-slot" *ngFor="let hour of timeSlots">
            <div class="time-label">{{ hour }}:00</div>
            <div class="hour-column" *ngFor="let day of currentWeekDays">
              <div 
                *ngFor="let entrainement of getEntrainementsForHour(day.entrainements, hour)"
                class="week-event"
                [style.background-color]="getTypeColor(entrainement.type)"
                [routerLink]="['/entrainements', entrainement.id]"
              >
                <div class="event-time">{{ entrainement.heureDebut }} - {{ entrainement.heureFin }}</div>
                <div class="event-title">{{ entrainement.titre }}</div>
                <div class="event-location">{{ entrainement.lieu }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Vue liste -->
      <div *ngIf="viewMode === 'list'" class="calendar-list">
        <div *ngFor="let group of groupedEntrainements" class="list-group">
          <h5 class="list-date">{{ group.date }}</h5>
          
          <div class="list-events">
            <div 
              *ngFor="let entrainement of group.entrainements"
              class="list-event"
              [routerLink]="['/entrainements', entrainement.id]"
            >
              <div class="event-time-badge">
                <div class="time">{{ entrainement.heureDebut }}</div>
                <div class="duration">{{ formatDuree(entrainement) }}</div>
              </div>
              
              <div class="event-content">
                <h6 class="event-title">{{ entrainement.titre }}</h6>
                <div class="event-details">
                  <span class="event-location">
                    <i class="material-icons">location_on</i>
                    {{ entrainement.lieu }}
                  </span>
                  <span class="event-participants">
                    <i class="material-icons">people</i>
                    {{ entrainement.nombreInscrits || 0 }}/{{ entrainement.nombreMaxParticipants }}
                  </span>
                </div>
              </div>
              
              <div class="event-badges">
                <span 
                  class="badge badge-type"
                  [style.background-color]="getTypeColor(entrainement.type)"
                >
                  {{ getTypeLabel(entrainement.type) }}
                </span>
                <span 
                  class="badge badge-status"
                  [style.background-color]="getStatutColor(entrainement.statut!)"
                >
                  {{ getStatutLabel(entrainement.statut!) }}
                </span>
              </div>
            </div>
          </div>
        </div>
        
        <div *ngIf="groupedEntrainements.length === 0" class="text-center py-5">
          <i class="material-icons text-muted" style="font-size: 4rem;">event_note</i>
          <h5 class="text-muted mt-3">Aucun entraînement ce mois</h5>
          <p class="text-muted">Planifiez votre premier entraînement pour ce mois.</p>
          <a routerLink="/entrainements/nouveau" class="btn btn-primary">
            <i class="material-icons me-1">add</i>
            Créer un entraînement
          </a>
        </div>
      </div>

      <!-- Légende -->
      <div class="calendar-legend">
        <h6>Légende :</h6>
        <div class="legend-items">
          <div *ngFor="let type of typeOptions" class="legend-item">
            <div 
              class="legend-color"
              [style.background-color]="getTypeColorByValue(type.value)"
            ></div>
            <span>{{ type.label }}</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .calendrier-container {
      max-width: 1400px;
      margin: 0 auto;
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 2rem;
      
      .header-content {
        .page-title {
          color: var(--primary-color);
          font-weight: 600;
          margin-bottom: 0.5rem;
          display: flex;
          align-items: center;
        }
        
        .page-subtitle {
          color: var(--secondary-color);
          margin: 0;
        }
      }
    }

    .calendar-navigation {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
      padding: 1rem;
      background: white;
      border-radius: var(--border-radius);
      box-shadow: var(--box-shadow);
      
      .nav-controls {
        display: flex;
        align-items: center;
        gap: 1rem;
        
        .current-month {
          text-align: center;
          
          h3 {
            margin: 0 0 0.5rem 0;
            color: var(--primary-color);
          }
        }
      }
    }

    .calendar-filters {
      display: flex;
      gap: 1rem;
      margin-bottom: 1.5rem;
      padding: 1rem;
      background: white;
      border-radius: var(--border-radius);
      box-shadow: var(--box-shadow);
      
      .filter-group {
        display: flex;
        flex-direction: column;
        
        .form-label {
          font-size: 0.8rem;
          margin-bottom: 0.25rem;
          color: var(--secondary-color);
        }
      }
    }

    /* Vue mensuelle */
    .calendar-month {
      background: white;
      border-radius: var(--border-radius);
      box-shadow: var(--box-shadow);
      overflow: hidden;
    }

    .calendar-header {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      background: var(--primary-color);
      color: white;
      
      .day-header {
        padding: 1rem;
        text-align: center;
        font-weight: 500;
      }
    }

    .calendar-body {
      .calendar-week {
        display: grid;
        grid-template-columns: repeat(7, 1fr);
        border-bottom: 1px solid #eee;
        
        &:last-child {
          border-bottom: none;
        }
      }
      
      .calendar-day {
        min-height: 120px;
        padding: 0.5rem;
        border-right: 1px solid #eee;
        position: relative;
        
        &:last-child {
          border-right: none;
        }
        
        &.other-month {
          background: #f8f9fa;
          color: #999;
        }
        
        &.today {
          background: rgba(25, 118, 210, 0.1);
          
          .day-number {
            background: var(--primary-color);
            color: white;
            border-radius: 50%;
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }
        
        &.has-events {
          cursor: pointer;
        }
        
        .day-number {
          font-weight: 500;
          margin-bottom: 0.5rem;
        }
        
        .day-events {
          .event-item {
            display: block;
            padding: 0.25rem 0.5rem;
            margin-bottom: 0.25rem;
            border-radius: 3px;
            color: white;
            text-decoration: none;
            font-size: 0.75rem;
            cursor: pointer;
            transition: var(--transition);
            
            &:hover {
              opacity: 0.8;
              transform: translateY(-1px);
            }
            
            .event-time {
              font-weight: 500;
              margin-right: 0.25rem;
            }
          }
          
          .more-events {
            font-size: 0.7rem;
            color: #666;
            text-align: center;
            padding: 0.25rem;
          }
        }
      }
    }

    /* Vue hebdomadaire */
    .calendar-week-view {
      background: white;
      border-radius: var(--border-radius);
      box-shadow: var(--box-shadow);
      overflow: hidden;
    }

    .week-header {
      display: grid;
      grid-template-columns: 80px repeat(7, 1fr);
      background: var(--primary-color);
      color: white;
      
      .time-column {
        padding: 1rem;
      }
      
      .day-column {
        padding: 1rem;
        text-align: center;
        border-left: 1px solid rgba(255,255,255,0.2);
        
        .day-label {
          font-weight: 500;
          margin-bottom: 0.25rem;
        }
        
        .day-date {
          font-size: 1.2rem;
          
          &.today {
            background: rgba(255,255,255,0.2);
            border-radius: 50%;
            width: 30px;
            height: 30px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto;
          }
        }
      }
    }

    .week-body {
      .time-slot {
        display: grid;
        grid-template-columns: 80px repeat(7, 1fr);
        border-bottom: 1px solid #eee;
        min-height: 60px;
        
        .time-label {
          padding: 1rem;
          font-size: 0.8rem;
          color: #666;
          border-right: 1px solid #eee;
        }
        
        .hour-column {
          border-left: 1px solid #eee;
          position: relative;
          
          .week-event {
            position: absolute;
            left: 2px;
            right: 2px;
            padding: 0.25rem 0.5rem;
            border-radius: 3px;
            color: white;
            font-size: 0.75rem;
            cursor: pointer;
            text-decoration: none;
            
            &:hover {
              opacity: 0.8;
            }
            
            .event-time {
              font-weight: 500;
            }
            
            .event-title {
              font-weight: 500;
              margin: 0.25rem 0;
            }
            
            .event-location {
              font-size: 0.7rem;
              opacity: 0.9;
            }
          }
        }
      }
    }

    /* Vue liste */
    .calendar-list {
      .list-group {
        margin-bottom: 2rem;
        
        .list-date {
          color: var(--primary-color);
          font-weight: 600;
          margin-bottom: 1rem;
          padding-bottom: 0.5rem;
          border-bottom: 2px solid var(--primary-color);
        }
        
        .list-events {
          .list-event {
            display: flex;
            align-items: center;
            padding: 1rem;
            background: white;
            border-radius: var(--border-radius);
            box-shadow: var(--box-shadow);
            margin-bottom: 1rem;
            text-decoration: none;
            color: inherit;
            transition: var(--transition);
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 8px 25px rgba(0,0,0,0.15);
            }
            
            .event-time-badge {
              text-align: center;
              margin-right: 1rem;
              min-width: 80px;
              
              .time {
                font-weight: 600;
                color: var(--primary-color);
                font-size: 1.1rem;
              }
              
              .duration {
                font-size: 0.8rem;
                color: #666;
              }
            }
            
            .event-content {
              flex: 1;
              
              .event-title {
                margin: 0 0 0.5rem 0;
                color: var(--primary-color);
                font-weight: 500;
              }
              
              .event-details {
                display: flex;
                gap: 1rem;
                font-size: 0.9rem;
                color: var(--secondary-color);
                
                span {
                  display: flex;
                  align-items: center;
                  
                  .material-icons {
                    font-size: 1rem;
                    margin-right: 0.25rem;
                  }
                }
              }
            }
            
            .event-badges {
              display: flex;
              flex-direction: column;
              gap: 0.25rem;
            }
          }
        }
      }
    }

    .calendar-legend {
      margin-top: 2rem;
      padding: 1rem;
      background: white;
      border-radius: var(--border-radius);
      box-shadow: var(--box-shadow);
      
      h6 {
        margin-bottom: 1rem;
        color: var(--primary-color);
      }
      
      .legend-items {
        display: flex;
        gap: 1rem;
        flex-wrap: wrap;
        
        .legend-item {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          
          .legend-color {
            width: 16px;
            height: 16px;
            border-radius: 3px;
          }
          
          span {
            font-size: 0.9rem;
            color: var(--secondary-color);
          }
        }
      }
    }

    .badge {
      font-size: 0.7rem;
      padding: 0.25rem 0.5rem;
      border-radius: 0.25rem;
    }

    @media (max-width: 768px) {
      .page-header {
        flex-direction: column;
        gap: 1rem;
        
        .header-actions {
          width: 100%;
        }
      }
      
      .calendar-navigation {
        flex-direction: column;
        gap: 1rem;
      }
      
      .calendar-filters {
        flex-direction: column;
      }
      
      .calendar-day {
        min-height: 80px;
      }
      
      .week-header,
      .week-body .time-slot {
        grid-template-columns: 60px repeat(7, 1fr);
      }
      
      .list-event {
        flex-direction: column;
        align-items: flex-start;
        
        .event-time-badge {
          margin-right: 0;
          margin-bottom: 0.5rem;
        }
        
        .event-badges {
          flex-direction: row;
          margin-top: 0.5rem;
        }
      }
    }
  `]
})
export class CalendrierComponent implements OnInit {
  currentDate = new Date();
  viewMode: 'month' | 'week' | 'list' = 'month';
  selectedType = '';
  selectedStatut = '';
  
  entrainements: Entrainement[] = [];
  filteredEntrainements: Entrainement[] = [];
  
  calendarWeeks: CalendarWeek[] = [];
  currentWeekDays: CalendarDay[] = [];
  groupedEntrainements: { date: string; entrainements: Entrainement[] }[] = [];
  
  dayLabels = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];
  timeSlots = Array.from({ length: 14 }, (_, i) => i + 8); // 8h à 21h
  
  typeOptions = Object.values(TypeEntrainement).map(type => ({
    value: type,
    label: TypeEntrainementLabels[type]
  }));

  statutOptions = Object.values(StatutEntrainement).map(statut => ({
    value: statut,
    label: StatutEntrainementLabels[statut]
  }));

  constructor(private entrainementService: EntrainementService) {}

  ngOnInit(): void {
    this.loadEntrainements();
  }

  private loadEntrainements(): void {
    this.entrainementService.getAllEntrainements().subscribe({
      next: (entrainements) => {
        this.entrainements = entrainements;
        this.applyFilters();
        this.generateCalendar();
      },
      error: (error) => {
        console.error('Erreur lors du chargement:', error);
      }
    });
  }

  applyFilters(): void {
    this.filteredEntrainements = this.entrainements.filter(entrainement => {
      if (this.selectedType && entrainement.type !== this.selectedType) return false;
      if (this.selectedStatut && entrainement.statut !== this.selectedStatut) return false;
      return true;
    });
    
    this.generateCalendar();
  }

  clearFilters(): void {
    this.selectedType = '';
    this.selectedStatut = '';
    this.applyFilters();
  }

  generateCalendar(): void {
    if (this.viewMode === 'month') {
      this.generateMonthView();
    } else if (this.viewMode === 'week') {
      this.generateWeekView();
    } else {
      this.generateListView();
    }
  }

  private generateMonthView(): void {
    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();
    
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    
    // Commencer par le lundi de la semaine contenant le premier jour
    const startDate = new Date(firstDay);
    startDate.setDate(startDate.getDate() - (startDate.getDay() === 0 ? 6 : startDate.getDay() - 1));
    
    // Finir par le dimanche de la semaine contenant le dernier jour
    const endDate = new Date(lastDay);
    endDate.setDate(endDate.getDate() + (7 - (endDate.getDay() === 0 ? 7 : endDate.getDay())));
    
    this.calendarWeeks = [];
    const currentWeek: CalendarDay[] = [];
    
    for (let date = new Date(startDate); date <= endDate; date.setDate(date.getDate() + 1)) {
      const dayEntrainements = this.getEntrainementsForDate(new Date(date));
      
      currentWeek.push({
        date: new Date(date),
        isCurrentMonth: date.getMonth() === month,
        isToday: this.isToday(date),
        entrainements: dayEntrainements
      });
      
      if (currentWeek.length === 7) {
        this.calendarWeeks.push({ days: [...currentWeek] });
        currentWeek.length = 0;
      }
    }
  }

  private generateWeekView(): void {
    const startOfWeek = new Date(this.currentDate);
    startOfWeek.setDate(startOfWeek.getDate() - (startOfWeek.getDay() === 0 ? 6 : startOfWeek.getDay() - 1));
    
    this.currentWeekDays = [];
    for (let i = 0; i < 7; i++) {
      const date = new Date(startOfWeek);
      date.setDate(date.getDate() + i);
      
      this.currentWeekDays.push({
        date: new Date(date),
        isCurrentMonth: true,
        isToday: this.isToday(date),
        entrainements: this.getEntrainementsForDate(date)
      });
    }
  }

  private generateListView(): void {
    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();
    
    const monthEntrainements = this.filteredEntrainements.filter(entrainement => {
      const entrainementDate = new Date(entrainement.date);
      return entrainementDate.getFullYear() === year && entrainementDate.getMonth() === month;
    });
    
    // Grouper par date
    const grouped = monthEntrainements.reduce((acc, entrainement) => {
      const dateKey = entrainement.date;
      if (!acc[dateKey]) {
        acc[dateKey] = [];
      }
      acc[dateKey].push(entrainement);
      return acc;
    }, {} as { [key: string]: Entrainement[] });
    
    // Convertir en tableau et trier
    this.groupedEntrainements = Object.keys(grouped)
      .sort()
      .map(date => ({
        date: new Date(date).toLocaleDateString('fr-FR', {
          weekday: 'long',
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        }),
        entrainements: grouped[date].sort((a, b) => a.heureDebut.localeCompare(b.heureDebut))
      }));
  }

  private getEntrainementsForDate(date: Date): Entrainement[] {
    const dateString = date.toISOString().split('T')[0];
    return this.filteredEntrainements.filter(entrainement => 
      entrainement.date === dateString
    );
  }

  getEntrainementsForHour(entrainements: Entrainement[], hour: number): Entrainement[] {
    return entrainements.filter(entrainement => {
      const startHour = parseInt(entrainement.heureDebut.split(':')[0]);
      return startHour === hour;
    });
  }

  private isToday(date: Date): boolean {
    const today = new Date();
    return date.toDateString() === today.toDateString();
  }

  // Navigation
  previousMonth(): void {
    this.currentDate.setMonth(this.currentDate.getMonth() - 1);
    this.generateCalendar();
  }

  nextMonth(): void {
    this.currentDate.setMonth(this.currentDate.getMonth() + 1);
    this.generateCalendar();
  }

  goToToday(): void {
    this.currentDate = new Date();
    this.generateCalendar();
  }

  getCurrentMonthLabel(): string {
    return this.currentDate.toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long'
    });
  }

  getDayLabel(date: Date): string {
    return date.toLocaleDateString('fr-FR', { weekday: 'short' });
  }

  // Méthodes utilitaires
  getTypeLabel(type: TypeEntrainement): string {
    return TypeEntrainementLabels[type];
  }

  getStatutLabel(statut: StatutEntrainement): string {
    return StatutEntrainementLabels[statut];
  }

  getTypeColor(type: TypeEntrainement): string {
    return TypeEntrainementColors[type];
  }

  getStatutColor(statut: StatutEntrainement): string {
    return StatutEntrainementColors[statut];
  }

  getTypeColorByValue(type: string): string {
    return TypeEntrainementColors[type as TypeEntrainement];
  }

  formatDuree(entrainement: Entrainement): string {
    return this.entrainementService.formatDuree(entrainement.heureDebut, entrainement.heureFin);
  }
}
