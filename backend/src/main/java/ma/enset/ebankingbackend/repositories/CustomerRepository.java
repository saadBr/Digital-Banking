package ma.enset.ebankingbackend.repositories;

import ma.enset.ebankingbackend.entities.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    List<Customer> findByNameContainingIgnoreCase(String keyword);
}
