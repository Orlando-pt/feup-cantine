import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NextIntentionComponent } from './next-intention.component';

describe('NextIntentionComponent', () => {
  let component: NextIntentionComponent;
  let fixture: ComponentFixture<NextIntentionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NextIntentionComponent ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NextIntentionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
