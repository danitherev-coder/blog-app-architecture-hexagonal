package com.dani.roles.infrastructure.adapters.output.persistence;

import com.dani.roles.application.ports.output.LoginAndRegisterPersistencePort;
import com.dani.roles.domain.exception.LoginException;
import com.dani.roles.domain.exception.UserNotFoundException;
import com.dani.roles.domain.model.Login;
import com.dani.roles.domain.model.User;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.RoleEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.mapper.UserPersistenceMapper;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.RoleRepository;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.UserRepository;
import com.dani.roles.infrastructure.config.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;



@Component
@RequiredArgsConstructor
public class LoginAndRegisterPersistenceAdapter implements LoginAndRegisterPersistencePort {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPersistenceMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    @Override
    public User register(User user) {
        if (repository.findByUsername(user.getUsername()) != null) {
            throw new DuplicateKeyException("El usuario ya existe");
        }

        if (repository.findByEmail(user.getEmail()) != null) {
            throw new DuplicateKeyException("El email ya existe");
        }

        UserEntity usuario = new UserEntity();
        usuario.setFirstname(user.getFirstname());
        usuario.setLastname(user.getLastname());
        usuario.setUsername(user.getUsername());
        usuario.setEmail(user.getEmail());
        usuario.setPassword(passwordEncoder.encode(user.getPassword()));
        usuario.setImage(user.getImage());

        RoleEntity rol = roleRepository.findById(1L).orElse(null);

        usuario.setRoles(Collections.singleton(rol));

        repository.save(usuario);

        return mapper.toUser(usuario);
    }

    @Override
    public Login login(Login login) {
        try {
            // autenticar el usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateAccessToken(authentication);

            return Login.builder().username(login.getUsername()).password(login.getPassword()).token(token).build();

        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
    }
}
