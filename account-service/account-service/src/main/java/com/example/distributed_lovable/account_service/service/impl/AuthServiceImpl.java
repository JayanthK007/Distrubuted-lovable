package com.example.distributed_lovable.account_service.service.impl;

import com.example.distributed_lovable.account_service.dto.auth.AuthResponse;
import com.example.distributed_lovable.account_service.dto.auth.LoginRequest;
import com.example.distributed_lovable.account_service.dto.auth.SignUpRequest;
import com.example.distributed_lovable.account_service.entity.User;
import com.example.distributed_lovable.account_service.mapper.UserMapper;
import com.example.distributed_lovable.account_service.repository.UserRepository;
import com.example.distributed_lovable.account_service.service.AuthService;
import com.example.distributed_lovable.common_lib.error.BadRequestException;
import com.example.distributed_lovable.common_lib.security.AuthUtil;
import com.example.distributed_lovable.common_lib.security.JwtUserPrinciple;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
	
	UserRepository userRepository;
	UserMapper userMapper;
	PasswordEncoder passwordEncoder;
	AuthUtil authUtil;
	AuthenticationManager authenticationManager;

	@Override
	public AuthResponse signup(SignUpRequest request) {
		if (userRepository.findByUsername(request.username()).isPresent()) {
		    throw new BadRequestException("User already exists with username: " + request.username());
		}
		User newUser = userMapper.toUserMapper(request);
		newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
		User savedUser = userRepository.save(newUser);

		JwtUserPrinciple jwtUserPrinciple = new JwtUserPrinciple(savedUser.getId(),
				savedUser.getName(),savedUser.getUsername(), null, new ArrayList<>());

		String token = authUtil.generateAccessToken(jwtUserPrinciple);
		return new AuthResponse(token, userMapper.toUserProfileResponse(jwtUserPrinciple));
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.username(), 
						request.password()
		));
		
		JwtUserPrinciple user = (JwtUserPrinciple) authentication.getPrincipal();
		String token = authUtil.generateAccessToken(user);
		return new AuthResponse(token, userMapper.toUserProfileResponse(user));
		
	}
	
	

}
