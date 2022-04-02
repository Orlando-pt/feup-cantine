import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  Input,
  OnInit,
} from '@angular/core';
import { NbToastrService } from '@nebular/theme';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { RestaurantsService } from '../../_services/restaurants/restaurants.service';
import { Restaurant } from '../../_types/Restaurant';
import { OnDestroy } from '@angular/core';
import { Review } from '../../_types/Review';

@Component({
  selector: 'ngx-restaurant-reviews',
  templateUrl: './restaurant-reviews.component.html',
  styleUrls: ['./restaurant-reviews.component.scss'],
})
export class RestaurantReviewsComponent implements OnInit, OnDestroy {
  @Input() restaurant: Restaurant;

  comment: string;

  loading: boolean = false;

  singleSelectGroupValue = [];

  reviewDone: boolean = false;

  userReviews: Review[] = [];

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly restaurantsService: RestaurantsService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly toastService: NbToastrService,
  ) {}

  ngOnInit(): void {
    this.getUserReview();
  }

  getUserReview() {
    const reviewSubscriber = this.restaurantsService
      .getClientReviews(this.restaurant.id)
      .pipe(
        tap((res) => {
          this.userReviews = res;
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

    this.subscriptions.push(reviewSubscriber);
  }

  save() {
    if (this.singleSelectGroupValue.length === 0) {
      this.showToast('You have to select a classification', 'Danger');
      return;
    }

    this.loading = true;

    const saveChangesSubscribe = this.restaurantsService
      .saveReview(
        {
          classificationGrade: Number(this.singleSelectGroupValue),
          comment: this.comment,
        },
        Number(this.restaurant.id),
      )
      .pipe(
        tap((res) => {
          this.userReviews.push(res);
          this.reviewDone = true;

          this.comment = '';
          this.singleSelectGroupValue = [];

          this.showToast('Review added with success', 'Success');
        }),
        finalize(() => {
          this.loading = false;
          this.cdr.markForCheck();
        }),
        catchError((err) => {
          this.handleError.handleError(err);
          this.showToast(
            'Something went wrong while adding a review, try again',
            'Danger',
          );
          return of(null);
        }),
      )
      .subscribe();

    this.subscriptions.push(saveChangesSubscribe);
  }

  updateSingleSelectGroupValue(value): void {
    this.singleSelectGroupValue = value;
    this.cdr.markForCheck();
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
