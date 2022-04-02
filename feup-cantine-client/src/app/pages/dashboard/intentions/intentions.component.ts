import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnInit,
} from '@angular/core';
import { NbThemeService } from '@nebular/theme';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { ClientService } from '../../_services/client/client.service';
import { OnDestroy } from '@angular/core';

@Component({
  selector: 'ngx-intentions',
  templateUrl: './intentions.component.html',
  styleUrls: ['./intentions.component.scss'],
})
export class IntentionsComponent implements OnInit, OnDestroy {
  options: any = {};
  dynamicData: any = {};
  themeSubscription: any;

  loading = false;

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly clientService: ClientService,
    private readonly cdr: ChangeDetectorRef,
    private readonly theme: NbThemeService,
    private readonly handleError: ErrorHandler,
  ) {}

  ngOnInit(): void {
    this.themeSubscription = this.theme.getJsTheme().subscribe((config) => {
      this.getSummary();

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
          data: ['Intentions Fullfiled', 'Intentions Not Fullfiled'],
          textStyle: {
            color: echarts.textColor,
          },
        },
        series: [
          {
            name: 'Intentions',
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
    });
  }

  getSummary() {
    this.loading = true;
    const clientSubscribe = this.clientService
      .getSummary()
      .pipe(
        tap((res) => {
          const data = { ...this.options.series[0] };

          data.data.push({
            value: res.intentionsGiven - res.intentionsNotFulfilled,
            name: 'Intentions Fullfiled',
          });
          data.data.push({
            value: res.intentionsNotFulfilled,
            name: 'Intentions Not Fullfiled',
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

    this.subscriptions.push(clientSubscribe);
  }

  ngOnDestroy(): void {
    this.themeSubscription.unsubscribe();
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }
}
