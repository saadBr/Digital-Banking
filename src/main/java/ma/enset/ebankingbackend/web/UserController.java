package ma.enset.ebankingbackend.web;

import lombok.RequiredArgsConstructor;
import ma.enset.ebankingbackend.dtos.CreateUserRequest;
import ma.enset.ebankingbackend.dtos.PasswordChangeRequest;
import ma.enset.ebankingbackend.dtos.PasswordResetRequest;
import ma.enset.ebankingbackend.entities.User;
import ma.enset.ebankingbackend.repositories.UserRepository;
import ma.enset.ebankingbackend.services.ActionLogServiceImpl;
import ma.enset.ebankingbackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ActionLogServiceImpl actionLogServiceImpl;

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public String changePassword(@RequestBody PasswordChangeRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username).orElseThrow();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return "Old password is incorrect!";
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        actionLogServiceImpl.log(username, "CHANGE_PASSWORD", "User changed own password");
        return "Password updated successfully!";
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public String resetPassword(@RequestBody PasswordResetRequest request, Principal principal) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        actionLogServiceImpl.log(principal.getName(), "RESET_PASSWORD", "Reset password for user: " + request.getUsername());
        return "Password reset successfully for user: " + request.getUsername();
    }

    @PostMapping("/create-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request, Principal principal) {
        User created = userService.createUser(request);
        actionLogServiceImpl.log(principal.getName(), "CREATE_USER", "Created user with username: " + created.getUsername());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id, Principal principal) {
        return userRepository.findById(id).map(user -> {
            userRepository.deleteById(id);
            actionLogServiceImpl.log(principal.getName(), "DELETE_USER", "Deleted user: " + user.getUsername());
            return ResponseEntity.ok("User deleted");
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody CreateUserRequest request, Principal principal) {
        User user = userRepository.findById(id).orElseThrow();

        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setRoles(request.getRoles());
        userRepository.save(user);

        actionLogServiceImpl.log(principal.getName(), "UPDATE_USER", "Updated user: " + user.getUsername());
        return ResponseEntity.ok(user);
    }


}