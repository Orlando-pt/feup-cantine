import { Component, Input, OnInit } from '@angular/core';

import { ViewCell } from 'ng2-smart-table';
import { Menu } from '../_types/Menu';

@Component({
  template: `
    {{renderValue}}
  `,
})
export class DietRenderComponent implements ViewCell, OnInit {

  renderValue: string;

  @Input() value: string | number;
  @Input() rowData: Menu;

  ngOnInit() {
    if (this.rowData.dietMeal) this.value = this.rowData.dietMeal.id;
    if (this.rowData.dietMeal) this.renderValue = this.rowData.dietMeal.description;
  }

}
