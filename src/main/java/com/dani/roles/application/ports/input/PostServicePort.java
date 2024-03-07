package com.dani.roles.application.ports.input;



import com.dani.roles.domain.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostServicePort {
    Post findById(Long id);
    List<Post> findAll();
    Post save(Post post);
    Post update(Long id, Post post);
    void delete(Long id);
}
