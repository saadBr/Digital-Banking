package ma.enset.ebankingbackend.repositories;

import ma.enset.ebankingbackend.entities.AccountOperation;
import ma.enset.ebankingbackend.enums.OperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface AccountOperationRepository extends MongoRepository<AccountOperation, String> {
    List<AccountOperation> findByBankAccountId(String id);

    Page<AccountOperation> findByBankAccountId(String id, Pageable pageable);

    long countByType(OperationType type);


    @Aggregation(pipeline = {
            "{ $group: { _id: '$bankAccountId', count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }",
            "{ $limit: 5 }"
    })
    List<Map<String, Object>> countOperationsGroupedByBankAccountId();

    @Query("""
            {
              'bankAccountId': ?0,
              'operationDate': { $gte: ?1, $lte: ?2 },
              'amount': { $gte: ?3, $lte: ?4 }
            }
            """)
    Page<AccountOperation> searchOperations(
            String accountId,
            Timestamp start,
            Timestamp end,
            Double minAmount,
            Double maxAmount,
            Pageable pageable
    );

}
