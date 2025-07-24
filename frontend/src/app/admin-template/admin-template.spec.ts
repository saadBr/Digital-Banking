import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTemplate } from './admin-template';

describe('AdminTemplate', () => {
  let component: AdminTemplate;
  let fixture: ComponentFixture<AdminTemplate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTemplate],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminTemplate);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
