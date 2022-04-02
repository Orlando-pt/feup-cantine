import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { NbToastrService } from '@nebular/theme';
import { LocalDataSource } from 'ng2-smart-table';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { MealsService } from '../_services/meals/meals.service';
import { Meal } from '../_types/Meal';

@Component({
  selector: 'ngx-meals',
  templateUrl: './meals.component.html',
  styleUrls: ['./meals.component.scss'],
})
export class MealsComponent implements OnInit, OnDestroy {
  settings = {
    add: {
      addButtonContent: '<i class="nb-plus"></i>',
      createButtonContent: '<i class="nb-checkmark"></i>',
      cancelButtonContent: '<i class="nb-close"></i>',
      confirmCreate: true,
    },
    edit: {
      editButtonContent: '<i class="nb-edit"></i>',
      saveButtonContent: '<i class="nb-checkmark"></i>',
      cancelButtonContent: '<i class="nb-close"></i>',
      confirmSave: true,
    },
    delete: {
      deleteButtonContent: '<i class="nb-trash"></i>',
      confirmDelete: true,
    },
    columns: {
      mealType: {
        title: 'Type',
        type: 'string',
        editor: {
          type: 'list',
          config: {
            list: [
              {
                value: 'MEAT',
                title: 'Meat',
              },
              {
                value: 'FISH',
                title: 'Fish',
              },
              {
                value: 'DIET',
                title: 'Diet',
              },
              {
                value: 'VEGETARIAN',
                title: 'Vegetarian',
              },
              {
                value: 'DESERT',
                title: 'Desert',
              },
            ],
          },
        },
      },
      description: {
        title: 'Description',
        type: 'string',
      },
      nutritionalInformation: {
        title: 'Nutricional Information',
        type: 'string',
      },
    },
  };

  source: LocalDataSource = new LocalDataSource();

  private subscriptions: Subscription[] = [];

  data: Meal[] = [];

  constructor(
    private readonly mealsService: MealsService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly toastService: NbToastrService,
  ) {}

  ngOnInit() {
    this.getMeals();
  }

  getMeals() {
    const mealsSubscribe = this.mealsService
      .getMeals()
      .pipe(
        tap((res) => {
          this.data = res;
          this.source.load(this.data);
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

    this.subscriptions.push(mealsSubscribe);
  }

  onCreateConfirm(event): void {
    if (window.confirm('Are you sure you want to add?')) {
      const mealsSubscribe = this.mealsService
        .addMeal(event.newData)
        .pipe(
          tap((res) => {
            event.newData = res;
            this.successToast('Meal added with success');
          }),
          finalize(() => {
            this.cdr.markForCheck();
            event.confirm.resolve(event.newData);
          }),
          catchError((err) => {
            this.handleError.handleError(err);
            event.confirm.reject();
            return of(null);
          }),
        )
        .subscribe();

      this.subscriptions.push(mealsSubscribe);
    } else {
      event.confirm.reject();
    }
  }

  onSaveConfirm(event): void {
    const id = event.newData.id;
    delete event.newData.id;
    const mealsSubscribe = this.mealsService
      .updateMeal(id, event.newData)
      .pipe(
        tap((res) => {
          event.newData = res;
          this.successToast('Meal updated with success');
        }),
        finalize(() => {
          this.cdr.markForCheck();
          event.confirm.resolve(event.newData);
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          event.confirm.reject();
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(mealsSubscribe);
  }

  onDeleteConfirm(event): void {
    if (window.confirm('Are you sure you want to delete?')) {
      const mealsSubscribe = this.mealsService
        .deleteMeal(event.data.id)
        .pipe(
          tap((res) => {
            this.successToast('Meal deleted with success');
          }),
          finalize(() => {
            this.cdr.markForCheck();
            event.confirm.resolve();
          }),
          catchError((err) => {
            this.handleError.handleError(err);
            event.confirm.reject();
            return of(null);
          }),
        )
        .subscribe();

      this.subscriptions.push(mealsSubscribe);
    } else {
      event.confirm.reject();
    }
  }

  private successToast(message: string) {
    this.toastService.show(message, 'Success', { status: 'success' });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }
}
