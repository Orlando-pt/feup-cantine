import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FoodCreatedComponent } from './food-created.component';

describe('FoodCreatedComponent', () => {
  let component: FoodCreatedComponent;
  let fixture: ComponentFixture<FoodCreatedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FoodCreatedComponent ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FoodCreatedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
