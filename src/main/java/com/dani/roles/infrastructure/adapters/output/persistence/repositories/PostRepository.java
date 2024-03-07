package com.dani.roles.infrastructure.adapters.output.persistence.repositories;

import com.dani.roles.infrastructure.adapters.output.persistence.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
