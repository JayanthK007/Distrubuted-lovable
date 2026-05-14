package com.example.distributed_lovable.intelligence_service.security;

import com.example.distributed_lovable.common_lib.security.JwtAuthFilter;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class IntelligenceServiceSecurityConfig {



    private final JwtAuthFilter authFilter;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {

        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(SessionConfig -> SessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                                auth
                                .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.accessDeniedHandler((request, response, accessDeniedException) -> {
                            handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
                        }));


        return httpSecurity.build();
    }

}

