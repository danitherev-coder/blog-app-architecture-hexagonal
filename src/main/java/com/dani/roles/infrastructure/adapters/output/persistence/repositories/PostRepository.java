package com.dani.roles.infrastructure.adapters.output.persistence.repositories;

import com.dani.roles.infrastructure.adapters.output.persistence.entities.CategoryEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.PostEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

   List<PostEntity> findByAuthor(UserEntity user);
   List<PostEntity> findByCategory(CategoryEntity category);
}
