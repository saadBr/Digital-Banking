import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangePassword } from './change-password';

describe('ChangePassword', () => {
  let component: ChangePassword;
  let fixture: ComponentFixture<ChangePassword>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangePassword]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangePassword);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
