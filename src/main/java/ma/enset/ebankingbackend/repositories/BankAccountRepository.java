package ma.enset.ebankingbackend.repositories;

import ma.enset.ebankingbackend.entities.BankAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends MongoRepository<BankAccount, String> {
    Optional<BankAccount> findTopByOrderByDateCreatedDesc();

    List<BankAccount> findByCustomerId(String customerId);
}
