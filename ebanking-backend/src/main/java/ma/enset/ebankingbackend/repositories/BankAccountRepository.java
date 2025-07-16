package ma.enset.ebankingbackend.repositories;

import ma.enset.ebankingbackend.entities.BankAccount;
import ma.enset.ebankingbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    @Query("SELECT COUNT(a) FROM CurrentAccount a")
    long countCurrentAccounts();

    @Query("SELECT COUNT(a) FROM SavingAccount a")
    long countSavingAccounts();

    Optional<BankAccount> findTopByOrderByDateCreatedDesc();
}
