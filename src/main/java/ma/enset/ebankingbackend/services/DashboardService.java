package ma.enset.ebankingbackend.services;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardStats();

    Map<String, Long> getMostActiveCustomers();

    Map<String, Long> getOperationsByType();
}
