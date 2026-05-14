package com.example.distributed_lovable.common_lib.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter{
	
	private final AuthUtil authUtil;
	private final HandlerExceptionResolver exceptionResolver;
	public JwtAuthFilter(
	        AuthUtil authUtil,
	        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver
	) {
	    this.authUtil = authUtil;
	    this.exceptionResolver = exceptionResolver;
	}
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("incoming request: {}", request.getRequestURI());
		try {
			
		
			String requestHeaderToken = request.getHeader("Authorization");
			
			if (requestHeaderToken == null || !requestHeaderToken.startsWith("Bearer ")) {
				filterChain.doFilter(request, response);
				return;
			}
			
			String jwttoken = requestHeaderToken.split("Bearer ")[1];
			
			JwtUserPrinciple user = authUtil.verifyToken(jwttoken);
			
			if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						user, jwttoken, user.authorities());
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
			
			filterChain.doFilter(request, response);
		
		} catch (Exception e) {
			
			exceptionResolver.resolveException(request, response, null, e);
		}
		
		
	}

}
