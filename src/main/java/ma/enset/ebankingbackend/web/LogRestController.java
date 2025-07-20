package ma.enset.ebankingbackend.web;

import lombok.RequiredArgsConstructor;
import ma.enset.ebankingbackend.dtos.ActionLogDTO;
import ma.enset.ebankingbackend.entities.ActionLog;
import ma.enset.ebankingbackend.entities.User;
import ma.enset.ebankingbackend.repositories.UserRepository;
import ma.enset.ebankingbackend.services.ActionLogService;
import ma.enset.ebankingbackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogRestController {
    private final ActionLogService logService;
    private final UserRepository userRepository;
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ActionLogDTO>> searchLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action
    ) {
        List<ActionLog> logs = logService.searchLogs(username, action);
        List<ActionLogDTO> dtos = logs.stream().map(log -> {
            ActionLogDTO dto = new ActionLogDTO();
            dto.setId(log.getId());
            dto.setTimestamp(log.getTimestamp());
            dto.setAction(log.getAction());
            dto.setDescription(log.getDescription());
            String userId = log.getPerformedByUserId();
            String usernameResult = userId != null
                    ? userRepository.findById(userId).map(User::getUsername).orElse("Unknown")
                    : "N/A";
            dto.setPerformedByUserId(usernameResult);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }


}
