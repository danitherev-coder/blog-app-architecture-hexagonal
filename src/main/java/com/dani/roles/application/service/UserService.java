package com.dani.roles.application.service;

import com.dani.roles.application.ports.input.UserServicePort;
import com.dani.roles.application.ports.output.UserPersistencePort;
import com.dani.roles.domain.exception.UserNotFoundException;
import com.dani.roles.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserServicePort {

    private final UserPersistencePort persistencePort;

    @Override
    public User findById(Long id) {
        return persistencePort.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public List<User> findAll() {
        return persistencePort.findAll();
    }

    @Override
    public User save(User user) {
        return persistencePort.save(user);
    }

    @Override
    public User update(Long id, User user) {

        System.out.println("Username: " + user.getUsername());
        System.out.println("User: " + user);
        System.out.println("ID: " + id);

        return persistencePort.findById(id)
                .map(savedUser -> {
                    savedUser.setFirstname(user.getFirstname());
                    savedUser.setLastname(user.getLastname());
                    savedUser.setEmail(user.getEmail());
                    savedUser.setUsername(user.getUsername());
                    savedUser.setImage(user.getImage());
                    savedUser.setPassword(user.getPassword());
                    savedUser.setRoleIds(user.getRoleIds());
                    return persistencePort.update(id, savedUser);

                })
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void delete(Long id) {
        if(persistencePort.findById(id).isEmpty()){
            throw new UserNotFoundException();
        }

        persistencePort.delete(id);
    }
}
