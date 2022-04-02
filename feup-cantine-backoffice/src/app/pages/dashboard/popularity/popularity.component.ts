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
  selector: 'ngx-popularity',
  templateUrl: './popularity.component.html',
  styleUrls: ['./popularity.component.scss'],
})
export class PopularityComponent implements OnInit, OnDestroy {
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
        color: [colors.primary],
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c}',
        },
        legend: {
          left: 'center',
          data: ['Popularity'],
          textStyle: {
            color: echarts.textColor,
          },
        },
        xAxis: [
          {
            type: 'category',
            data: [],
            axisTick: {
              alignWithLabel: true,
            },
            axisLine: {
              lineStyle: {
                color: echarts.axisLineColor,
              },
            },
            axisLabel: {
              textStyle: {
                color: echarts.textColor,
              },
            },
          },
        ],
        yAxis: {
          type: 'value',
          nameLocation: 'middle',
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        series: [
          {
            name: 'Reviews',
            type: 'line',
            data: [],
          },
        ],
      };
      this.getPopularityInfo();
    });
  }

  getPopularityInfo() {
    this.loading = true;
    const restaurantSubscribe = this.restaurantService
      .getPopularity(1, new Date('2022-01-01'), new Date())
      .pipe(
        tap((res) => {
          const data = {
            name: 'Reviews',
            type: 'line',
            data: [],
          };
          for (const key in res) {
            if (res.hasOwnProperty(key)) {
              this.options.xAxis[0].data.push(
                new Date(key).toLocaleString('pt-PT').split(',')[0].toString(),
              );
              data.data.push(res[key]);
            }
          }

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
