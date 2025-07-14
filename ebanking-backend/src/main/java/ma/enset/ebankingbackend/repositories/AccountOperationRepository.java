package ma.enset.ebankingbackend.repositories;

import ma.enset.ebankingbackend.entities.AccountOperation;
import ma.enset.ebankingbackend.entities.Customer;
import ma.enset.ebankingbackend.enums.OperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    List<AccountOperation> findBybankAccountId(String id);
    Page<AccountOperation> findBybankAccountId(String id, Pageable pageable);
    long countByType(OperationType type);
    @Query("SELECT COUNT(op) FROM AccountOperation op WHERE op.description LIKE 'Transfer%'")
    long countTransfers();
    @Query("SELECT o.bankAccount.customer.name, COUNT(o) FROM AccountOperation o GROUP BY o.bankAccount.customer.name ORDER BY COUNT(o) DESC")
    List<Object[]> countOperationsPerCustomer();
}
