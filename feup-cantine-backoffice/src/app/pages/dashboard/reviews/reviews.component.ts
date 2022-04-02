import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnInit,
  OnDestroy,
} from '@angular/core';
import { NbThemeService } from '@nebular/theme';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { RestaurantService } from '../../_services/restaurant/restaurant.service';

@Component({
  selector: 'ngx-reviews',
  templateUrl: './reviews.component.html',
  styleUrls: ['./reviews.component.scss'],
})
export class ReviewsComponent implements OnInit, OnDestroy {
  options: any = {};
  dynamicData: any = {};
  themeSubscription: any;

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

      const colors: any = config.variables;
      const echarts: any = config.variables.echarts;

      this.options = {
        backgroundColor: echarts.bg,
        color: [
          colors.warningLight,
          colors.infoLight,
          colors.dangerLight,
          colors.successLight,
          colors.primaryLight,
        ],
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        legend: {
          orient: 'vertical',
          right: 'right',
          data: ['Reviews Received', 'Reviews Responded'],
          textStyle: {
            color: echarts.textColor,
          },
        },
        series: [
          {
            name: 'Reviews',
            type: 'pie',
            radius: '80%',
            center: ['50%', '50%'],
            data: [],
            itemStyle: {
              emphasis: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: echarts.itemHoverShadowColor,
              },
            },
            label: {
              normal: {
                textStyle: {
                  color: echarts.textColor,
                },
              },
            },
            labelLine: {
              normal: {
                lineStyle: {
                  color: echarts.axisLineColor,
                },
              },
            },
          },
        ],
      };

      this.getSummary();
    });
  }

  getSummary() {
    this.loading = true;
    const restaurantSubscribe = this.restaurantService
      .getSummary()
      .pipe(
        tap((res) => {
          const data = { ...this.options.series[0] };

          data.data.push({
            value: res.totalReviewsReceived,
            name: 'Reviews Received',
          });
          data.data.push({
            value: res.totalReviewsResponded,
            name: 'Reviews Responded',
          });

          this.dynamicData = this.options;
          this.dynamicData.series = [];
          // Applying my dynamic data here
          this.dynamicData.series.push(data);
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
