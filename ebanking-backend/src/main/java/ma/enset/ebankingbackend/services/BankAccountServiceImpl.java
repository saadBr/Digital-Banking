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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private BankAccountRepository bankAccountRepository;
    private CustomerRepository customerRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl bankAccountMapper;
    private UserRepository userRepository;
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        Customer customer = bankAccountMapper.fromCustomerDTOToCustomer(customerDTO);
        customer.setCreatedBy(user);
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Saving new customer");
        return bankAccountMapper.fromCustomerToCustomerDTO(savedCustomer);
    }

    @Override
    public CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        CurrentAccount currentAccount = new CurrentAccount();

        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setDateCreated(new Date());
        currentAccount.setStatus(AccountStatus.ACTIVE);
        currentAccount.setCustomer(customer);
        currentAccount.setOverdraftLimit(overDraft);
        currentAccount.setCreatedBy(user);
        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentAccount);
        return bankAccountMapper.fromCurrentAccountToCurrentAccountDTO(savedCurrentAccount);
    }

    @Override
    public SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        SavingAccount savingAccount = new SavingAccount();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setDateCreated(new Date());
        savingAccount.setStatus(AccountStatus.PENDING);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCreatedBy(user);
        SavingAccount savedSavingAccount = bankAccountRepository.save(savingAccount);

        return bankAccountMapper.fromSavingAccountToSavingAccountDTO(savedSavingAccount);
    }


    @Override
    public List<CustomerDTO> getCustomers() {
        List<Customer> custoemrs = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = custoemrs.stream()
                .map(cust->bankAccountMapper.fromCustomerToCustomerDTO(cust))
                .collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccountById(String id) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(()-> new BankAccountNotFoundException("Bank Account Not Found"));
        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return bankAccountMapper.fromSavingAccountToSavingAccountDTO(savingAccount);
        }
        else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return bankAccountMapper.fromCurrentAccountToCurrentAccountDTO(currentAccount);
        }
    }

    @Override
    public void debit(String id, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(()-> new BankAccountNotFoundException("Bank Account Not Found"));
        if (bankAccount.getStatus() != AccountStatus.PENDING) {
            throw new IllegalStateException("Account is not active");
        }
        if(bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficientException("Balance Not Sufficient");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setOperationDate(new Date());
        accountOperation.setPerformedBy(user);
        accountOperation.setCancelled(false);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String id, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(()-> new BankAccountNotFoundException("Bank Account Not Found"));
        if (bankAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setOperationDate(new Date());
        accountOperation.setPerformedBy(user);
        accountOperation.setCancelled(false);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String fromId, String toId, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(fromId,amount,"Transfer to "+toId);
        credit(toId,amount,"Transfer from "+fromId);
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
    public CustomerDTO getCustomerById(long id) throws CustomerNotFoundException {
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
    public void deleteCustomer(long id) throws CustomerNotFoundException {
        customerRepository.deleteById(id);
    }
    @Override
    public List<AccountOperationDTO> getHistory(String id){
        List<AccountOperation> accountOperations = accountOperationRepository.findBybankAccountId(id);
        return accountOperations.stream()
                .map(op->bankAccountMapper.fromAccountToAccountOperationDTO(op))
                .collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount == null) {
            throw new BankAccountNotFoundException("Account not found");
        }

        Page<AccountOperation> accountOperations = accountOperationRepository.findBybankAccountId(accountId, PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"operationDate")));
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
        return accountHistoryDTO;
    }
    @Override
    public List<CustomerDTO> searchCustomer(String keyword) {
        List<Customer> customers = customerRepository.searchCustomer(keyword);
        List<CustomerDTO> customerDTOS =customers.stream().map(customer -> bankAccountMapper.fromCustomerToCustomerDTO(customer)).collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public List<BankAccountDTO> getAccountsByCustomerId(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return customer.getBankAccounts()
                .stream()
                .map(account -> {
                    if (account instanceof SavingAccount) {
                        return bankAccountMapper.fromSavingAccountToSavingAccountDTO((SavingAccount) account);
                    } else {
                        return bankAccountMapper.fromCurrentAccountToCurrentAccountDTO((CurrentAccount) account);
                    }
                })
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
    public void cancelOperation(Long id) throws BankAccountNotFoundException {
        AccountOperation op = accountOperationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operation not found"));

        if (op.isCancelled()) throw new RuntimeException("Already cancelled");

        BankAccount account = op.getBankAccount();

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
        Timestamp startTimestamp = null;
        Timestamp endTimestamp = null;

        if (startDate != null) {
            startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        }
        if (endDate != null) {
            endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay().minusNanos(1));
        }

        Page<AccountOperation> operationsPage = accountOperationRepository.searchOperations(
                accountId, startTimestamp, endTimestamp, minAmount, maxAmount,
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
                .map(operation -> bankAccountMapper.fromAccountToAccountOperationDTO(operation))
                .toList());

        return dto;

    }


}
