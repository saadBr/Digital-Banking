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
        User user = userRepository.findByUsername(username).orElseThrow();
        ActionLog log = new ActionLog();
        log.setTimestamp(new Date());
        log.setAction(action);
        log.setDescription(description);
        log.setPerformedBy(user);
        actionLogRepository.save(log);
    }

    @Override
    public List<ActionLog> searchLogs(String username, String action) {
        if (username != null && action != null) {
            return actionLogRepository.findByPerformedByUsernameAndAction(username, action);
        } else if (username != null) {
            return actionLogRepository.findByPerformedByUsername(username);
        } else if (action != null) {
            return actionLogRepository.findByAction(action);
        } else {
            return actionLogRepository.findAll();
        }
    }
}
