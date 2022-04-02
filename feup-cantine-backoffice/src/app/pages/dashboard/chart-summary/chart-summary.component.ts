import { Component, Input } from '@angular/core';

@Component({
  selector: 'ngx-chart-summary',
  styleUrls: ['./chart-summary.component.scss'],
  template: `
    <div class="summary-container">
      <div *ngFor="let item of summary">
        <div>{{ item.title }}</div>
        <div class="h6">{{ item.value }}</div>
      </div>
    </div>
  `,
})
export class ChartSummaryComponent {
  @Input() summary: { title: string; value: number }[];
}
