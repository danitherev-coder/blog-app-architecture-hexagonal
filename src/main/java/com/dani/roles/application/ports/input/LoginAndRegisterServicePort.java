package com.dani.roles.application.ports.input;

import com.dani.roles.domain.model.Login;
import com.dani.roles.domain.model.User;

import java.util.Map;

public interface LoginAndRegisterServicePort {
    User register(User user);
   Login login(Login login);
}
