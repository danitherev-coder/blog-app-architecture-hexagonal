package com.dani.roles.infrastructure.adapters.output.persistence;

import com.dani.roles.application.ports.output.LoginAndRegisterPersistencePort;
import com.dani.roles.domain.exception.LoginException;
import com.dani.roles.domain.model.Login;
import com.dani.roles.domain.model.User;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.RoleEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.mapper.UserPersistenceMapper;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.RoleRepository;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.UserRepository;
import com.dani.roles.infrastructure.config.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final JavaMailSender mailSender;
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

        RoleEntity rol = roleRepository.findById(2L).orElseThrow(EntityNotFoundException::new);

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

    @Override
    public void resetPassword(String email) {

        UserEntity userEntity = repository.findByEmail(email);
        if (userEntity == null) {
            throw new EntityNotFoundException("El usuario no existe");
        }

        String token = jwtUtil.generateAccessToken(new UsernamePasswordAuthenticationToken(userEntity.getUsername(), null));

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("danitherev98@gmail.com");
        mailMessage.setTo(email);
        mailMessage.setSubject("Restablecer Password");
        mailMessage.setText("Para restablecer tu password, haz click en el siguiente enlace: http://localhost:8080/api/v1/auth/verify/"+token);

        mailSender.send(mailMessage);
    }

    @Override
    public void verifyToken(String token) {

        System.out.println("token adapter: " + token);

        boolean isTokenValid = jwtUtil.isTokenValid(token);
        if (!isTokenValid) {
            throw new LoginException("El token no es valido");
        }
    }

    @Override
    public void changePassword(String password, String token) {



        if(password ==null || password.isEmpty()){
            throw new LoginException("La contraseña no puede ser nula");
        }

        boolean isTokenValid = jwtUtil.isTokenValid(token);
        if (!isTokenValid) {
            throw new LoginException("El token no es valido");
        }

        UserEntity userEntity = repository.findByUsername(jwtUtil.getUsernameFromToken(token));
        if (userEntity == null) {
            throw new EntityNotFoundException("El usuario no existe");
        }

        userEntity.setPassword(passwordEncoder.encode(password));
        repository.save(userEntity);
    }
}
