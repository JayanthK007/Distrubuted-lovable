package com.example.distributed_lovable.account_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.example.distributed_lovable.account_service.dto.subscription.*;
import com.example.distributed_lovable.account_service.service.PaymentProcessor;
import com.example.distributed_lovable.account_service.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BillingController {
	
	private final SubscriptionService subscriptionService;
	private final PaymentProcessor paymentProcessor;
	
	@Value("${stripe.webhook.secret}")
	private String webhookSecret;
	
	

	@GetMapping("/me/subscription")
	public ResponseEntity<SubscriptionResponse> getMySubscription(){
		return ResponseEntity.ok(subscriptionService.getCurrentSubscription());
	}
	
	@PostMapping("/payments/checkout")
	public ResponseEntity<CheckOutResponse> createCheckoutResponse(@RequestBody CheckOutRequest request){
		
		return ResponseEntity.ok(paymentProcessor.createCheckOutSessionUrl(request));
	}
	
	@PostMapping("/payments/portal")
	public ResponseEntity<PortalResponse> openCustomPortal(){

		return ResponseEntity.ok(paymentProcessor.openCustomPortal());
	}
	
	@PostMapping("/webhooks/payment")
	public ResponseEntity<String> handleWebhookPayment(
			@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String signature) {
		try {
			
			Event event = Webhook.constructEvent(payload, signature, webhookSecret);
			
			
			EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
			StripeObject stripeObject = null;
			if (dataObjectDeserializer.getObject().isPresent()) {
		       stripeObject = dataObjectDeserializer.getObject().get();
		    } else {
		    	try {
		            stripeObject = dataObjectDeserializer.deserializeUnsafe();
		            if (stripeObject == null) {
			            log.debug("Used unsafe deserialization for event: {}", event.getType());
			            return ResponseEntity.ok().build();
		            }
		        } catch (Exception e) {
		            log.error("Failed to deserialize Stripe event: {}", e.getMessage());
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deserialization failed");
		        }
		    }
			
			Map<String, String> metadata = new HashMap<>();
			if (stripeObject instanceof Session session) {
		        metadata = session.getMetadata();
		    }
			
			
			paymentProcessor.handleWebhookEvent(event.getType(),stripeObject, metadata);
			return ResponseEntity.ok().build();
		      
		} catch (SignatureVerificationException e) {
			
			throw new RuntimeException(e);
		}
	}
}
