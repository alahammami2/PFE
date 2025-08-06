import { Component, Input, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartConfiguration, ChartType, registerables } from 'chart.js';

// Enregistrer tous les composants Chart.js
Chart.register(...registerables);

export interface ChartData {
  labels: string[];
  datasets: {
    label: string;
    data: number[];
    backgroundColor?: string | string[];
    borderColor?: string | string[];
    borderWidth?: number;
    fill?: boolean;
    tension?: number;
  }[];
}

export interface ChartOptions {
  responsive?: boolean;
  maintainAspectRatio?: boolean;
  plugins?: {
    legend?: {
      display?: boolean;
      position?: 'top' | 'bottom' | 'left' | 'right';
    };
    title?: {
      display?: boolean;
      text?: string;
    };
  };
  scales?: {
    x?: {
      display?: boolean;
      title?: {
        display?: boolean;
        text?: string;
      };
    };
    y?: {
      display?: boolean;
      beginAtZero?: boolean;
      title?: {
        display?: boolean;
        text?: string;
      };
      max?: number;
      min?: number;
    };
  };
}

@Component({
  selector: 'app-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="chart-container" [style.height]="height">
      <canvas #chartCanvas></canvas>
    </div>
  `,
  styles: [`
    .chart-container {
      position: relative;
      width: 100%;
      height: 400px;
    }
    
    canvas {
      max-width: 100%;
      max-height: 100%;
    }
  `]
})
export class ChartComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('chartCanvas', { static: true }) chartCanvas!: ElementRef<HTMLCanvasElement>;
  
  @Input() type: ChartType = 'line';
  @Input() data: ChartData = { labels: [], datasets: [] };
  @Input() options: ChartOptions = {};
  @Input() height: string = '400px';
  @Input() width: string = '100%';

  private chart: Chart | null = null;

  ngOnInit(): void {
    // Configuration par défaut
    this.setDefaultOptions();
  }

  ngAfterViewInit(): void {
    this.createChart();
  }

  ngOnDestroy(): void {
    if (this.chart) {
      this.chart.destroy();
    }
  }

  private setDefaultOptions(): void {
    const defaultOptions: ChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'top'
        }
      },
      scales: {
        y: {
          beginAtZero: true
        }
      }
    };

    this.options = { ...defaultOptions, ...this.options };
  }

  private createChart(): void {
    if (this.chart) {
      this.chart.destroy();
    }

    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    const config: ChartConfiguration = {
      type: this.type,
      data: this.data,
      options: this.options
    };

    this.chart = new Chart(ctx, config);
  }

  public updateChart(newData: ChartData, newOptions?: ChartOptions): void {
    if (this.chart) {
      this.chart.data = newData;
      if (newOptions) {
        this.chart.options = { ...this.chart.options, ...newOptions };
      }
      this.chart.update();
    }
  }

  public refreshChart(): void {
    if (this.chart) {
      this.chart.update();
    }
  }
}

// Composant spécialisé pour les graphiques en ligne
@Component({
  selector: 'app-line-chart',
  standalone: true,
  imports: [ChartComponent],
  template: `
    <app-chart 
      type="line" 
      [data]="data" 
      [options]="options"
      [height]="height">
    </app-chart>
  `
})
export class LineChartComponent {
  @Input() data: ChartData = { labels: [], datasets: [] };
  @Input() options: ChartOptions = {};
  @Input() height: string = '400px';
}

// Composant spécialisé pour les graphiques en barres
@Component({
  selector: 'app-bar-chart',
  standalone: true,
  imports: [ChartComponent],
  template: `
    <app-chart 
      type="bar" 
      [data]="data" 
      [options]="options"
      [height]="height">
    </app-chart>
  `
})
export class BarChartComponent {
  @Input() data: ChartData = { labels: [], datasets: [] };
  @Input() options: ChartOptions = {};
  @Input() height: string = '400px';
}

// Composant spécialisé pour les graphiques en secteurs
@Component({
  selector: 'app-pie-chart',
  standalone: true,
  imports: [ChartComponent],
  template: `
    <app-chart 
      type="pie" 
      [data]="data" 
      [options]="options"
      [height]="height">
    </app-chart>
  `
})
export class PieChartComponent {
  @Input() data: ChartData = { labels: [], datasets: [] };
  @Input() options: ChartOptions = {};
  @Input() height: string = '400px';
}

// Composant spécialisé pour les graphiques en aires
@Component({
  selector: 'app-area-chart',
  standalone: true,
  imports: [ChartComponent],
  template: `
    <app-chart 
      type="line" 
      [data]="areaData" 
      [options]="options"
      [height]="height">
    </app-chart>
  `
})
export class AreaChartComponent implements OnInit {
  @Input() data: ChartData = { labels: [], datasets: [] };
  @Input() options: ChartOptions = {};
  @Input() height: string = '400px';

  areaData: ChartData = { labels: [], datasets: [] };

  ngOnInit(): void {
    // Convertir les données pour un graphique en aires
    this.areaData = {
      ...this.data,
      datasets: this.data.datasets.map(dataset => ({
        ...dataset,
        fill: true,
        tension: 0.4
      }))
    };
  }
}

// Composant spécialisé pour les graphiques radar
@Component({
  selector: 'app-radar-chart',
  standalone: true,
  imports: [ChartComponent],
  template: `
    <app-chart 
      type="radar" 
      [data]="data" 
      [options]="radarOptions"
      [height]="height">
    </app-chart>
  `
})
export class RadarChartComponent implements OnInit {
  @Input() data: ChartData = { labels: [], datasets: [] };
  @Input() options: ChartOptions = {};
  @Input() height: string = '400px';

  radarOptions: ChartOptions = {};

  ngOnInit(): void {
    this.radarOptions = {
      ...this.options,
      scales: {
        r: {
          beginAtZero: true,
          max: 5,
          ticks: {
            stepSize: 1
          }
        }
      }
    };
  }
}
