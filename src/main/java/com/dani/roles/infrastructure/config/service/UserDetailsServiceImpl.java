package com.dani.roles.infrastructure.config.service;

import com.dani.roles.application.service.UserService;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.RoleEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userDetails =  repository.findByUsername(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }


        return User.withUsername(userDetails.getUsername())
                .password(userDetails.getPassword())
                .authorities(mapearRoles(userDetails.getRoles()))
                .build();
    }

    private Collection<? extends GrantedAuthority> mapearRoles(Set<RoleEntity> roles) {

        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getName()))
                .collect(Collectors.toList());
    }
}
