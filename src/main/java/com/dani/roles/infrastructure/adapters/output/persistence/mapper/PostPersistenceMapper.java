package com.dani.roles.infrastructure.adapters.output.persistence.mapper;

import com.dani.roles.domain.model.Post;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.PostEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostPersistenceMapper {
    List<Post> toPostList(List<PostEntity> postEntityList);
    PostEntity toPostEntity(Post post);
    Post toPost(PostEntity postEntity);
}
