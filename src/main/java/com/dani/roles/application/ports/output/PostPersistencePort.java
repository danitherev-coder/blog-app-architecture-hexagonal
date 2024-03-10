package com.dani.roles.application.ports.output;

import com.dani.roles.domain.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostPersistencePort {
    Optional<Post> findById(Long id);
    List<Post> findAll();
    List<Post> findByUserId(Long id);
    List<Post> findByCategoryId(Long id);
    Post save(Post post);
    Post update(Long id, Post post);
    void delete(Long id);
}
