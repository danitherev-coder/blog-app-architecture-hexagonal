package com.dani.roles.infrastructure.adapters.output.persistence.repositories;

import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);
}
