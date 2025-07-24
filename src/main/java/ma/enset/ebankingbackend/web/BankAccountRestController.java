package ma.enset.ebankingbackend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankingbackend.dtos.*;
import ma.enset.ebankingbackend.enums.AccountStatus;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficientException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.services.ActionLogServiceImpl;
import ma.enset.ebankingbackend.services.BankAccountService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
public class BankAccountRestController {
    private BankAccountService bankAccountService;
    private ActionLogServiceImpl actionLogServiceImpl;

    @GetMapping("/account/{accountId}")
    public BankAccountDTO getBankAccountById(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccountById(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> getAllBankAccounts() {
        return bankAccountService.getAllBankAccounts();
    }

    @GetMapping("accounts/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getHistory(accountId);
    }

    @GetMapping("accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(
            @PathVariable String accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }

    @PostMapping("accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO, Principal principal) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
        actionLogServiceImpl.log(principal.getName(), "DEBIT", "Debited " + debitDTO.getAmount() + " from account " + debitDTO.getAccountId());
        return debitDTO;
    }

    @PostMapping("accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO, Principal principal) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.credit(creditDTO.getAccountId(), creditDTO.getAmount(), creditDTO.getDescription());
        actionLogServiceImpl.log(principal.getName(), "CREDIT", "Credited " + creditDTO.getAmount() + " to account " + creditDTO.getAccountId());
        return creditDTO;
    }

    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferDTO transferDTO, Principal principal) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.transfer(
                transferDTO.getAccountSource(),
                transferDTO.getAccountDestination(),
                transferDTO.getAmount());
        actionLogServiceImpl.log(principal.getName(), "TRANSFER", "Transferred " + transferDTO.getAmount() + " from account " + transferDTO.getAccountSource() + " to " + transferDTO.getAccountDestination());
    }

    @PostMapping("/accounts")
    public BankAccountDTO create(@RequestBody BankAccountRequestDTO bankAccountRequestDTO, Principal principal) throws BankAccountNotFoundException, CustomerNotFoundException {
        BankAccountDTO result;
        String id;
        if ("current".equalsIgnoreCase(bankAccountRequestDTO.getType())) {
            result = bankAccountService.saveCurrentBankAccount(
                    bankAccountRequestDTO.getInitialBalance(),
                    bankAccountRequestDTO.getOverdraft(),
                    bankAccountRequestDTO.getCustomerId()
            );
            id = ((CurrentAccountDTO) result).getId();
        } else if ("saving".equalsIgnoreCase(bankAccountRequestDTO.getType())) {
            result = bankAccountService.saveSavingBankAccount(
                    bankAccountRequestDTO.getInitialBalance(),
                    bankAccountRequestDTO.getInterestRate(),
                    bankAccountRequestDTO.getCustomerId()
            );
            id = ((SavingAccountDTO) result).getId();
        } else {
            throw new IllegalArgumentException("Unsupported bank account type");
        }
        actionLogServiceImpl.log(principal.getName(), "CREATE_ACCOUNT", "Created " + bankAccountRequestDTO.getType() + " account with ID " + id);
        return result;
    }

    @PatchMapping("/accounts/{id}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload,
            Principal principal) throws BankAccountNotFoundException {

        String status = payload.get("status");
        AccountStatus newStatus;
        try {
            newStatus = AccountStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value: " + status);
        }

        bankAccountService.updateAccountStatus(id, newStatus);
        actionLogServiceImpl.log(principal.getName(), "UPDATE_ACCOUNT_STATUS", "Updated status of account " + id + " to " + newStatus);
        return ResponseEntity.ok("Status updated to " + newStatus);
    }

    @PutMapping("/accounts/{id}/interest-rate")
    @PreAuthorize("hasRole('USER')")
    public void updateInterestRate(@PathVariable String id, @RequestBody double newRate) throws BankAccountNotFoundException {
        bankAccountService.updateInterestRate(id, newRate);
    }

    @PutMapping("/accounts/{id}/overdraft")
    @PreAuthorize("hasRole('USER')")
    public void updateOverdraft(@PathVariable String id, @RequestBody double newLimit) throws BankAccountNotFoundException {
        bankAccountService.updateOverdraftLimit(id, newLimit);
    }

    @PatchMapping("/operations/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cancelOperation(@PathVariable String id, Principal principal) throws BankAccountNotFoundException {
        bankAccountService.cancelOperation(id);
        actionLogServiceImpl.log(principal.getName(), "CANCEL_OPERATION", "Cancelled operation with ID " + id);
        return ResponseEntity.ok("Operation cancelled");
    }

    @GetMapping("/accounts/{accountId}/operations/search")
    @PreAuthorize("hasRole('USER')")
    public AccountHistoryDTO searchOperations(
            @PathVariable String accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) throws BankAccountNotFoundException {
        return bankAccountService.searchOperations(accountId, startDate, endDate, minAmount, maxAmount, page, size);
    }

    @GetMapping("/accounts/latest")
    public BankAccountDTO getLatestAccount() {
        return bankAccountService.getLatestAccount();
    }
}
