import { Component, Input, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { Chart, ChartConfiguration, ChartType } from 'chart.js';

@Component({
  selector: 'app-chart',
  template: `
    <div class="chart-container">
      <canvas #chartCanvas></canvas>
    </div>
  `,
  styles: [`
    .chart-container {
      position: relative;
      height: 100%;
      width: 100%;
      
      canvas {
        max-height: 100%;
        max-width: 100%;
      }
    }
  `]
})
export class ChartComponent implements OnInit, OnDestroy {
  @ViewChild('chartCanvas', { static: true }) chartCanvas!: ElementRef<HTMLCanvasElement>;
  
  @Input() type: ChartType = 'line';
  @Input() data: any = null;
  @Input() options: ChartConfiguration['options'] = {};
  @Input() height: number = 300;
  
  private chart: Chart | null = null;
  
  ngOnInit(): void {
    this.createChart();
  }
  
  ngOnDestroy(): void {
    if (this.chart) {
      this.chart.destroy();
    }
  }
  
  private createChart(): void {
    if (!this.data) {
      return;
    }
    
    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (!ctx) {
      return;
    }
    
    // Configuration par défaut
    const defaultOptions: ChartConfiguration['options'] = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom'
        }
      }
    };
    
    // Fusion des options
    const mergedOptions = { ...defaultOptions, ...this.options };
    
    this.chart = new Chart(ctx, {
      type: this.type,
      data: this.data,
      options: mergedOptions
    });
  }
  
  updateChart(newData: any, newOptions?: ChartConfiguration['options']): void {
    if (!this.chart) {
      return;
    }
    
    this.chart.data = newData;
    
    if (newOptions) {
      this.chart.options = { ...this.chart.options, ...newOptions };
    }
    
    this.chart.update();
  }
  
  refreshChart(): void {
    if (this.chart) {
      this.chart.destroy();
    }
    this.createChart();
  }
}
