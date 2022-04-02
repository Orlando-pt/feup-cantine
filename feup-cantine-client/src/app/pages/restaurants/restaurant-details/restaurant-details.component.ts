import {
  ChangeDetectorRef,
  Component,
  ErrorHandler,
  OnInit,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NbMenuItem, NbMenuService } from '@nebular/theme';
import { Restaurant } from '../../_types/Restaurant';
import { RestaurantsService } from '../../_services/restaurants/restaurants.service';
import { catchError, finalize, tap } from 'rxjs/operators';
import { of, Subscription } from 'rxjs';
import { OnDestroy } from '@angular/core';

@Component({
  selector: 'ngx-restaurant-details',
  templateUrl: './restaurant-details.component.html',
  styleUrls: ['./restaurant-details.component.scss'],
})
export class RestaurantDetailsComponent implements OnInit, OnDestroy {
  idRestaurant: string;

  restaurant: Restaurant;

  currentPage = 'profile';

  items: NbMenuItem[] = [
    {
      title: 'Profile',
      icon: 'person-outline',
    },
    {
      title: 'Menu',
      icon: 'book-open-outline',
    },
    {
      title: 'Reviews',
      icon: 'star-outline',
    },
  ];

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly activatedRoute: ActivatedRoute,
    private readonly restaurantsService: RestaurantsService,
    private readonly cdr: ChangeDetectorRef,
    private readonly handleError: ErrorHandler,
    private readonly menu: NbMenuService,
  ) {
    this.idRestaurant = this.activatedRoute.snapshot.params.id;

    menu.onItemClick().subscribe((cur) => {
      this.currentPage = cur.item.title.toLowerCase();
    });
  }

  ngOnInit(): void {
    this.getRestaurant();
  }

  getRestaurant() {
    const restaurantSubscriber = this.restaurantsService
      .getOne(this.idRestaurant)
      .pipe(
        tap((res) => {
          this.restaurant = res;
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

    this.subscriptions.push(restaurantSubscriber);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sb) => sb.unsubscribe());
  }
}
