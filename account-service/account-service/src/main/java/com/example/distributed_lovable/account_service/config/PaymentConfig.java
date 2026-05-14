package com.example.distributed_lovable.account_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

@Configuration
public class PaymentConfig {

	@Value("${stripe.api.secret}")
    private String secretKey;

    @PostConstruct
    public void setup() {
        Stripe.apiKey = secretKey;
    }
}
