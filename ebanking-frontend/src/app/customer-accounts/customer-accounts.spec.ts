import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerAccounts } from './customer-accounts';

describe('CustomerAccounts', () => {
  let component: CustomerAccounts;
  let fixture: ComponentFixture<CustomerAccounts>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerAccounts],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomerAccounts);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
