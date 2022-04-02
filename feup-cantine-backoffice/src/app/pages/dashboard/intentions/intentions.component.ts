import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnInit,
  OnDestroy,
} from '@angular/core';
import { NbColorHelper, NbThemeService, NbToastrService } from '@nebular/theme';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { RestaurantService } from '../../_services/restaurant/restaurant.service';

@Component({
  selector: 'ngx-intentions',
  templateUrl: './intentions.component.html',
  styleUrls: ['./intentions.component.scss'],
})
export class IntentionsComponent implements OnInit, OnDestroy {
  options: any = {};
  dynamicData: any = {};
  themeSubscription: any;
  data: any;

  loading = false;

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly restaurantService: RestaurantService,
    private readonly cdr: ChangeDetectorRef,
    private readonly theme: NbThemeService,
    private readonly handleError: ErrorHandler,
  ) {}

  ngOnInit(): void {
    this.themeSubscription = this.theme.getJsTheme().subscribe((config) => {
      const chartjs: any = config.variables.chartjs;

      this.data = {};

      this.options = {
        maintainAspectRatio: false,
        responsive: true,
        legend: {
          labels: {
            fontColor: chartjs.textColor,
          },
        },
        scales: {
          xAxes: [
            {
              gridLines: {
                display: false,
                color: chartjs.axisLineColor,
              },
              ticks: {
                fontColor: chartjs.textColor,
              },
            },
          ],
          yAxes: [
            {
              gridLines: {
                display: true,
                color: chartjs.axisLineColor,
              },
              ticks: {
                fontColor: chartjs.textColor,
              },
            },
          ],
        },
      };
      this.getIntentions();
    });
  }

  getIntentions() {
    this.loading = true;
    const restaurantSubscribe = this.restaurantService
      .getIntentions(1, new Date('2022-01-01'), new Date())
      .pipe(
        tap((res) => {
          const obj = {
            labels: [],
            intentionsGiven: [],
            intentionsFulfilled: [],
            intentionsNotFulfilled: [],
          };

          for (const key in res) {
            if (res.hasOwnProperty(key)) {
              if (
                res[key].intentionsGiven !== 0 ||
                res[key].intentionsFulfilled !== 0 ||
                res[key].intentionsNotFulfilled !== 0
              ) {
                obj.labels.push(
                  new Date(key).toLocaleString('pt-PT').split(',')[0].toString(),
                );

                obj.intentionsGiven.push(res[key].intentionsGiven);
                obj.intentionsFulfilled.push(res[key].intentionsFulfilled);
                obj.intentionsNotFulfilled.push(
                  res[key].intentionsNotFulfilled,
                );
              }
            }
          }

          this.data = {
            labels: obj.labels,
            datasets: [
              {
                data: obj.intentionsGiven,
                label: 'Intentions Given',
                backgroundColor: NbColorHelper.hexToRgbA('#00bbff', 0.8),
              },
              {
                data: obj.intentionsFulfilled,
                label: 'Intentions Fullfilled',
                backgroundColor: NbColorHelper.hexToRgbA('#ADD8E6', 0.8),
              },
              {
                data: obj.intentionsNotFulfilled,
                label: 'Intentions Not Fullfilled',
                backgroundColor: NbColorHelper.hexToRgbA('#8A2BE2', 0.8),
              },
            ],
          };
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
    this.themeSubscription.unsubscribe();

    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }
}
