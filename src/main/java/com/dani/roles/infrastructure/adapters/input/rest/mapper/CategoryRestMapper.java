package com.dani.roles.infrastructure.adapters.input.rest.mapper;

import com.dani.roles.domain.model.Category;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.CategoryCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.CategoryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryRestMapper {
    List<CategoryResponse> toCategoryResponseList(List<Category> categoryResponseList);
    Category toCategory(CategoryCreateRequest request);
    CategoryResponse toCategoryCreateRequest(Category category);
}
