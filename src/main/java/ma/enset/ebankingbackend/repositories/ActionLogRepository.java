package ma.enset.ebankingbackend.repositories;

import ma.enset.ebankingbackend.entities.ActionLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActionLogRepository extends MongoRepository<ActionLog, String> {
    List<ActionLog> findByPerformedByUserIdOrderByTimestampDesc(String userId);

    List<ActionLog> findByActionOrderByTimestampDesc(String action);

    List<ActionLog> findByPerformedByUserIdAndActionOrderByTimestampDesc(String userId, String action);

    List<ActionLog> findAllByOrderByTimestampDesc();
}
