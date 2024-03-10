package com.dani.roles.infrastructure.adapters.output.persistence;

import com.dani.roles.application.ports.output.PostPersistencePort;
import com.dani.roles.domain.exception.UnauthorizedAccessException;
import com.dani.roles.domain.model.Post;
import com.dani.roles.domain.model.User;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.CategoryEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.PostEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.dani.roles.infrastructure.adapters.output.persistence.mapper.PostPersistenceMapper;
import com.dani.roles.infrastructure.adapters.output.persistence.mapper.UserPersistenceMapper;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.CategoryRepository;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.PostRepository;
import com.dani.roles.infrastructure.adapters.output.persistence.repositories.UserRepository;
import com.dani.roles.infrastructure.config.security.jwt.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostPersistenceAdapter implements PostPersistencePort {

    private final PostRepository repository;
    private final PostPersistenceMapper mapper;
    private final UserPersistenceMapper userMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public Optional<Post> findById(Long id) {
        return repository.findById(id).map(postEntity -> {
            // Obtener el usuario asociado con el post
            UserEntity userEntity = postEntity.getAuthor();
            // Mapear UserEntity a User
            User user = userMapper.toUser(userEntity);
            // Mapear PostEntity a Post
            Post post = mapper.toPost(postEntity);
            // Establecer el usuario mapeado en el post
            post.setUser(user);
            return post;
        });
    }


    @Override
    public List<Post> findAll() {
        List<PostEntity> postEntities = repository.findAll();
        List<Post> posts = new ArrayList<>();
        for (PostEntity postEntity : postEntities) {
            // Obtener el usuario asociado con cada publicación
            UserEntity userEntity = postEntity.getAuthor();
            // Mapear UserEntity a User
            User user = userMapper.toUser(userEntity);
            // Mapear PostEntity a Post
            Post post = mapper.toPost(postEntity);
            // Establecer el usuario mapeado en la publicación
            post.setUser(user);
            // Agregar la publicación a la lista
            posts.add(post);
        }
        return posts;
    }

    @Override
    public List<Post> findByUserId(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
        List<PostEntity> postEntities = repository.findByAuthor(userEntity);
        List<Post> posts = new ArrayList<>();

        for (PostEntity postEntity : postEntities) {
            // Mapear PostEntity a Post
            Post post = mapper.toPost(postEntity);
            // Establecer el usuario mapeado en la publicación
            post.setUser(userMapper.toUser(userEntity));
            // Agregar la publicación a la lista
            posts.add(post);
        }
        return posts;
    }

    @Override
    public List<Post> findByCategoryId(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + id + " not found"));
        List<PostEntity> postEntities = repository.findByCategory(categoryEntity);
        List<Post> posts = new ArrayList<>();

        for (PostEntity postEntity : postEntities) {
            // Obtener el usuario asociado con cada publicación
            UserEntity userEntity = postEntity.getAuthor();
            // Mapear UserEntity a User
            User user = userMapper.toUser(userEntity);
            // Mapear PostEntity a Post
            Post post = mapper.toPost(postEntity);
            // Establecer el usuario mapeado en la publicación
            post.setUser(user);
            // Agregar la publicación a la lista
            posts.add(post);
        }

        return posts;
    }


    @Override
    public Post save(Post post) {
        // Obtener el usuario autenticado del contexto de seguridad
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            throw new EntityNotFoundException("User details not found");
        }

        String username = userDetails.getUsername();

        // Buscar si existe el usuario
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new EntityNotFoundException("User with username " + username + " not found");
        }

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
        // Obtener el usuario autenticado del contexto de seguridad
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            throw new EntityNotFoundException("User details not found");
        }

        String username = userDetails.getUsername();
        // Buscar si existe el usuario
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new EntityNotFoundException("User with username " + username + " not found");
        }
        // Verificar que el post exista
        if(repository.existsById(id)){
            // Obtener el post existente
            PostEntity existingPost = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));
            CategoryEntity existinCat = categoryRepository.findById(post.getCatIds())
                    .orElseThrow(() -> new EntityNotFoundException("Category with ID " + post.getCatIds() + " not found"));

            // Verificar si el usuario que intenta actualizar el post es el mismo que lo creó
            if(existingPost.getAuthor().getId().equals(userEntity.getId())){
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
        // Obtener los detalles del usuario actual
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            throw new EntityNotFoundException("User details not found");
        }

        // Buscar el usuario en la base de datos
        String username = userDetails.getUsername();
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new EntityNotFoundException("User with username " + username + " not found");
        }

        // Verificar si el usuario actual tiene el rol de administrador
        boolean isAdmin = userEntity.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

        // Verificar si el post existe
        if(repository.existsById(id)){
            // Obtener el post existente
            PostEntity existingPost = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));

            // Verificar si el usuario actual es el autor del post o es administrador
            if(existingPost.getAuthor().getId().equals(userEntity.getId()) || isAdmin){
                repository.deleteById(id);
            } else {
                throw new UnauthorizedAccessException();
            }
        } else {
            throw new EntityNotFoundException("Post with ID " + id + " not found");
        }
    }

}
