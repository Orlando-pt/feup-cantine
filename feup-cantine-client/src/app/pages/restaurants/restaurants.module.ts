import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListRestaurantsComponent } from './list-restaurants/list-restaurants.component';
import { RestaurantsRoutingModule } from './restaurants-routing.module';
import {
  NbButtonGroupModule,
  NbCardModule,
  NbDialogModule,
  NbInputModule,
  NbListModule,
  NbMenuModule,
  NbSelectModule,
  NbSpinnerModule,
  NbToastrModule,
  NbToggleModule,
  NbTooltipModule,
  NbUserModule,
} from '@nebular/theme';
import { RestaurantDetailsComponent } from './restaurant-details/restaurant-details.component';
import { RestaurantProfileComponent } from './restaurant-profile/restaurant-profile.component';
import { NbIconModule, NbButtonModule } from '@nebular/theme';
import { AngularXTimelineModule } from 'angularx-timeline';
import { RestaurantReviewsComponent } from './restaurant-reviews/restaurant-reviews.component';
import { FormsModule } from '@angular/forms';
import { RestaurantMenuComponent } from './restaurant-menu/restaurant-menu.component';

@NgModule({
  declarations: [
    ListRestaurantsComponent,
    RestaurantDetailsComponent,
    RestaurantProfileComponent,
    RestaurantReviewsComponent,
    RestaurantMenuComponent,
  ],
  imports: [
    CommonModule,
    RestaurantsRoutingModule,
    NbToastrModule,
    NbCardModule,
    NbMenuModule,
    NbUserModule,
    NbInputModule,
    NbIconModule,
    NbButtonModule,
    AngularXTimelineModule,
    FormsModule,
    NbButtonGroupModule,
    NbListModule,
    NbDialogModule.forChild(),
    NbSpinnerModule,
    NbSelectModule,
    NbToggleModule,
    NbTooltipModule,
  ],
})
export class RestaurantsModule {}
