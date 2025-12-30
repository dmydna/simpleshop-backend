package com.techlab.store.service;

import com.techlab.store.entity.User;
import com.techlab.store.repository.UserRepository;
import com.techlab.store.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
public class UserService {
    private final UserRepository UserRepository;
    private final StringUtils stringUtils;

    public UserService(UserRepository userRepository, StringUtils stringUtils) {
        this.UserRepository = userRepository;
        this.stringUtils = stringUtils;
    }

    public User createUser(User user){
        log.info("User ingresado: {}", user);
        return this.UserRepository.save(user);
    }

    public User getUserById(Long id){
        Optional<User> userOptional = this.UserRepository.findById(id);

        if (userOptional.isEmpty()){
            throw new RuntimeException("User no encontrado con ID: " + id);
        }

        return userOptional.get();
    }

}
