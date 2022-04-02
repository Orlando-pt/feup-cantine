import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnInit,
} from '@angular/core';
import { of, Subscription } from 'rxjs';
import { RestaurantsService } from '../_services/restaurants/restaurants.service';
import { Assignment } from '../_types/Assignment';
import { OnDestroy } from '@angular/core';
import { catchError, finalize, tap } from 'rxjs/operators';
import { Meal } from '../_types/Meal';
import { NbToastrService } from '@nebular/theme';

@Component({
  selector: 'ngx-codes',
  templateUrl: './codes.component.html',
  styleUrls: ['./codes.component.scss'],
})
export class CodesComponent implements OnInit, OnDestroy {
  loadingCodes = false;

  codes: {
    assignment: Assignment;
    code: string;
    id: string;
    meals: Meal[];
    validatedCode: boolean;
    restaurant: string;
  }[];

  keys = ['meatMeal', 'fishMeal', 'dietMeal', 'vegetarianMeal', 'desertMeal'];

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly restaurantsService: RestaurantsService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly toastService: NbToastrService,
  ) {}

  ngOnInit(): void {
    this.getCodes();
  }

  getCodes() {
    this.loadingCodes = true;
    const intentionSubscriber = this.restaurantsService
      .getIntentions()
      .pipe(
        tap((res) => {
          this.codes = res.sort(
            (a, b) =>
              Number(new Date(a.assignment.date)) -
              Number(new Date(b.assignment.date)),
          );
        }),
        finalize(() => {
          this.loadingCodes = false;
          this.cdr.markForCheck();
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(intentionSubscriber);
  }

  copyCodeToClipboard(code: string) {
    this.showToast('Code was copied to your clipboard!', 'Success');

    navigator.clipboard.writeText(code).then(
      function () {},
      function (err) {
        this.showToast('Code was not copied to your clipboard!', 'Danger');
        console.error('Async: Could not copy text: ', err);
      },
    );
  }

  showToast(message: string, type: string) {
    this.toastService.show(message, type, {
      status: type.toLowerCase(),
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }
}
