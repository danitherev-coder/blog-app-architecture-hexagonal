package com.dani.roles.infrastructure.adapters.output.persistence.repositories;

import com.dani.roles.infrastructure.adapters.output.persistence.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    CategoryEntity findByName(String name);
}
