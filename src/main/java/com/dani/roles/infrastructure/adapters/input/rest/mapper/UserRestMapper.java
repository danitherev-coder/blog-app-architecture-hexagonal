package com.dani.roles.infrastructure.adapters.input.rest.mapper;

import com.dani.roles.domain.model.User;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.UserCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRestMapper {

    User toUser(UserCreateRequest request);
    UserResponse toUserCreateRequest(User user);
    List<UserResponse> toUserResponseList(List<User> userList);

}
