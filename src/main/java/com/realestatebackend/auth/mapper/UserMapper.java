package com.realestatebackend.auth.mapper;

import com.realestatebackend.auth.dto.UserResponse;
import com.realestatebackend.auth.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);
}
