package com.example.distributed_lovable.account_service.controller;

import com.example.distributed_lovable.account_service.dto.auth.AuthResponse;
import com.example.distributed_lovable.account_service.dto.auth.LoginRequest;
import com.example.distributed_lovable.account_service.dto.auth.SignUpRequest;
import com.example.distributed_lovable.account_service.service.AuthService;
import com.example.distributed_lovable.common_lib.security.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@FieldDefaults(makeFinal = true)
public class AuthController {
	
	private AuthService authService;
//	private UserDetailsService userService;
	private AuthUtil authUtil;
	
	
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> signUp(@RequestBody @Valid SignUpRequest request){
		return ResponseEntity.ok(authService.signup(request));
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request){
		return ResponseEntity.ok(authService.login(request));
	}
	
//	@GetMapping("/me")
//	public ResponseEntity<UserProfileResponse> getProfile(){
//		Long userId = authUtil.getCurrentUserId();
//		return ResponseEntity.ok(userService.getProfile(userId));
//	}
}
