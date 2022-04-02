import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnInit,
} from '@angular/core';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { ClientService } from '../../_services/client/client.service';
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
    private readonly clientService: ClientService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
  ) {}

  ngOnInit(): void {
    this.getSummary();
  }

  getSummary() {
    this.loading = true;
    const clientSubscribe = this.clientService
      .getSummary()
      .pipe(
        tap((res) => {
          this.chartPanelSummary.push({
            title: 'Intentions Given',
            value: res.intentionsGiven.toString(),
          });
          this.chartPanelSummary.push({
            title: 'Favorite Restaurants',
            value: res.numberOfFavoritRestaurants.toString(),
          });
          this.chartPanelSummary.push({
            title: 'Review Made',
            value: res.numberOfReviews.toString(),
          });
          this.chartPanelSummary.push({
            title: 'Money Saved',
            value: res.moneySaved.toFixed(2).toString() + ' €',
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

    this.subscriptions.push(clientSubscribe);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }
}
