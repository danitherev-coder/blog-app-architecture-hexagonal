package com.dani.roles.infrastructure.adapters.output.persistence;

import com.dani.roles.application.ports.output.PostPersistencePort;
import com.dani.roles.domain.exception.UnauthorizedAccessException;
import com.dani.roles.domain.model.Post;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.CategoryEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.PostEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.mapper.PostPersistenceMapper;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.CategoryRepository;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.PostRepository;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostPersistenceAdapter implements PostPersistencePort {

    private final PostRepository repository;
    private final PostPersistenceMapper mapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<Post> findById(Long id) {
        return repository.findById(id).map(mapper::toPost);
    }

    @Override
    public List<Post> findAll() {
        return mapper.toPostList(repository.findAll());
    }

    @Override
    public Post save(Post post) {

        System.out.println("post user id PERSISTENCIA: " + post.getAuthorId());
        System.out.println("post category id PERSISTENCIA: " + post.getCatIds());

        // Buscar si existe el usuario
        UserEntity userEntity = userRepository.findById(post.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + post.getAuthorId() + " not found"));
        // Asignar el usuario al post
        post.setAuthorId(userEntity.getId());

        // Buscar si existe la categoría
        CategoryEntity categoryEntity = categoryRepository.findById(post.getCatIds())
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + post.getCatIds() + " not found"));
        // Asignar la categoría al post
        post.setCatIds(categoryEntity.getId());

        PostEntity postEntity = mapper.toPostEntity(post);
        postEntity.setAuthor(userEntity);
        postEntity.setCategory(categoryEntity);

        // Guardar el post en la base de datos
        PostEntity savedPostEntity = repository.save(postEntity);

        // Convertir y devolver el post guardado
        return mapper.toPost(savedPostEntity);
    }
    @Transactional
    @Override
    public Post update(Long id, Post post) {
        //TODO -> Cuando implemente la seguridad, verificar que el usuario que intenta actualizar el post sea el mismo que lo creó
        // usando el contexto de seguridad de Spring Security, sin tener que asignar manualmente el id, sino del Bearer TOken

        // Verificar que el post exista
        if(repository.existsById(id)){
            // Obtener el post existente
            PostEntity existingPost = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));
            CategoryEntity existinCat = categoryRepository.findById(post.getCatIds())
                    .orElseThrow(() -> new EntityNotFoundException("Category with ID " + post.getCatIds() + " not found"));

            // Verificar si el usuario que intenta actualizar el post es el mismo que lo creó
            if(existingPost.getAuthor().getId().equals(post.getAuthorId())){
                // Actualizar los campos del post existente con los valores del post pasado como parámetro
                existingPost.setTitle(post.getTitle());
                existingPost.setDescription(post.getDescription());
                existingPost.setContent(post.getContent());
                existingPost.setCover(post.getCover());
                existingPost.setCategory(existinCat);
                // Guardar los cambios en el post existente
                return mapper.toPost(repository.save(existingPost));
            } else {
                throw new UnauthorizedAccessException();
            }
        } else {
            throw new EntityNotFoundException("Post with ID " + id + " not found");
        }
    }


    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
