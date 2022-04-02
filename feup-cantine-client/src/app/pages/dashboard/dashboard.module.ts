import { NgModule } from '@angular/core';
import {
  NbActionsModule,
  NbButtonModule,
  NbCardModule,
  NbTabsetModule,
  NbUserModule,
  NbRadioModule,
  NbSelectModule,
  NbListModule,
  NbIconModule,
  NbTooltipModule,
  NbSpinnerModule,
} from '@nebular/theme';
import { NgxEchartsModule } from 'ngx-echarts';

import { ThemeModule } from '../../@theme/theme.module';
import { DashboardComponent } from './dashboard.component';
import { FormsModule } from '@angular/forms';
import { SummaryComponent } from './summary/summary.component';
import { ChartModule } from 'angular2-chartjs';
import { ChartSummaryComponent } from './chart-summary/chart-summary.component';
import { IntentionsComponent } from './intentions/intentions.component';
import { NextIntentionComponent } from './next-intention/next-intention.component';

@NgModule({
  imports: [
    FormsModule,
    ThemeModule,
    NbCardModule,
    NbUserModule,
    NbButtonModule,
    NbTabsetModule,
    NbActionsModule,
    NbRadioModule,
    NbSelectModule,
    NbListModule,
    NbIconModule,
    NbButtonModule,
    NgxEchartsModule,
    ChartModule,
    NbTooltipModule,
    NbSpinnerModule,
  ],
  declarations: [
    DashboardComponent,
    ChartSummaryComponent,
    SummaryComponent,
    IntentionsComponent,
    NextIntentionComponent,
  ],
})
export class DashboardModule {}
