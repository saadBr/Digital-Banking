package ma.enset.ebankingbackend.services;

import ma.enset.ebankingbackend.dtos.CustomerDTO;
import ma.enset.ebankingbackend.entities.BankAccount;
import ma.enset.ebankingbackend.entities.CurrentAccount;
import ma.enset.ebankingbackend.entities.Customer;
import ma.enset.ebankingbackend.entities.SavingAccount;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficientException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    Customer saveCustomer(Customer customer);
    CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> getCustomers();
    BankAccount getBankAccountById(String id) throws BankAccountNotFoundException;
    void debit(String id, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String id, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String fromId, String toId, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<BankAccount> getAllBankAccounts();

    CustomerDTO getCustomerById(long id) throws CustomerNotFoundException;
}
