package com.example.distributed_lovable.account_service.service;

import java.util.Map;

import com.example.distributed_lovable.account_service.dto.subscription.CheckOutRequest;
import com.example.distributed_lovable.account_service.dto.subscription.CheckOutResponse;
import com.example.distributed_lovable.account_service.dto.subscription.PortalResponse;

import com.stripe.model.StripeObject;

public interface PaymentProcessor {
	
	CheckOutResponse createCheckOutSessionUrl(CheckOutRequest request);

	PortalResponse openCustomPortal();

	void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
