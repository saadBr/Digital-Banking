package ma.enset.ebankingbackend.repositories;

import ma.enset.ebankingbackend.entities.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    List<ActionLog> findByPerformedByUsername(String username);
    List<ActionLog> findByAction(String action);
    List<ActionLog> findByPerformedByUsernameAndAction(String username, String action);
}
