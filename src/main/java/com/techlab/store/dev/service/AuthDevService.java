package com.techlab.store.dev.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techlab.store.dto.RegisterRequest;
import com.techlab.store.entity.User;
import com.techlab.store.service.ClientService;
import com.techlab.store.service.UserService;

import lombok.RequiredArgsConstructor;


// TODO: eliminar AuthDevService.java
@Service
@RequiredArgsConstructor
public class AuthDevService {

    private final UserService userService;
    private final ClientService clientService;

    @Transactional
    public List<User> saveAll(List<RegisterRequest> listRequests) {
        List<User> savedUsers = new ArrayList<>();
        for (RegisterRequest request : listRequests) {
            User savedUser = userService.create(request);
            clientService.create(request, savedUser);
            savedUsers.add(savedUser);
        }
        return savedUsers;
    }

}