package com.dani.roles.infrastructure.adapters.output.persistence;

import com.dani.roles.application.ports.output.UserPersistencePort;
import com.dani.roles.domain.model.Role;
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

        // Set de roles permite que no se repitan los roles
        Set<RoleEntity> roleEntities = new HashSet<>();
        // Buscar los roles en la base de datos obtenidos por el user
        for (Long roleId : user.getRoleIds()) {
            // Buscar el rol por ID gracias al rolerepository, si no encuentra, lanza una excepcion
            RoleEntity roleEntity = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Role with ID " + roleId + " not found"));
            // finalmente agregamos el rol a la lista de roles
            roleEntities.add(roleEntity);
        }

        // Convertimos el objeto de usuario de dominio(User user) a una entidad de usuario
        UserEntity userEntity = mapper.toUserEntity(user);
        userEntity.setRoles(roleEntities); // Asignar las entidades de roles al usuario

        // Guardar el usuario en la base de datos
        UserEntity savedUserEntity = repository.save(userEntity);

        // Convertir la entidad de usuario guardada a un objeto de usuario de dominio y devolverlo
        return mapper.toUser(savedUserEntity);
    }

    @Override
    public User update(Long id, User user) {
        UserEntity existingUser = repository.findById(user.getId()).orElseThrow(EntityNotFoundException::new);

        // Verificar si existe un usuario con el mismo nombre de usuario pero diferente ID
        UserEntity existingUsername = repository.findByUsername(user.getUsername());
        if (existingUsername != null && (existingUser == null || !existingUser.getUsername().equals(user.getUsername()))) {
            throw new DuplicateKeyException("Username: " + user.getUsername() + " already exists");
        }

        // Verificar si existe un usuario con el mismo correo electrónico pero diferente ID
        UserEntity existingEmail = repository.findByEmail(user.getEmail());
        if (existingEmail != null && (existingUser == null || !existingUser.getEmail().equals(user.getEmail()))) {
            throw new DuplicateKeyException("Email: " + user.getEmail() + " already exists");
        }

        // Set de roles permite que no se repitan los roles
        Set<RoleEntity> roleEntities = new HashSet<>();
        // Buscar los roles en la base de datos obtenidos por el user
        for (Long roleId : user.getRoleIds()) {
            // Buscar el rol por ID gracias al rolerepository, si no encuentra, lanza una excepcion
            RoleEntity roleEntity = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Role with ID " + roleId + " not found"));
            // finalmente agregamos el rol a la lista de roles
            roleEntities.add(roleEntity);
        }

        // Convertimos el objeto de usuario de dominio(User user) a una entidad de usuario
        UserEntity userEntity = mapper.toUserEntity(user);
        userEntity.setRoles(roleEntities); // Asignar las entidades de roles al usuario

        // Guardar el usuario en la base de datos
        UserEntity savedUserEntity = repository.save(userEntity);

        // Convertir la entidad de usuario guardada a un objeto de usuario de dominio y devolverlo
        return mapper.toUser(savedUserEntity);
    }

    @Override
    public void delete(Long id) {
        // NO buscamos si existe el usuario porque eso lo hacemos en la capa de aplicacion, aca pasamos solo el id
        repository.deleteById(id);
    }
}
