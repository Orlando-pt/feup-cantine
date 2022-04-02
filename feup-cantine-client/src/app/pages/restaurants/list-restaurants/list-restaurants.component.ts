import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnInit,
} from '@angular/core';
import { of, Subscription } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { RestaurantsService } from '../../_services/restaurants/restaurants.service';
import { Restaurant } from '../../_types/Restaurant';
import { OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'ngx-list-restaurants',
  templateUrl: './list-restaurants.component.html',
  styleUrls: ['./list-restaurants.component.scss'],
})
export class ListRestaurantsComponent implements OnInit, OnDestroy {
  restaurants: Restaurant[] = [];

  favorites: Boolean;

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly restaurantsService: RestaurantsService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe((data) => {
      this.favorites = data.favorites;
      if (data.favorites === true) {
        this.getAllRestaurantsFavorite();
      } else {
        this.getAllRestaurants();
      }
    });
  }

  getAllRestaurants() {
    const restaurantsSubscriber = this.restaurantsService
      .getAll()
      .pipe(
        tap((res) => {
          this.restaurants = res;
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

    this.subscriptions.push(restaurantsSubscriber);
  }

  getAllRestaurantsFavorite() {
    const restaurantsSubscriber = this.restaurantsService
      .getFavorites()
      .pipe(
        tap((res) => {
          this.restaurants = res;
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

    this.subscriptions.push(restaurantsSubscriber);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }
}
