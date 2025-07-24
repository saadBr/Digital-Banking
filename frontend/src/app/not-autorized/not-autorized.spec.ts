import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotAutorized } from './not-autorized';

describe('NotAutorized', () => {
  let component: NotAutorized;
  let fixture: ComponentFixture<NotAutorized>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotAutorized],
    }).compileComponents();

    fixture = TestBed.createComponent(NotAutorized);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
