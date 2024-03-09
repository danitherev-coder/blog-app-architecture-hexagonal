package com.dani.roles.infrastructure.adapters.input.rest;


import com.dani.roles.application.ports.input.LoginAndRegisterServicePort;
import com.dani.roles.infrastructure.adapters.input.rest.mapper.LoginAndRegisterRestMapper;
import com.dani.roles.infrastructure.adapters.input.rest.mapper.UserRestMapper;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.ChangePassword;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.EmailCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.LoginCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.UserCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.EmailSendingResponse;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.JwtAuthResponseDto;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.UserResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse save(@RequestBody @Valid UserCreateRequest request) {
        return restMapper.toUserCreateRequest(servicePort.register(restMapper.toUser(request)));
    }

    @PostMapping("/reset-password")
    public EmailSendingResponse resetPassword(@RequestBody @Valid EmailCreateRequest request) {
        servicePort.resetPassword(request.getEmail());
        return new EmailSendingResponse("Email enviado correctamente");
    }

    @GetMapping("/verify/{token}")
    public RedirectView verifyToken(@PathVariable String token) {
        servicePort.verifyToken(token);
        return new RedirectView("http://localhost:8080/api/v1/auth/change-password/" + token);
    }

    @PostMapping("/change-password/{token}")
    public EmailSendingResponse changePassword(@RequestBody @Valid ChangePassword request, @PathVariable String token) {
        servicePort.changePassword(request.getPassword(), token);
        return new EmailSendingResponse("Contraseña cambiada correctamente");
    }

}
