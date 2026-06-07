package com.techlab.store.service;

import java.time.LocalDateTime;
import com.techlab.store.entity.User;
import com.techlab.store.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import com.techlab.store.dto.BanRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.techlab.store.enums.UserStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // CHECKME impedir logear usuario con BANNED o DELETED 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos el usuario en la DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("⚠️ Usuario no encontrado: " + username));
        
        log.info("🔔 Verificando usuario con id {} antes de logear...", user.getId());

        // Usuario esta baneado.
        if( user.getStatus().equals(UserStatus.BANNED) ){
            if(user.getBanExpiresAt() == null){
                throw new RuntimeException("⚠️ Usuario baneado permanentemente: " + username);
            }
            // FIXME: tengo que ver si la fecha es "menor o antes a la actual" caso contrario quita baneo
            if(user.getBanExpiresAt().isBefore(LocalDateTime.now())){
                throw new RuntimeException("⚠️ Usuario baneado temporalmente: " + username);
            }else{
                unbanUser(user);
            }
        }

        // Usuario esta eliminado.
        if(user.getStatus().equals(UserStatus.DELETED)){
            throw new RuntimeException("⚠️ Usuario Eliminado: " + username);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().name()) // Importante: Spring busca el prefijo ROLE_
                .build();
    }

    // HACK: no puedo usar userService (por problemas de dependencia circular)
    public User unbanUser(User user){
        if( user.getStatus().equals(UserStatus.BANNED) 
          && user.getBanExpiresAt() != null ){
            throw new RuntimeException("Nose pude deshacer Baneo Permanente");
        }
        user.setStatus(UserStatus.ACTIVE);
        user.setBanExpiresAt(null);
        log.info("🔔 El usuario con id {}, fue desbaneado con exito.", user.getId());
        return userRepository.save(user);
    }

}
