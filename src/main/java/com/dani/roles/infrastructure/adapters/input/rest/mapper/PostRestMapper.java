package com.dani.roles.infrastructure.adapters.input.rest.mapper;

import com.dani.roles.domain.model.Post;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.PostCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.PostResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostRestMapper {

    Post toPost(PostCreateRequest request);
    PostResponse toPostCreateRequest(Post post);
    List<PostResponse> toPostResponseList(List<Post> postList);
}
