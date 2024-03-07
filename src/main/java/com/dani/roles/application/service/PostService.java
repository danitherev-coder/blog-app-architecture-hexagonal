package com.dani.roles.application.service;

import com.dani.roles.application.ports.input.PostServicePort;
import com.dani.roles.application.ports.output.PostPersistencePort;
import com.dani.roles.domain.model.Post;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostService implements PostServicePort {

    private final PostPersistencePort persistencePort;

    @Override
    public Post findById(Long id) {
        return persistencePort.findById(id).orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));
    }

    @Override
    public List<Post> findAll() {
        return persistencePort.findAll();
    }

    @Override
    public Post save(Post post) {
        return persistencePort.save(post);
    }

    @Override
    public Post update(Long id, Post post) {
        return persistencePort.findById(id)
                .map(savedPost -> {
                    savedPost.setTitle(post.getTitle());
                    savedPost.setDescription(post.getDescription());
                    savedPost.setContent(post.getContent());
                    savedPost.setCover(post.getCover());
                    savedPost.setCatIds(post.getCatIds());
                    savedPost.setAuthorId(post.getAuthorId());
                    return persistencePort.update(id, savedPost);
                })
                .orElseThrow(()-> new EntityNotFoundException("Post with ID " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        if(persistencePort.findById(id).isEmpty()){
            throw new EntityNotFoundException("Post with ID " + id + " not found");
        }
        persistencePort.delete(id);
    }
}
