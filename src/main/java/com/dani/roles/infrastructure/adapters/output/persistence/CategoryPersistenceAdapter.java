package com.dani.roles.infrastructure.adapters.output.persistence;


import com.dani.roles.application.ports.output.CategoryPersistencePort;
import com.dani.roles.domain.model.Category;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.CategoryEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.mapper.CategoryPersistenceMapper;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryPersistencePort {

    private final CategoryRepository repository;
    private final CategoryPersistenceMapper mapper;


    @Override
    public Optional<Category> findById(Long id) {
        return repository.findById(id).map(mapper::toCategory);
    }

    @Override
    public List<Category> findAll() {
        return mapper.toCategoryList(repository.findAll());
    }

    @Override
    public Category save(Category category) {
        CategoryEntity existCategory = repository.findByName(category.getName());
        if (existCategory != null) {
            throw new DuplicateKeyException("Category: " + category.getName() + " already exists");
        }

        return mapper.toCategory(repository.save(mapper.toCategoryEntity(category)));
    }

    @Override
    public Category update(Long id, Category category) {
        Category existCat = this.findById(id).orElseThrow(() -> new EntityNotFoundException("Category with ID " + id + " not found"));
        CategoryEntity existCatName = repository.findByName(existCat.getName());

        if (existCatName != null && !existCatName.getId().equals(id)) {
            throw new DuplicateKeyException("Category: " + category.getName() + " already exists");
        }

        return mapper.toCategory(repository.save(mapper.toCategoryEntity(category)));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
