import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  NbCardModule,
  NbIconModule,
  NbInputModule,
  NbTreeGridModule,
} from '@nebular/theme';
import { ThemeModule } from '../../@theme/theme.module';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { TablesRoutingModule } from '../tables/tables-routing.module';
import { AssignmentsComponent } from './assignments.component';
import { MenuRenderComponent } from './menu-render.component';
import { DateRenderComponent } from './date-render.component';
import { FormsModule } from '@angular/forms';
import { DateEditorComponent } from './date-editor.component';

@NgModule({
  imports: [
    NbCardModule,
    NbTreeGridModule,
    NbIconModule,
    NbInputModule,
    FormsModule,
    // CommonModule,
    ThemeModule,
    TablesRoutingModule,
    Ng2SmartTableModule,
  ],
  declarations: [AssignmentsComponent, MenuRenderComponent, DateRenderComponent, DateEditorComponent],
})
export class AssignmentsModule {}
