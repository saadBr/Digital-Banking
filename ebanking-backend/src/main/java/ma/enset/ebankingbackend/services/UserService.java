package ma.enset.ebankingbackend.services;

import ma.enset.ebankingbackend.dtos.CreateUserRequest;
import ma.enset.ebankingbackend.entities.User;

public interface UserService {
    User createUser(CreateUserRequest request);
}
