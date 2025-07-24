package ma.enset.ebankingbackend.services;

import lombok.RequiredArgsConstructor;
import ma.enset.ebankingbackend.entities.ActionLog;
import ma.enset.ebankingbackend.entities.User;
import ma.enset.ebankingbackend.repositories.ActionLogRepository;
import ma.enset.ebankingbackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionLogServiceImpl implements ActionLogService {
    private final ActionLogRepository actionLogRepository;
    private final UserRepository userRepository;

    @Override
    public void log(String username, String action, String description) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        ActionLog log = new ActionLog();
        log.setTimestamp(new Date());
        log.setAction(action);
        log.setDescription(description);
        log.setPerformedByUserId(user.getId());
        actionLogRepository.save(log);
    }

    @Override
    public List<ActionLog> searchLogs(String username, String action) {
        String userId = null;
        if (username != null) {
            userId = userRepository.findByUsername(username)
                    .map(User::getId).orElse(null);
            if (userId == null) return List.of();
        }
        if (userId != null && action != null) {
            return actionLogRepository.findByPerformedByUserIdAndActionOrderByTimestampDesc(userId, action);
        } else if (userId != null) {
            return actionLogRepository.findByPerformedByUserIdOrderByTimestampDesc(userId);
        } else if (action != null) {
            return actionLogRepository.findByActionOrderByTimestampDesc(action);
        } else {
            return actionLogRepository.findAllByOrderByTimestampDesc();
        }
    }

}
