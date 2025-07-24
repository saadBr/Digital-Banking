package ma.enset.ebankingbackend.services;

import ma.enset.ebankingbackend.entities.ActionLog;

import java.util.List;

public interface ActionLogService {

    void log(String username, String action, String description);

    List<ActionLog> searchLogs(String username, String action);
}
