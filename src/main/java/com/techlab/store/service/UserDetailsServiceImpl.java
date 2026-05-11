package com.techlab.store.service;

import com.techlab.store.entity.User;
import com.techlab.store.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.techlab.store.enums.UserStatus;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // HACK impide logear usuario con BANNED o DELETED 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos el usuario en la DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        if(user.getStatus().equals(UserStatus.BANNED)){
            throw new RuntimeException("Usuario baneado: " + username);
        }
        if(user.getStatus().equals(UserStatus.DELETED)){
            throw new RuntimeException("Usuario Eliminado: " + username);
        }
        // Convertimos tu entidad 'User' al objeto 'UserDetails' que Spring entiende
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().name()) // Importante: Spring busca el prefijo ROLE_
                .build();
    }
}
