import { Component, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { DefaultEditor } from 'ng2-smart-table';

@Component({
  template: `
    <input [ngClass]="inputClass"
        #date
        nbInput
        fullWidth
        [name]="cell.getId()"
        [disabled]="!cell.isEditable()"
        [placeholder]="cell.getTitle()"
        (keyup)="updateValue()"
        (keydown.enter)="updateValue()"
        (keydown.esc)="updateValue()"
        (change)="updateValue()"
        type="date"
    />
  `,
})
export class DateEditorComponent extends DefaultEditor implements AfterViewInit {

  @ViewChild('date') date: ElementRef;

  constructor() {
    super();
  }
    ngAfterViewInit(): void {
        if (this.cell.newValue !== '')
            this.date.nativeElement.value = this.cell.newValue;
    }

    updateValue() {
        this.cell.newValue = this.date.nativeElement.value;
    }
}
