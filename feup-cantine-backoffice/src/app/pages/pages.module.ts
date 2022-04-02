import { NgModule } from '@angular/core';
import { NbMenuModule } from '@nebular/theme';

import { ThemeModule } from '../@theme/theme.module';
import { PagesComponent } from './pages.component';
import { DashboardModule } from './dashboard/dashboard.module';
import { PagesRoutingModule } from './pages-routing.module';
import { MiscellaneousModule } from './miscellaneous/miscellaneous.module';
import { MealsModule } from './meals/meals.module';
import { ProfileModule } from './profile/profile.module';
import { MenusModule } from './menus/menus.module';
import { AssignmentsModule } from './assignments/assignments.module';
import { ValidateCodesModule } from './validate-codes/validate-codes.module';
import { ReviewsComponent } from './reviews/reviews.component';
import { ReviewsModule } from './reviews/reviews.module';

@NgModule({
  imports: [
    PagesRoutingModule,
    ThemeModule,
    NbMenuModule,
    DashboardModule,
    MiscellaneousModule,
    MealsModule,
    ProfileModule,
    MenusModule,
    AssignmentsModule,
    ReviewsModule,
    ValidateCodesModule,
  ],
  declarations: [PagesComponent],
})
export class PagesModule {}
