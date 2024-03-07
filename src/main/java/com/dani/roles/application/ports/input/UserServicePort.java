package com.dani.roles.application.ports.input;

import com.dani.roles.domain.model.User;

import java.util.List;

public interface UserServicePort {
    User findById(Long id);
    List<User> findAll();
    User save(User user);
    User update(Long id, User user);
    void delete(Long id);
}
