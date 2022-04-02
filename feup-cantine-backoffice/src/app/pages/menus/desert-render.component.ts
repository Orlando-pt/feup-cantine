import { Component, Input, OnInit } from '@angular/core';

import { ViewCell } from 'ng2-smart-table';
import { Menu } from '../_types/Menu';

@Component({
  template: `
    {{renderValue}}
  `,
})
export class DesertRenderComponent implements ViewCell, OnInit {

  renderValue: string;

  @Input() value: string | number;
  @Input() rowData: Menu;

  ngOnInit() {
    if (this.rowData.desertMeal) this.value = this.rowData.desertMeal.id;
    if (this.rowData.desertMeal) this.renderValue = this.rowData.desertMeal.description;
  }

}
