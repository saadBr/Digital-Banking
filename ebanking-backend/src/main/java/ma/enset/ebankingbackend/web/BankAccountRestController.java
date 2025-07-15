package ma.enset.ebankingbackend.web;

import lombok.AllArgsConstructor;
import ma.enset.ebankingbackend.dtos.*;
import ma.enset.ebankingbackend.enums.AccountStatus;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficientException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.services.BankAccountService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class BankAccountRestController {
    private BankAccountService bankAccountService;
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
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "size",defaultValue = "5")int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,size);
    }
    @PostMapping("accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.debit(debitDTO.getAccountId(),debitDTO.getAmount(),debitDTO.getDescription());
        return debitDTO;
    }
    @PostMapping("accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.credit(creditDTO.getAccountId(),creditDTO.getAmount(),creditDTO.getDescription());
        return creditDTO;
    }
    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferDTO transferDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.transfer(
                transferDTO.getAccountSource(),
                transferDTO.getAccountDestination(),
                transferDTO.getAmount());
    }

    @PostMapping("/accounts")
    public BankAccountDTO create(@RequestBody BankAccountRequestDTO bankAccountRequestDTO) throws BankAccountNotFoundException, CustomerNotFoundException {
        if("current".equalsIgnoreCase(bankAccountRequestDTO.getType())){

            return bankAccountService.saveCurrentBankAccount(
                bankAccountRequestDTO.getInitialBalance(),
                bankAccountRequestDTO.getOverdraft(),
                bankAccountRequestDTO.getCustomerId()
            );
        } else if ("saving".equalsIgnoreCase(bankAccountRequestDTO.getType())) {
            return bankAccountService.saveSavingBankAccount(
                    bankAccountRequestDTO.getInitialBalance(),
                    bankAccountRequestDTO.getInterestRate(),
                    bankAccountRequestDTO.getCustomerId()
            );
        }
        else {
            throw new IllegalArgumentException("Unsupported bank account type");
        }
    }
    @PatchMapping("/accounts/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) throws BankAccountNotFoundException {

        String status = payload.get("status");
        AccountStatus newStatus;
        try {
            newStatus = AccountStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value: " + status);
        }

        bankAccountService.updateAccountStatus(id, newStatus);
        return ResponseEntity.ok("Status updated to " + newStatus);
    }
    @PatchMapping("/operations/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cancelOperation(@PathVariable Long id) throws BankAccountNotFoundException {
        bankAccountService.cancelOperation(id);
        return ResponseEntity.ok("Operation cancelled");
    }

    @GetMapping("/accounts/{accountId}/operations/search")
    @PreAuthorize("hasRole('ADMIN')")
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





}
