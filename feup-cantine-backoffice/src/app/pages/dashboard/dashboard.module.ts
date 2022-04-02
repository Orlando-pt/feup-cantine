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
  NbSpinnerModule,
} from '@nebular/theme';
import { NgxEchartsModule } from 'ngx-echarts';

import { ThemeModule } from '../../@theme/theme.module';
import { DashboardComponent } from './dashboard.component';
import { FormsModule } from '@angular/forms';
import { PopularityComponent } from './popularity/popularity.component';
import { ChartSummaryComponent } from './chart-summary/chart-summary.component';
import { SummaryComponent } from './summary/summary.component';
import { ReviewsComponent } from './reviews/reviews.component';
import { FoodCreatedComponent } from './food-created/food-created.component';
import { ChartModule } from 'angular2-chartjs';
import { IntentionsComponent } from './intentions/intentions.component';
import { DailyIntentionsComponent } from './daily-intentions/daily-intentions.component';

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
    NbSpinnerModule,
  ],
  declarations: [
    DashboardComponent,
    PopularityComponent,
    ChartSummaryComponent,
    SummaryComponent,
    ReviewsComponent,
    FoodCreatedComponent,
    IntentionsComponent,
    DailyIntentionsComponent,
  ],
})
export class DashboardModule {}
