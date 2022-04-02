import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ValidateCodesComponent } from './validate-codes.component';
import {
  NbButtonModule,
  NbCardModule,
  NbIconModule,
  NbInputModule,
  NbTagModule,
  NbToastrModule,
  NbUserModule,
} from '@nebular/theme';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [ValidateCodesComponent],
  imports: [
    CommonModule,
    NbCardModule,
    NbInputModule,
    NbIconModule,
    FormsModule,
    NbButtonModule,
    NbToastrModule,
    NbUserModule,
    NbTagModule,
  ],
})
export class ValidateCodesModule {}
