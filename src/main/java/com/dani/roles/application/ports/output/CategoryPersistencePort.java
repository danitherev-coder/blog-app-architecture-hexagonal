package com.dani.roles.application.ports.output;

import com.dani.roles.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryPersistencePort {
   Optional<Category> findById(Long id);

    List<Category> findAll();

    Category save(Category category);

    Category update(Long id, Category category);

    void delete(Long id);
}
