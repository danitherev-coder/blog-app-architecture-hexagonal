package com.dani.roles.infrastructure.adapters.output.persistence.mapper;

import com.dani.roles.domain.model.User;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.UserEntity;
import org.mapstruct.Mapper;
import java.util.List;


@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
    List<User> toUserList(List<UserEntity> userEntityList);

    UserEntity toUserEntity(User user);
    User toUser(UserEntity userEntity);

}

