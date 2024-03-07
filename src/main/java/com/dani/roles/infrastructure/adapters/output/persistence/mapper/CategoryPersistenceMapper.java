package com.dani.roles.infrastructure.adapters.output.persistence.mapper;


import com.dani.roles.domain.model.Category;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.CategoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryPersistenceMapper {

    List<Category> toCategoryList(List<CategoryEntity> categoryEntityList);

    CategoryEntity toCategoryEntity(Category category);

    Category toCategory(CategoryEntity categoryEntity);

}
