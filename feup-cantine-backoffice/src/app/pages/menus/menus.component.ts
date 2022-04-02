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
import { MenuService } from '../_services/menu/menu.service';
import { Meal } from '../_types/Meal';
import { Menu } from '../_types/Menu';
import { DesertRenderComponent } from './desert-render.component';
import { DietRenderComponent } from './diet-render.component';
import { FishRenderComponent } from './fish-render.component';
import { MeatRenderComponent } from './meat-render.component';
import { VegetarianRenderComponent } from './vegetarian-render.component';

@Component({
  selector: 'ngx-menus',
  templateUrl: './menus.component.html',
  styleUrls: ['./menus.component.scss'],
})
export class MenusComponent implements OnInit, OnDestroy {
  source: LocalDataSource = new LocalDataSource();

  private subscriptions: Subscription[] = [];

  data: Menu[] = [];

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
      name: {
        title: 'Name',
        type: 'text',
      },
      startPrice: {
        title: 'Start Price',
        type: 'text',
      },
      endPrice: {
        title: 'End Price',
        type: 'text',
      },
      meatMeal: {
        title: 'Meat Meal',
        type: 'custom',
        renderComponent: MeatRenderComponent,
        editor: {
          type: 'list',
          config: {
            list: [],
          },
        },
      },
      fishMeal: {
        title: 'Fish Meal',
        type: 'custom',
        renderComponent: FishRenderComponent,
        editor: {
          type: 'list',
          config: {
            list: [],
          },
        },
      },
      dietMeal: {
        title: 'Diet Meal',
        type: 'custom',
        renderComponent: DietRenderComponent,
        editor: {
          type: 'list',
          config: {
            list: [],
          },
        },
      },
      vegetarianMeal: {
        title: 'Vegetarian Meal',
        type: 'custom',
        renderComponent: VegetarianRenderComponent,
        editor: {
          type: 'list',
          config: {
            list: [],
          },
        },
      },
      desertMeal: {
        title: 'Desert',
        type: 'custom',
        renderComponent: DesertRenderComponent,
        editor: {
          type: 'list',
          config: {
            list: [],
          },
        },
      },
      additionalInformation: {
        title: 'Other Information',
        type: 'text',
      },
      discount: {
        title: 'Discount',
        type: 'number',
      },
    },
  };

  constructor(
    private readonly menusService: MenuService,
    private readonly mealsService: MealsService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly toastService: NbToastrService,
  ) {}

  ngOnInit(): void {
    this.getMenus();
    this.getMeals();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }

  getMenus() {
    const menusSubscribe = this.menusService
      .getMenus()
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
    this.subscriptions.push(menusSubscribe);
  }

  getMeals() {
    const mealsSubscribe = this.mealsService
      .getMeals()
      .pipe(
        tap((res) => {
          this.filterMeals(res);
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

  private filterMeals(meals: Meal[]) {
    meals.forEach((meal) =>
      this.settings.columns[
        `${meal.mealType.toLocaleLowerCase()}Meal`
      ].editor.config.list.push({
        value: meal.id,
        title: meal.description,
      }),
    );
    this.settings = Object.assign({}, this.settings);
  }

  onCreateConfirm(event): void {
    if (window.confirm('Are you sure you want to add?')) {
      const menusSubscribe = this.menusService
        .addMenu(event.newData)
        .pipe(
          tap((res) => {
            event.newData = res;
            this.successToast('Menu added successfully!');
          }),
          finalize(() => {
            this.cdr.markForCheck();
            event.confirm.resolve(event.newData);
          }),
          catchError((error) => {
            this.handleError.handleError(error);
            event.confirm.reject();
            this.errorToast('Failed to save menu.');
            return of(null);
          }),
        )
        .subscribe();
      this.subscriptions.push(menusSubscribe);
    } else {
      event.confirm.reject();
    }
  }

  onSaveConfirm(event): void {
    const id = event.newData.id;
    delete event.newData.id;

    if (event.newData.meatMeal && event.newData.meatMeal.id)
      event.newData.meatMeal = event.newData.meatMeal.id;
    if (event.newData.fishMeal && event.newData.fishMeal.id)
      event.newData.fishMeal = event.newData.fishMeal.id;
    if (event.newData.dietMeal && event.newData.dietMeal.id)
      event.newData.dietMeal = event.newData.dietMeal.id;
    if (event.newData.vegetarianMeal && event.newData.vegetarianMeal.id)
      event.newData.vegetarianMeal = event.newData.vegetarianMeal.id;
    if (event.newData.desertMeal && event.newData.desertMeal.id)
      event.newData.desertMeal = event.newData.desertMeal.id;

    const menusSubscribe = this.menusService
      .updateMenu(id, event.newData)
      .pipe(
        tap((res) => {
          event.newData = res;
          this.successToast('Menu updated successfully!');
        }),
        finalize(() => {
          this.cdr.markForCheck();
          event.confirm.resolve(event.newData);
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          event.confirm.reject();
          this.errorToast('Failed to update menu.');
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(menusSubscribe);
  }

  onDeleteConfirm(event): void {
    if (window.confirm('Are you sure you want to delete?')) {
      const menusSubscribe = this.menusService
        .deleteMenu(event.data.id)
        .pipe(
          tap((res) => {
            this.successToast('Menu deleted successfully!');
          }),
          finalize(() => {
            this.cdr.markForCheck();
            event.confirm.resolve();
          }),
          catchError((err) => {
            this.handleError.handleError(err);
            event.confirm.reject();
            this.errorToast('Failed to delete menu.');
            return of(null);
          }),
        )
        .subscribe();

      this.subscriptions.push(menusSubscribe);
    } else {
      event.confirm.reject();
    }
  }

  private successToast(message: string) {
    this.toastService.show(message, 'Success', { status: 'success' });
  }

  private errorToast(message: string) {
    this.toastService.show(message, 'Danger', { status: 'danger' });
  }
}
