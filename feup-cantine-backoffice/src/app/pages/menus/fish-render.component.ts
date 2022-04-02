import { Component, Input, OnInit } from '@angular/core';

import { ViewCell } from 'ng2-smart-table';
import { Menu } from '../_types/Menu';

@Component({
  template: `
    {{renderValue}}
  `,
})
export class FishRenderComponent implements ViewCell, OnInit {

  renderValue: string;

  @Input() value: string | number;
  @Input() rowData: Menu;

  ngOnInit() {
    if (this.rowData.fishMeal) this.value = this.rowData.fishMeal.id;
    if (this.rowData.fishMeal) this.renderValue = this.rowData.fishMeal.description;
  }

}
