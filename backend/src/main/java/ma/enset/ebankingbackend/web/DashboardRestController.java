package ma.enset.ebankingbackend.web;

import lombok.RequiredArgsConstructor;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.services.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DashboardRestController {
    private final DashboardService dashboardService;
    private final AccountOperationRepository accountOperationRepository;

    @GetMapping("/dashboard")
    public Map<String, Object> getStats() {
        return dashboardService.getDashboardStats();
    }

    @GetMapping("/dashboard/operationsByType")
    public Map<String, Long> getOperationsByType() {
        return dashboardService.getOperationsByType();
    }

    @GetMapping("/dashboard/most-active-customers")
    public Map<String, Long> getMostActiveCustomers() {
        return dashboardService.getMostActiveCustomers();
    }

}
