import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValidateCodesComponent } from './validate-codes.component';

describe('ValidateCodesComponent', () => {
  let component: ValidateCodesComponent;
  let fixture: ComponentFixture<ValidateCodesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ValidateCodesComponent ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ValidateCodesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
