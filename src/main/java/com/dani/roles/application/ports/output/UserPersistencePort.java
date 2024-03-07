package com.dani.roles.application.ports.output;

import com.dani.roles.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserPersistencePort {

    Optional<User> findById(Long id);
    List<User> findAll();
    User save(User user);
    User update(Long id, User user);
    void delete(Long id);

}
