import { Component, Input, OnInit } from '@angular/core';

import { ViewCell } from 'ng2-smart-table';
import { Assignment } from '../_types/Assignment';

@Component({
  template: `
    <input
        nbInput
        fullWidth
        readonly
        name="assignment-date"
        [(ngModel)]="renderValue"
        type="date"
    />
  `,
})
export class DateRenderComponent implements ViewCell, OnInit {

  renderValue: Date;

  @Input() value: string | number;
  @Input() rowData: Assignment;

  ngOnInit() {
    if (this.rowData.date) {
        this.renderValue = this.rowData.date;
        this.value = this.renderValue.toString();
    }
  }

}
