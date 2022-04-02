import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  Input,
  OnDestroy,
  OnInit,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { NbDialogService, NbToastrService } from '@nebular/theme';
import { AngularXTimelineDataSource } from 'angularx-timeline';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { RestaurantsService } from '../../_services/restaurants/restaurants.service';
import { Assignment } from '../../_types/Assignment';
import { PriceRange } from '../../_types/PriceRange';
import { Restaurant } from '../../_types/Restaurant';

@Component({
  selector: 'ngx-restaurant-profile',
  templateUrl: './restaurant-profile.component.html',
  styleUrls: ['./restaurant-profile.component.scss'],
})
export class RestaurantProfileComponent implements OnInit, OnDestroy {
  @ViewChild('dialog') codeDialog: TemplateRef<any>;

  @Input() restaurant: Restaurant;

  dataSource: AngularXTimelineDataSource = [];

  priceRange: PriceRange;

  favorite: boolean;

  assignment: Assignment;

  keys = ['meatMeal', 'fishMeal', 'dietMeal', 'vegetarianMeal', 'desertMeal'];

  loadingIntention = false;

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly restaurantsService: RestaurantsService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly toastService: NbToastrService,
    private readonly dialogService: NbDialogService,
  ) {}

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }

  ngOnInit(): void {
    const data = [];

    if (this.restaurant.morningOpeningSchedule) {
      data.push({
        date: this.restaurant.morningOpeningSchedule,
        title: '',
        content: 'Morning schedule',
      });
    }

    if (this.restaurant.morningClosingSchedule) {
      data.push({
        date: this.restaurant.morningClosingSchedule,
        title: '',
      });
    }

    if (this.restaurant.afternoonOpeningSchedule) {
      data.push({
        date: this.restaurant.afternoonOpeningSchedule,
        title: '',
        content: 'Afternoon schedule',
      });
    }

    if (this.restaurant.afternoonClosingSchedule) {
      data.push({
        date: this.restaurant.afternoonClosingSchedule,
        title: '',
      });
    }

    data.forEach((entry) => this.dataSource.push(entry));

    this.getAssignment();
    this.getPriceRange();
    this.getFavorite();
  }

  getPriceRange() {
    const priceSubscriber = this.restaurantsService
      .getPriceRange(this.restaurant.id)
      .pipe(
        tap((res) => {
          this.priceRange = res;
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

    this.subscriptions.push(priceSubscriber);
  }

  getFavorite() {
    const favoriteSubscriber = this.restaurantsService
      .getFavorite(this.restaurant.id)
      .pipe(
        tap((res) => {
          this.favorite = res.favorite;
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

    this.subscriptions.push(favoriteSubscriber);
  }

  handleFavoriteClick() {
    if (this.favorite) {
      this.handleRemoveFavorite();
    } else {
      this.handleFavorite();
    }
  }

  handleFavorite() {
    const addFavoriteSubscriber = this.restaurantsService
      .addFavorite(this.restaurant.id)
      .pipe(
        finalize(() => {
          this.favorite = true;
          this.showToast('Restaurant was added to favorites', 'Success');
          this.cdr.markForCheck();
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(addFavoriteSubscriber);
  }

  handleRemoveFavorite() {
    const deleteFavoriteSubscriber = this.restaurantsService
      .deleteFavorite(this.restaurant.id)
      .pipe(
        finalize(() => {
          this.favorite = false;
          this.showToast('Restaurant was removed from favorites', 'Success');
          this.cdr.markForCheck();
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(deleteFavoriteSubscriber);
  }

  getAssignment() {
    this.loadingIntention = true;
    const assignmentSubscriber = this.restaurantsService
      .getNAssignments(this.restaurant.id, 5)
      .pipe(
        tap((res) => {
          this.assignment = res
            .filter((cur) => cur.available === true)
            .sort((a, b) => Number(new Date(a.date)) - Number(new Date(b.date)))
            .map((cur) => {
              for (const key of this.keys) {
                if (cur.menu[key])
                  cur.menu[key].intent = false;
              }

              return { ...cur };
            })[0];

          for (const key of this.keys) {
            if (this.assignment.menu[key])
              this.assignment.menu[key].disable = false;
          }

          this.getIntentions();
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

  getIntentions() {
    const intentionSubscriber = this.restaurantsService
      .getIntentions()
      .pipe(
        tap((res) => {
          for (const cur of res) {
            if (cur.assignment.id === this.assignment.id) {
              this.keys.forEach((key) => {
                if (this.assignment.menu[key])
                  if (cur.meals[0].id === this.assignment.menu[key].id) {
                    this.assignment.menu[key].disable = true;
                  }
              });
            }
          }
        }),
        finalize(() => {
          this.loadingIntention = false;
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
}
