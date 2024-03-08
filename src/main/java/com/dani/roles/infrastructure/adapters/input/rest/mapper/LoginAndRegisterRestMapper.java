package com.dani.roles.infrastructure.adapters.input.rest.mapper;

import com.dani.roles.domain.model.Login;
import com.dani.roles.infrastructure.adapters.input.rest.model.request.LoginCreateRequest;
import com.dani.roles.infrastructure.adapters.input.rest.model.response.JwtAuthResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoginAndRegisterRestMapper {
    Login toLogin(LoginCreateRequest request);
    @Mapping(target = "tokenDeAcceso", source = "token")
    JwtAuthResponseDto toJwtAuthCreateLogin(Login login);
}
