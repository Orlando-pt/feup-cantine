import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnInit,
} from '@angular/core';
import { NbToastrService } from '@nebular/theme';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { RestaurantService } from '../../_services/restaurant/restaurant.service';
import { OnDestroy } from '@angular/core';

@Component({
  selector: 'ngx-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.scss'],
})
export class SummaryComponent implements OnInit, OnDestroy {
  chartPanelSummary: {
    title: string;
    value: string;
  }[] = [];

  loading = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private readonly restaurantService: RestaurantService,
    private readonly cdr: ChangeDetectorRef,
    private readonly toastService: NbToastrService,
    private readonly handleError: ErrorHandler,
  ) {}

  ngOnInit(): void {
    this.getSummary();
  }

  getSummary() {
    this.loading = true;
    const restaurantSubscribe = this.restaurantService
      .getSummary()
      .pipe(
        tap((res) => {
          this.chartPanelSummary.push({
            title: 'Total Reviews received',
            value: res.totalReviewsReceived.toString(),
          });
          this.chartPanelSummary.push({
            title: 'Total Intents received',
            value: res.totalIntentsReceived.toString(),
          });
          this.chartPanelSummary.push({
            title: 'Total User Favorites',
            value: res.favorited.toString(),
          });
          this.chartPanelSummary.push({
            title: 'Money Offered',
            value: res.moneyOffered.toFixed(2).toString() + ' €',
          });
        }),
        finalize(() => {
          this.loading = false;
          this.cdr.markForCheck();
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(restaurantSubscribe);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }
}
