import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectorRef,
  ErrorHandler,
} from '@angular/core';
import { Assignment } from '../../_types/Assignment';
import { Meal } from '../../_types/Meal';
import { of, Subscription } from 'rxjs';
import { ClientService } from '../../_services/client/client.service';
import { NbToastrService } from '@nebular/theme';
import { catchError, finalize, tap } from 'rxjs/operators';

@Component({
  selector: 'ngx-next-intention',
  templateUrl: './next-intention.component.html',
  styleUrls: ['./next-intention.component.scss'],
})
export class NextIntentionComponent implements OnInit, OnDestroy {
  loading = false;

  intention: {
    assignment: Assignment;
    code: string;
    id: string;
    meals: Meal[];
    validatedCode: boolean;
    restaurant: string;
  };

  keys = ['meatMeal', 'fishMeal', 'dietMeal', 'vegetarianMeal', 'desertMeal'];

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly clientService: ClientService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly toastService: NbToastrService,
  ) {}

  ngOnInit(): void {
    this.getCodes();
  }

  getCodes() {
    this.loading = true;
    const intentionSubscriber = this.clientService
      .getNextIntention()
      .pipe(
        tap((res) => {
          this.intention = res;
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
