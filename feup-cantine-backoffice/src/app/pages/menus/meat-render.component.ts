import { Component, Input, OnInit } from '@angular/core';

import { ViewCell } from 'ng2-smart-table';
import { Menu } from '../_types/Menu';

@Component({
  template: `
    {{renderValue}}
  `,
})
export class MeatRenderComponent implements ViewCell, OnInit {

  renderValue: string;

  @Input() value: string | number;
  @Input() rowData: Menu;

  ngOnInit() {
    if (this.rowData.meatMeal) this.value = this.rowData.meatMeal.id;
    if (this.rowData.meatMeal) this.renderValue = this.rowData.meatMeal.description;
  }

}
