import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CodesComponent } from './codes.component';
import { CodesRoutingModule } from './codes-routing.module';
import {
  NbCardModule,
  NbToastrModule,
  NbIconModule,
  NbTooltipModule,
} from '@nebular/theme';

@NgModule({
  declarations: [CodesComponent],
  imports: [
    CommonModule,
    CodesRoutingModule,
    NbCardModule,
    NbToastrModule,
    NbIconModule,
    NbTooltipModule,
  ],
})
export class CodesModule {}
