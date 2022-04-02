import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthGuard } from '../../modules/guards/auth-guard.service';
import { ListRestaurantsComponent } from './list-restaurants/list-restaurants.component';
import { RestaurantDetailsComponent } from './restaurant-details/restaurant-details.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: ListRestaurantsComponent,
        canActivate: [AuthGuard],
        data: {
          favorites: false,
        },
      },
      {
        path: 'favorites',
        component: ListRestaurantsComponent,
        canActivate: [AuthGuard],
        data: {
          favorites: true,
        },
      },
      {
        path: ':id',
        component: RestaurantDetailsComponent,
        canActivate: [AuthGuard],
      },
    ]),
  ],
  exports: [RouterModule],
})
export class RestaurantsRoutingModule {}
