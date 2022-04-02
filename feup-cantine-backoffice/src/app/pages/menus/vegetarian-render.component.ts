import { Component, Input, OnInit } from '@angular/core';

import { ViewCell } from 'ng2-smart-table';
import { Menu } from '../_types/Menu';

@Component({
  template: `
    {{renderValue}}
  `,
})
export class VegetarianRenderComponent implements ViewCell, OnInit {

  renderValue: string;

  @Input() value: string | number;
  @Input() rowData: Menu;

  ngOnInit() {
    if (this.rowData.vegetarianMeal) this.value = this.rowData.vegetarianMeal.id;
    if (this.rowData.vegetarianMeal) this.renderValue = this.rowData.vegetarianMeal.description;
  }

}
