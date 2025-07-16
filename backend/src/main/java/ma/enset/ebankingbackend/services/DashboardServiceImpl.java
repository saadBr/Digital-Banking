package ma.enset.ebankingbackend.services;

import lombok.RequiredArgsConstructor;
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
                .mapToDouble(account -> account.getBalance())
                .sum());
        stats.put("currentAccounts", bankAccountRepository.countCurrentAccounts());
        stats.put("savingAccounts", bankAccountRepository.countSavingAccounts());
        stats.put("operationCount", accountOperationRepository.count());
        return stats;
    }

    public Map<String, Long> getOperationsByType() {
        Map<String, Long> result = new HashMap<>();
        result.put("DEBIT", accountOperationRepository.countByType(OperationType.DEBIT));
        result.put("CREDIT", accountOperationRepository.countByType(OperationType.CREDIT));
        result.put("TRANSFER", accountOperationRepository.countTransfers());
        return result;
    }

    public Map<String, Long> getMostActiveCustomers() {
        List<Object[]> results = accountOperationRepository.countOperationsPerCustomer();
        Map<String, Long> response = new LinkedHashMap<>();
        for (Object[] row : results) {
            String customerName = (String) row[0];
            Long operationCount = (Long) row[1];
            response.put(customerName, operationCount);
        }
        return response;
    }
}
