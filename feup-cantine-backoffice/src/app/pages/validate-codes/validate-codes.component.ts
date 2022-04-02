import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnInit,
} from '@angular/core';
import { NbToastrService } from '@nebular/theme';
import { RestaurantService } from '../_services/restaurant/restaurant.service';
import { OnDestroy } from '@angular/core';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { VerifyCode } from '../_types/VerifyCode';
import { Assignment } from '../_types/Assignment';

@Component({
  selector: 'ngx-validate-codes',
  templateUrl: './validate-codes.component.html',
  styleUrls: ['./validate-codes.component.scss'],
})
export class ValidateCodesComponent implements OnInit, OnDestroy {
  code: string = '';
  loading = false;

  verifiedUser: VerifyCode;

  assignment: Assignment;

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly restaurantService: RestaurantService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly toastService: NbToastrService,
  ) {}

  ngOnInit(): void {
    this.getCurrentAssignment();
  }

  getCurrentAssignment() {
    const assignmentSubscribe = this.restaurantService
      .getCurrentAssignment()
      .pipe(
        tap((res) => {
          this.assignment = res;
        }),
        finalize(() => {
          this.cdr.markForCheck();
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(assignmentSubscribe);
  }

  save() {
    if (this.code.length === 0 || this.code.length > 9) {
      return;
    }

    const validateCodeSubscribe = this.restaurantService
      .validateCode(this.code)
      .pipe(
        tap((res) => {
          if (res) {
            this.code = '';
            this.verifiedUser = res;

            this.toastService.show('Code is valid', 'Success', {
              status: 'success',
            });
          }
        }),
        finalize(() => {
          this.cdr.markForCheck();
        }),
        catchError((err) => {
          this.verifiedUser = null;

          this.toastService.show(err.error.message, 'Danger', {
            status: 'danger',
          });

          this.handleError.handleError(err);
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(validateCodeSubscribe);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }
}
