export interface BankAccountRequestDTO {
  initialBalance: number;
  overdraft?: number;
  interestRate?: number;
  customerId: string;
  type: 'current' | 'saving';
}
