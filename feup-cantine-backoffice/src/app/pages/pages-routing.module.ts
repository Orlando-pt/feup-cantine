import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

import { PagesComponent } from './pages.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { NotFoundComponent } from './miscellaneous/not-found/not-found.component';
import { MealsComponent } from './meals/meals.component';
import { MenusComponent } from './menus/menus.component';
import { AssignmentsComponent } from './assignments/assignments.component';
import { ValidateCodesComponent } from './validate-codes/validate-codes.component';
import { ReviewsComponent } from './reviews/reviews.component';

const routes: Routes = [
  {
    path: '',
    component: PagesComponent,
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent,
      },
      {
        path: 'meals',
        component: MealsComponent,
      },
      {
        path: 'menus',
        component: MenusComponent,
      },
      {
        path: 'assignments',
        component: AssignmentsComponent,
      },
      {
        path: 'reviews',
        component: ReviewsComponent,
      },
      {
        path: 'validate-codes',
        component: ValidateCodesComponent,
      },
      {
        path: 'tables',
        loadChildren: () =>
          import('./tables/tables.module').then((m) => m.TablesModule),
      },
      {
        path: 'miscellaneous',
        loadChildren: () =>
          import('./miscellaneous/miscellaneous.module').then(
            (m) => m.MiscellaneousModule,
          ),
      },
      {
        path: 'profile',
        loadChildren: () =>
          import('./profile/profile.module').then((m) => m.ProfileModule),
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: '**',
        component: NotFoundComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PagesRoutingModule {}
