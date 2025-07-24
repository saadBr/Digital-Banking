package ma.enset.ebankingbackend.services;

import lombok.RequiredArgsConstructor;
import ma.enset.ebankingbackend.dtos.CreateUserRequest;
import ma.enset.ebankingbackend.entities.User;
import ma.enset.ebankingbackend.enums.Role;
import ma.enset.ebankingbackend.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        Set<Role> roles = request.getRoles();
        if (roles.contains(Role.ADMIN)) {
            roles.add(Role.USER);
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .enabled(true)
                .roles(roles)
                .build();
        System.out.println("Roles received: " + request.getRoles());
        return userRepository.save(user);
    }
}
