package ma.enset.ebankingbackend.services;

import lombok.RequiredArgsConstructor;
import ma.enset.ebankingbackend.entities.BankAccount;
import ma.enset.ebankingbackend.entities.CurrentAccount;
import ma.enset.ebankingbackend.entities.SavingAccount;
import ma.enset.ebankingbackend.enums.OperationType;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import ma.enset.ebankingbackend.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("customerCount", customerRepository.count());
        stats.put("accountCount", bankAccountRepository.count());
        stats.put("totalBalance", bankAccountRepository.findAll().stream()
                .mapToDouble(BankAccount::getBalance)
                .sum());
        stats.put("currentAccounts", countCurrentAccounts());
        stats.put("savingAccounts", countSavingAccounts());
        stats.put("operationCount", accountOperationRepository.count());
        return stats;
    }

    public Map<String, Long> getOperationsByType() {
        Map<String, Long> result = new HashMap<>();
        result.put("DEBIT", accountOperationRepository.countByType(OperationType.DEBIT));
        result.put("CREDIT", accountOperationRepository.countByType(OperationType.CREDIT));
        result.put("TRANSFER", accountOperationRepository.countByDescriptionRegex("TRANSFER"));
        return result;
    }

    public Map<String, Long> getMostActiveCustomers() {
        List<Map<String, Object>> raw = accountOperationRepository.countOperationsGroupedByUserId();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Map<String, Object> row : raw) {
            String userId = (String) row.get("_id");
            Long count = ((Number) row.get("count")).longValue();
            result.put(userId, count);
        }
        return result;
    }

    public long countCurrentAccounts() {
        return bankAccountRepository.findAll().stream()
                .filter(account -> account instanceof CurrentAccount)
                .count();
    }

    public long countSavingAccounts() {
        return bankAccountRepository.findAll().stream()
                .filter(account -> account instanceof SavingAccount)
                .count();
    }

}
