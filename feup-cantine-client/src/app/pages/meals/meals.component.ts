import { Component, OnInit } from '@angular/core';
import { of } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { RestaurantsService } from '../_services/restaurants/restaurants.service';

@Component({
  selector: 'ngx-meals',
  templateUrl: './meals.component.html',
  styleUrls: ['./meals.component.scss'],
})
export class MealsComponent implements OnInit {
  fakeRestaurants = [
    {
      name: 'Restaurante 1',
    },
    {
      name: 'Restaurante 2',
    },
    {
      name: 'Restaurante 3',
    },
    {
      name: 'Restaurante 4',
    },
    {
      name: 'Restaurante 5',
    },
    {
      name: 'Restaurante 6',
    },
  ];

  constructor(private readonly restaurantsService: RestaurantsService) {}

  ngOnInit(): void {
    this.getAllRestaurants();
  }

  getAllRestaurants() {
    this.restaurantsService
      .getAll()
      .pipe(
        tap((res) => {}),
        finalize(() => {
          // this.cdr.markForCheck();
        }),
        catchError((err) => {
          // this.handleError.handleError(err);
          return of(null);
        }),
      )
      .subscribe();
  }
}
