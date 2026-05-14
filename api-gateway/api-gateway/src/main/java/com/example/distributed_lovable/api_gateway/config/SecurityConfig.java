package com.example.distributed_lovable.api_gateway.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityConfig {

    private List<String> publicRoutes = new ArrayList<>();
}
