package com.dani.roles.infrastructure.adapters.output.persistence.repositories;

import com.dani.roles.infrastructure.adapters.output.persistence.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    RoleEntity findByName(String name);
}
