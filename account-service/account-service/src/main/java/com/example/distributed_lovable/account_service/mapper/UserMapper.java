package com.example.distributed_lovable.account_service.mapper;

import com.example.distributed_lovable.account_service.dto.auth.SignUpRequest;
import com.example.distributed_lovable.account_service.dto.auth.UserProfileResponse;
import com.example.distributed_lovable.account_service.entity.User;
import com.example.distributed_lovable.common_lib.dto.UserDto;
import com.example.distributed_lovable.common_lib.security.JwtAuthFilter;
import com.example.distributed_lovable.common_lib.security.JwtUserPrinciple;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {
	
	User toUserMapper(SignUpRequest request);
	@Mapping(source = "userId", target = "id")
	UserProfileResponse toUserProfileResponse(JwtUserPrinciple user);
	UserDto toUserDto(User user);
}
