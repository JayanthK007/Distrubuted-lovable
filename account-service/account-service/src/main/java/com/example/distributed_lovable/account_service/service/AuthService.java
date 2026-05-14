package com.example.distributed_lovable.account_service.service;


import com.example.distributed_lovable.account_service.dto.auth.AuthResponse;
import com.example.distributed_lovable.account_service.dto.auth.LoginRequest;
import com.example.distributed_lovable.account_service.dto.auth.SignUpRequest;

public interface AuthService {


	AuthResponse signup(SignUpRequest request);

	AuthResponse login(LoginRequest request);

}
