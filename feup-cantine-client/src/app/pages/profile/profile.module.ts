import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileComponent } from './profile.component';
import { ProfileRoutingModule } from './profile-routing.module';
import {
  NbButtonModule,
  NbCardModule,
  NbIconModule,
  NbInputModule,
  NbTimepickerModule,
  NbToastrModule,
  NbUserModule,
} from '@nebular/theme';
import { ThemeModule } from '../../@theme/theme.module';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [ProfileComponent],
  imports: [
    CommonModule,
    ProfileRoutingModule,
    NbCardModule,
    NbIconModule,
    NbInputModule,
    ThemeModule,
    NbUserModule,
    FormsModule,
    NbButtonModule,
    NbToastrModule,
    NbTimepickerModule,
  ],
})
export class ProfileModule {}
