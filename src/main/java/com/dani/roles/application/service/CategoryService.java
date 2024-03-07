package com.dani.roles.application.service;

import com.dani.roles.application.ports.input.CategoryServicePort;
import com.dani.roles.application.ports.output.CategoryPersistencePort;
import com.dani.roles.domain.model.Category;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServicePort {

    private final CategoryPersistencePort persistencePort;

    @Override
    public Category findById(Long id) {
        return persistencePort.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<Category> findAll() {
        return persistencePort.findAll();
    }

    @Override
    public Category save(Category category) {
        return persistencePort.save(category);
    }

    @Override
    public Category update(Long id, Category category) {
        return persistencePort.findById(id)
                .map(savedCategory -> {
                    savedCategory.setName(category.getName());
                    return persistencePort.update(id, savedCategory);
                })
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public void delete(Long id) {
        if(persistencePort.findById(id).isEmpty()){
            throw new EntityNotFoundException();
        }
        persistencePort.delete(id);
    }
}
