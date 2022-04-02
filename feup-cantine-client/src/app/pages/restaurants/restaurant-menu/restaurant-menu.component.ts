import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  Input,
  OnInit,
  TemplateRef,
  ViewChild,
  OnDestroy,
} from '@angular/core';
import { Restaurant } from '../../_types/Restaurant';
import { of, Subscription } from 'rxjs';
import { RestaurantsService } from '../../_services/restaurants/restaurants.service';
import { catchError, finalize, tap } from 'rxjs/operators';
import { Assignment } from '../../_types/Assignment';
import { NbDialogService, NbToastrService } from '@nebular/theme';

@Component({
  selector: 'ngx-restaurant-menu',
  templateUrl: './restaurant-menu.component.html',
  styleUrls: ['./restaurant-menu.component.scss'],
})
export class RestaurantMenuComponent implements OnInit, OnDestroy {
  @ViewChild('dialog') codeDialog: TemplateRef<any>;

  @Input() restaurant: Restaurant;

  loadingAssignments = false;

  assignments: Assignment[];

  keys = ['meatMeal', 'fishMeal', 'dietMeal', 'vegetarianMeal', 'desertMeal'];

  selected: string;

  options: string[] | Date[];

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly restaurantsService: RestaurantsService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly dialogService: NbDialogService,
    private readonly toastService: NbToastrService,
  ) {}

  ngOnInit(): void {
    this.getAssignments();
  }

  getAssignments() {
    this.loadingAssignments = true;
    const assignmentSubscriber = this.restaurantsService
      .getNAssignments(this.restaurant.id, 5)
      .pipe(
        tap((res) => {
          this.assignments = res
            .sort((a, b) => Number(new Date(a.date)) - Number(new Date(b.date)))
            .map((cur) => {
              for (const key of this.keys) {
                if (cur.menu[key])
                  cur.menu[key].intent = false;
              }

              return { ...cur };
            });

          this.options = [...new Set(this.assignments.map((cur) => cur.date))];

          this.selected = this.options[0].toString();
        }),
        finalize(() => {
          this.loadingAssignments = false;
          this.cdr.markForCheck();
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(assignmentSubscriber);
  }

  setIntent(assignment) {
    const obj = {
      assignmentId: assignment.id,
      mealsId: [],
    };

    for (const key of this.keys) {
      if (assignment.menu[key] && assignment.menu[key].intent === true) {
        obj.mealsId.push(assignment.menu[key].id);
      }
    }

    if (obj.mealsId.length === 0) {
      this.showToast(
        'You have to select at least one meal to intent',
        'Danger',
      );
      return;
    }

    const intentionSubscriber = this.restaurantsService
      .addIntention(obj)
      .pipe(
        tap((res) => {
          if (res) {
            this.openCodeDialog(res.code);

            assignment.available = false;
            assignment.purchased === true;

            for (const key of this.keys) {
              if (assignment.menu[key])
                assignment.menu[key].choosen = assignment.menu[key].intent;
            }
          }
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

    this.subscriptions.push(intentionSubscriber);
  }

  openCodeDialog(code) {
    this.dialogService.open(this.codeDialog, {
      context: code,
    });
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
