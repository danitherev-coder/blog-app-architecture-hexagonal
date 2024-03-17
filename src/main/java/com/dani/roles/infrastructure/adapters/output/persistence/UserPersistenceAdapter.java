package com.dani.roles.infrastructure.adapters.output.persistence;

import com.dani.roles.application.ports.output.UserPersistencePort;
import com.dani.roles.domain.model.User;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.RoleEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.mapper.UserPersistenceMapper;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.RoleRepository;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final UserPersistenceMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(mapper::toUser);
    }

    @Override
    public List<User> findAll() {
        return mapper.toUserList(repository.findAll());
    }

    @Transactional
    @Override
    public User save(User user) {

        UserEntity existUsername = repository.findByUsername(user.getUsername());
        if(existUsername != null){
            throw new DuplicateKeyException("Username: " + user.getUsername() + " already exists");
        }

        UserEntity existEmail = repository.findByEmail(user.getEmail());
        if(existEmail != null){
            throw new DuplicateKeyException("Email: "+ user.getEmail() + " already exists");
        }

        Set<RoleEntity> roleEntities = new HashSet<>();
        for (Long roleId : user.getRoleIds()) {

            RoleEntity roleEntity = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Role with ID " + roleId + " not found"));

            roleEntities.add(roleEntity);
        }

        UserEntity userEntity = mapper.toUserEntity(user);
        userEntity.setRoles(roleEntities);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        
        UserEntity savedUserEntity = repository.save(userEntity);
        
        return mapper.toUser(savedUserEntity);
    }

    @Override
    public User update(Long id, User user) {
        UserEntity existingUser = repository.findById(user.getId()).orElseThrow(EntityNotFoundException::new);
        
        UserEntity existingUsername = repository.findByUsername(user.getUsername());
        if (existingUsername != null && (existingUser == null || !existingUser.getUsername().equals(user.getUsername()))) {
            throw new DuplicateKeyException("Username: " + user.getUsername() + " already exists");
        }
        
        UserEntity existingEmail = repository.findByEmail(user.getEmail());
        if (existingEmail != null && (existingUser == null || !existingUser.getEmail().equals(user.getEmail()))) {
            throw new DuplicateKeyException("Email: " + user.getEmail() + " already exists");
        }
        
        Set<RoleEntity> roleEntities = new HashSet<>();        
        for (Long roleId : user.getRoleIds()) {            
            RoleEntity roleEntity = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Role with ID " + roleId + " not found"));            
            roleEntities.add(roleEntity);
        }
        
        UserEntity userEntity = mapper.toUserEntity(user);
        userEntity.setRoles(roleEntities);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));        
        UserEntity savedUserEntity = repository.save(userEntity);
        
        return mapper.toUser(savedUserEntity);
    }

    @Override
    public void delete(Long id) {        
        repository.deleteById(id);
    }
}
