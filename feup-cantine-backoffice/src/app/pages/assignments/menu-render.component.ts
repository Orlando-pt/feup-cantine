import { Component, Input, OnInit } from '@angular/core';

import { ViewCell } from 'ng2-smart-table';
import { Assignment } from '../_types/Assignment';

@Component({
  template: `
    {{renderValue}}
  `,
})
export class MenuRenderComponent implements ViewCell, OnInit {

    // TODO date field
  renderValue: string;

  @Input() value: string | number;
  @Input() rowData: Assignment;

  ngOnInit() {
    if (this.rowData.menu) this.value = this.rowData.menu.id;
    if (this.rowData.menu) this.renderValue = this.rowData.menu.name;
  }

}
