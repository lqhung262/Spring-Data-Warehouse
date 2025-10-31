package com.example.demo.mapper.systemuser;

import com.example.demo.dto.systemuser.User.UserRequest;
import com.example.demo.dto.systemuser.User.UserResponse;
import com.example.demo.entity.systemuser.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toUser(UserRequest request);

    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserRequest request);
}
