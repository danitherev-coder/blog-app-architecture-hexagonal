package com.dani.roles.application.service;

import com.dani.roles.application.ports.input.LoginAndRegisterServicePort;
import com.dani.roles.application.ports.output.LoginAndRegisterPersistencePort;
import com.dani.roles.domain.model.Login;
import com.dani.roles.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LoginAndRegisterService implements LoginAndRegisterServicePort {

    private final LoginAndRegisterPersistencePort persistencePort;
    @Override
    public User register(User user) {
        return persistencePort.register(user);
    }
    @Override
    public Login login(Login login) {
        return persistencePort.login(login);
    }

}
