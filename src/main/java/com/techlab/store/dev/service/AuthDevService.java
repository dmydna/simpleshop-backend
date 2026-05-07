package com.techlab.store.dev.service;

import com.techlab.store.dto.AuthResponse;
import com.techlab.store.dto.LoginRequest;
import com.techlab.store.entity.User;
import com.techlab.store.dto.RegisterRequest;
import com.techlab.store.entity.User;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.techlab.store.service.ClientService;
import com.techlab.store.service.JwtService;
import com.techlab.store.service.UserService;


@Service
@RequiredArgsConstructor
public class AuthDevService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
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