package com.dani.roles.infrastructure.adapters.input.rest;


import com.dani.roles.infrastructure.adapters.input.rest.model.request.LoginCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.UserCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.JwtAuthResponseDto;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.RoleEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.RoleRepository;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.UserRepository;
import com.dani.roles.infrastructure.config.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RoleRepository rolRepository;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDto> authenticateUser(@RequestBody @Valid LoginCreateRequest loginCreateRequest) {

        System.out.println(loginCreateRequest.getUsername() + " " + loginCreateRequest.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginCreateRequest.getUsername(), loginCreateRequest.getPassword()));


            System.out.println("AUTHENTICATION: " + authentication.getName() + " " + authentication.getAuthorities() + " " + authentication.getPrincipal() + " " + authentication.getCredentials() + " " + authentication.getDetails() + " " + authentication.isAuthenticated());

            SecurityContextHolder.getContext().setAuthentication(authentication);


            String token = jwtUtil.generateAccessToken(authentication);

            System.out.println("TOKEN: " + token);

            return ResponseEntity.ok(new JwtAuthResponseDto(token));

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            throw new UsernameNotFoundException("Usuario o contraseña incorrectos");
        }
    }

    @Transactional
    @PostMapping("/signup")
    public ResponseEntity<?> registrarUser(@RequestBody @Valid UserCreateRequest userCreateRequest) {

        if (userRepository.findByUsername(userCreateRequest.getUsername()) != null) {
            return new ResponseEntity<>("El nombre de usuario ya existe", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.findByEmail(userCreateRequest.getEmail()) != null) {
            return new ResponseEntity<>("El email ya existe", HttpStatus.BAD_REQUEST);
        }

        UserEntity usuario = new UserEntity();
        usuario.setFirstname(userCreateRequest.getFirstname());
        usuario.setLastname(userCreateRequest.getLastname());
        usuario.setUsername(userCreateRequest.getUsername());
        usuario.setEmail(userCreateRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        usuario.setImage(userCreateRequest.getImage());

        RoleEntity rol = rolRepository.findById(1L).orElse(null);

        usuario.setRoles(Collections.singleton(rol));

        userRepository.save(usuario);

        return new ResponseEntity<>("Usuario registrado con éxito!!", HttpStatus.OK);
    }

}
