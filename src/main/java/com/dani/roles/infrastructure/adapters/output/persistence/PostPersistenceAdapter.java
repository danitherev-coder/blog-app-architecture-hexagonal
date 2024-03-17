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
    

    @Override
    public Optional<Post> findById(Long id) {
        return repository.findById(id).map(postEntity -> {            
            UserEntity userEntity = postEntity.getAuthor();            
            User user = userMapper.toUser(userEntity);            
            Post post = mapper.toPost(postEntity);            
            post.setUser(user);
            return post;
        });
    }


    @Override
    public List<Post> findAll() {
        List<PostEntity> postEntities = repository.findAll();
        List<Post> posts = new ArrayList<>();
        for (PostEntity postEntity : postEntities) {            
            UserEntity userEntity = postEntity.getAuthor();            
            User user = userMapper.toUser(userEntity);            
            Post post = mapper.toPost(postEntity);            
            post.setUser(user);            
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
            Post post = mapper.toPost(postEntity);            
            post.setUser(userMapper.toUser(userEntity));            
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
            UserEntity userEntity = postEntity.getAuthor();            
            User user = userMapper.toUser(userEntity);            
            Post post = mapper.toPost(postEntity);            
            post.setUser(user);            
            posts.add(post);
        }

        return posts;
    }


    @Override
    public Post save(Post post) {        
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            throw new EntityNotFoundException("User details not found");
        }

        String username = userDetails.getUsername();
        
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new EntityNotFoundException("User with username " + username + " not found");
        }
        
        CategoryEntity categoryEntity = categoryRepository.findById(post.getCatIds())
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + post.getCatIds() + " not found"));        
        post.setCatIds(categoryEntity.getId());

        PostEntity postEntity = mapper.toPostEntity(post);
        postEntity.setAuthor(userEntity);
        postEntity.setCategory(categoryEntity);
        
        PostEntity savedPostEntity = repository.save(postEntity);
        
        return mapper.toPost(savedPostEntity);
    }

    @Transactional
    @Override
    public Post update(Long id, Post post) {
        //TODO -> Cuando implemente la seguridad, verificar que el usuario que intenta actualizar el post sea el mismo que lo creó
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            throw new EntityNotFoundException("User details not found");
        }

        String username = userDetails.getUsername();        
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new EntityNotFoundException("User with username " + username + " not found");
        }        
        if(repository.existsById(id)){            
            PostEntity existingPost = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));
            CategoryEntity existinCat = categoryRepository.findById(post.getCatIds())
                    .orElseThrow(() -> new EntityNotFoundException("Category with ID " + post.getCatIds() + " not found"));
            
            if(existingPost.getAuthor().getId().equals(userEntity.getId())){                
                existingPost.setTitle(post.getTitle());
                existingPost.setDescription(post.getDescription());
                existingPost.setContent(post.getContent());
                existingPost.setCover(post.getCover());
                existingPost.setCategory(existinCat);            
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
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            throw new EntityNotFoundException("User details not found");
        }
        
        String username = userDetails.getUsername();
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new EntityNotFoundException("User with username " + username + " not found");
        }
        
        boolean isAdmin = userEntity.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));
        
        if(repository.existsById(id)){            
            PostEntity existingPost = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Post with ID " + id + " not found"));
            
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
