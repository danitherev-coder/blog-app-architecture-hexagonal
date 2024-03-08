package com.dani.roles.application.ports.output;

import com.dani.roles.domain.model.Login;
import com.dani.roles.domain.model.User;


public interface LoginAndRegisterPersistencePort {

    User register(User user);

   Login login(Login login);

}
