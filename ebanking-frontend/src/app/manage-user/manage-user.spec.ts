import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageUser } from './manage-user';

describe('ManageUser', () => {
  let component: ManageUser;
  let fixture: ComponentFixture<ManageUser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageUser],
    }).compileComponents();

    fixture = TestBed.createComponent(ManageUser);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
