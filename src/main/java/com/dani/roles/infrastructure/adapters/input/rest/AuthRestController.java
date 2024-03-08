package com.dani.roles.infrastructure.adapters.input.rest;


import com.dani.roles.application.ports.input.LoginAndRegisterServicePort;
import com.dani.roles.infrastructure.adapters.input.rest.mapper.LoginAndRegisterRestMapper;
import com.dani.roles.infrastructure.adapters.input.rest.mapper.UserRestMapper;
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

    private final LoginAndRegisterServicePort servicePort;
    private final UserRestMapper restMapper;
    private final LoginAndRegisterRestMapper loginAndRegisterRestMapper;

    @PostMapping("/login")
    public JwtAuthResponseDto authenticateUser(@RequestBody @Valid LoginCreateRequest request) {
        return loginAndRegisterRestMapper.toJwtAuthCreateLogin(servicePort.login(loginAndRegisterRestMapper.toLogin(request)));
    }

    @Transactional
    @PostMapping("/signup")
    public ResponseEntity<?> registrarUser(@RequestBody @Valid UserCreateRequest userCreateRequest) {

        return ResponseEntity.ok(servicePort.register(restMapper.toUser(userCreateRequest)));
    }

}
