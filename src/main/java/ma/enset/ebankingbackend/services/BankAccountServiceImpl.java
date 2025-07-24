package ma.enset.ebankingbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankingbackend.dtos.*;
import ma.enset.ebankingbackend.entities.*;
import ma.enset.ebankingbackend.enums.AccountStatus;
import ma.enset.ebankingbackend.enums.OperationType;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficientException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.mappers.BankAccountMapperImpl;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import ma.enset.ebankingbackend.repositories.CustomerRepository;
import ma.enset.ebankingbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private BankAccountRepository bankAccountRepository;
    private CustomerRepository customerRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl bankAccountMapper;
    private UserRepository userRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        User user = getAuthenticatedUser();

        Customer customer = bankAccountMapper.fromCustomerDTOToCustomer(customerDTO);
        customer.setCreatedByUserId(user.getId());
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Saving new customer");
        return bankAccountMapper.fromCustomerToCustomerDTO(savedCustomer);
    }

    @Override
    public CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, String customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        User user = getAuthenticatedUser();

        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setDateCreated(new Date());
        currentAccount.setStatus(AccountStatus.PENDING);
        currentAccount.setCustomerId(customer.getId());
        currentAccount.setOverdraftLimit(overDraft);
        currentAccount.setCreatedByUserId(user.getId());

        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentAccount);
        return bankAccountMapper.fromCurrentAccountToCurrentAccountDTO(savedCurrentAccount);
    }

    @Override
    public SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, String customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        User user = getAuthenticatedUser();

        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setDateCreated(new Date());
        savingAccount.setStatus(AccountStatus.PENDING);
        savingAccount.setCustomerId(customer.getId());
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCreatedByUserId(user.getId());

        SavingAccount savedSavingAccount = bankAccountRepository.save(savingAccount);
        return bankAccountMapper.fromSavingAccountToSavingAccountDTO(savedSavingAccount);
    }

    @Override
    public List<BankAccountDTO> getAccountsByCustomerId(String customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        List<BankAccount> accounts = bankAccountRepository.findByCustomerId(customer.getId());
        List<BankAccountDTO> dtos = accounts.stream().map(account -> {
            if (account instanceof SavingAccount) {
                return bankAccountMapper.fromSavingAccountToSavingAccountDTO((SavingAccount) account);
            } else {
                return bankAccountMapper.fromCurrentAccountToCurrentAccountDTO((CurrentAccount) account);
            }
        }).collect(Collectors.toList());

        return dtos;
    }


    @Override
    public List<CustomerDTO> getCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream()
                .map(cust -> bankAccountMapper.fromCustomerToCustomerDTO(cust))
                .collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccountById(String id) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found"));
        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return bankAccountMapper.fromSavingAccountToSavingAccountDTO(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return bankAccountMapper.fromCurrentAccountToCurrentAccountDTO(currentAccount);
        }
    }

    @Override
    public void debit(String id, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        double overDraft = 0;
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found"));
        if (bankAccount instanceof CurrentAccount) {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            overDraft = currentAccount.getOverdraftLimit();
        }

        if (bankAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        if (bankAccount.getBalance() + overDraft < amount) {
            throw new BalanceNotSufficientException("Balance Not Sufficient");
        }
        User user = getAuthenticatedUser();

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setBankAccountId(bankAccount.getId());
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setOperationDate(new Date());
        accountOperation.setPerformedByUserId(user.getId());
        accountOperation.setCancelled(false);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String id, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found"));
        if (bankAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }

        User user = getAuthenticatedUser();
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setBankAccountId(bankAccount.getId());
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setOperationDate(new Date());
        accountOperation.setPerformedByUserId(user.getId());
        accountOperation.setCancelled(false);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String fromId, String toId, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(fromId, amount, "Transfer to " + toId);
        credit(toId, amount, "Transfer from " + fromId);
    }

    @Override
    public List<BankAccountDTO> getAllBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> listAccountDTO = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return bankAccountMapper.fromSavingAccountToSavingAccountDTO(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return bankAccountMapper.fromCurrentAccountToCurrentAccountDTO(currentAccount);
            }
        }).collect(Collectors.toList());
        return listAccountDTO;
    }

    @Override
    public CustomerDTO getCustomerById(String id) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));
        return bankAccountMapper.fromCustomerToCustomerDTO(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        Customer customer = bankAccountMapper.fromCustomerDTOToCustomer(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Updating customer");
        return bankAccountMapper.fromCustomerToCustomerDTO(savedCustomer);
    }

    @Override
    public void deleteCustomer(String id) throws CustomerNotFoundException {
        customerRepository.deleteById(id);
    }

    @Override
    public List<AccountOperationDTO> getHistory(String id) {
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(id);
        return accountOperations.stream()
                .map(op -> bankAccountMapper.fromAccountToAccountOperationDTO(op))
                .collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException("Account not found");
        }

        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "operationDate")));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(
                        op -> bankAccountMapper.fromAccountToAccountOperationDTO(op))
                .collect(Collectors.toList());
        accountHistoryDTO.setOperations(accountOperationDTOS);
        accountHistoryDTO.setAccountId(accountId);
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        accountHistoryDTO.setStatus(bankAccount.getStatus());
        if (bankAccount instanceof SavingAccount savingAccount) {
            accountHistoryDTO.setType("Saving");
            accountHistoryDTO.setInterestRate(savingAccount.getInterestRate());
        }
        if (bankAccount instanceof CurrentAccount currentAccount) {
            accountHistoryDTO.setType("Current");
            accountHistoryDTO.setOverdraftLimit(currentAccount.getOverdraftLimit());
        }
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomer(String keyword) {
        List<Customer> customers;

        if (keyword == null || keyword.trim().isEmpty()) {
            customers = customerRepository.findAll();
        } else {
            customers = customerRepository.findByNameContainingIgnoreCase(keyword);
        }

        return customers.stream()
                .map(bankAccountMapper::fromCustomerToCustomerDTO)
                .collect(Collectors.toList());
    }


    @Override
    public void updateAccountStatus(String id, AccountStatus status) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));
        bankAccount.setStatus(status);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void cancelOperation(String id) throws BankAccountNotFoundException {
        AccountOperation op = accountOperationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operation not found"));

        if (op.isCancelled()) throw new RuntimeException("Already cancelled");

        String accountId = op.getBankAccountId();
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));

        if (op.getType() == OperationType.DEBIT) {
            account.setBalance(account.getBalance() + op.getAmount());
        } else if (op.getType() == OperationType.CREDIT) {
            if (account.getBalance() < op.getAmount())
                throw new RuntimeException("Insufficient balance to reverse credit");

            account.setBalance(account.getBalance() - op.getAmount());
        }

        op.setCancelled(true);
        accountOperationRepository.save(op);
        bankAccountRepository.save(account);
    }

    @Override
    public AccountHistoryDTO searchOperations(String accountId, LocalDate startDate, LocalDate endDate,
                                              Double minAmount, Double maxAmount, int page, int size) throws BankAccountNotFoundException {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));

        Timestamp start = (startDate != null)
                ? Timestamp.valueOf(startDate.atStartOfDay())
                : new Timestamp(0);

        Timestamp end = (endDate != null)
                ? Timestamp.valueOf(endDate.plusDays(1).atStartOfDay().minusNanos(1))
                : new Timestamp(System.currentTimeMillis());

        double min = (minAmount != null) ? minAmount : 0.0;
        double max = (maxAmount != null) ? maxAmount : Double.MAX_VALUE;

        Page<AccountOperation> operationsPage = accountOperationRepository.searchOperations(
                accountId, start, end, min, max,
                PageRequest.of(page, size, Sort.by("operationDate").descending())
        );

        AccountHistoryDTO dto = new AccountHistoryDTO();
        dto.setAccountId(account.getId());
        dto.setBalance(account.getBalance());
        dto.setCurrentPage(page);
        dto.setPageSize(size);
        dto.setTotalPages(operationsPage.getTotalPages());
        dto.setStatus(account.getStatus());
        dto.setOperations(operationsPage.getContent().stream()
                .map(bankAccountMapper::fromAccountToAccountOperationDTO)
                .toList());
        if (account instanceof SavingAccount savingAccount) {
            dto.setType("Saving");
            dto.setInterestRate(savingAccount.getInterestRate());
        }
        if (account instanceof CurrentAccount currentAccount) {
            dto.setType("Current");
            dto.setOverdraftLimit(currentAccount.getOverdraftLimit());
        }
        return dto;
    }


    @Override
    public BankAccountDTO getLatestAccount() {
        BankAccount account = bankAccountRepository.findTopByOrderByDateCreatedDesc()
                .orElseThrow(() -> new RuntimeException("No accounts found"));
        if (account instanceof SavingAccount) {
            return bankAccountMapper.fromSavingAccountToSavingAccountDTO((SavingAccount) account);
        } else {
            return bankAccountMapper.fromCurrentAccountToCurrentAccountDTO((CurrentAccount) account);
        }
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    @Override
    public void updateInterestRate(String accountId, double newRate) throws BankAccountNotFoundException {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));

        if (!(account instanceof SavingAccount)) {
            throw new IllegalArgumentException("Only SavingAccount supports interest rate");
        }

        ((SavingAccount) account).setInterestRate(newRate);
        bankAccountRepository.save(account);
    }

    @Override
    public void updateOverdraftLimit(String accountId, double newLimit) throws BankAccountNotFoundException {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));

        if (!(account instanceof CurrentAccount)) {
            throw new IllegalArgumentException("Only CurrentAccount supports overdraft limit");
        }
        ((CurrentAccount) account).setOverdraftLimit(newLimit);
        bankAccountRepository.save(account);
    }


}
