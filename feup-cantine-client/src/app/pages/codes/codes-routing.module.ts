import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CodesComponent } from './codes.component';

const routes: Routes = [
  {
    path: '',
    component: CodesComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CodesRoutingModule {}
