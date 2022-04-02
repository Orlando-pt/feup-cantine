import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DailyIntentionsComponent } from './daily-intentions.component';

describe('DailyIntentionsComponent', () => {
  let component: DailyIntentionsComponent;
  let fixture: ComponentFixture<DailyIntentionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DailyIntentionsComponent ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DailyIntentionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
