package com.example.distributed_lovable.account_service.service.impl;

import java.time.Instant;
import java.util.Map;

import com.example.distributed_lovable.account_service.dto.subscription.CheckOutRequest;
import com.example.distributed_lovable.account_service.dto.subscription.CheckOutResponse;
import com.example.distributed_lovable.account_service.dto.subscription.PortalResponse;
import com.example.distributed_lovable.account_service.entity.Plan;
import com.example.distributed_lovable.account_service.entity.User;
import com.example.distributed_lovable.account_service.repository.PlanRepository;
import com.example.distributed_lovable.account_service.repository.UserRepository;
import com.example.distributed_lovable.account_service.service.PaymentProcessor;
import com.example.distributed_lovable.account_service.service.SubscriptionService;
import com.example.distributed_lovable.common_lib.enums.SubscriptionStatus;
import com.example.distributed_lovable.common_lib.error.BadRequestException;
import com.example.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.example.distributed_lovable.common_lib.security.AuthUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.Invoice.Parent;
import com.stripe.model.Invoice.Parent.SubscriptionDetails;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentProcessor implements PaymentProcessor {
	
	@Value("${app.frontend.url}")
	String frontendUrl;
	
	private final AuthUtil authUtil;
	private final PlanRepository planRepository;
	private final UserRepository userRepository;
	private final SubscriptionService subscriptionService;

	@Override
	public CheckOutResponse createCheckOutSessionUrl(CheckOutRequest request) {

		Plan plan = planRepository.findById(request.planId())
				.orElseThrow(() -> new ResourceNotFoundException("Plan",request.planId()));
		Long userId = authUtil.getCurrentUserId();
		
		User user = getUser(userId);
		
		var params = SessionCreateParams.builder()
		          .addLineItem(
		              SessionCreateParams.LineItem.builder().setPrice(plan.getStripePriceId()).setQuantity(1L).build())
		          .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
		          .setSubscriptionData(new SessionCreateParams.SubscriptionData.Builder()
		        		  	.setBillingMode(SessionCreateParams.SubscriptionData.BillingMode.builder()
		        		  			.setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE)
		        		  			.build())
		        		  	.build()
		        		  )
		          .setSuccessUrl(frontendUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
		          .setCancelUrl(frontendUrl + "/cancel.html")
		          .putMetadata("user_id", userId.toString())
		          .putMetadata("plan_id", plan.getId().toString());
		          
		      try {
		    	  
		    	String stripeCustomerId = user.getStripeCustomerId();
		    	
		    	if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
		    		params.setCustomerEmail(user.getUsername());
		    	} else {
		    		params.setCustomer(stripeCustomerId);
		    	}
				Session session = Session.create(params.build());
				return new CheckOutResponse(session.getUrl());
			} catch (StripeException e) {
				e.printStackTrace();
			}
		return null;
	}

	

	@Override
	public PortalResponse openCustomPortal() {
		Long userId = authUtil.getCurrentUserId();
		User user = getUser(userId);
		
		String stripeCustomerId = user.getStripeCustomerId();
		
		if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
			throw new BadRequestException("User does not have a Stripe Customer Id, UserId: "+ userId);
		}
		
		try {
			var session = com.stripe.model.billingportal.Session.create(
					com.stripe.param.billingportal.SessionCreateParams.builder()
					.setCustomer(stripeCustomerId)
					.setReturnUrl(frontendUrl)
					.build()
					);
			return new PortalResponse(session.getUrl());
		} catch (StripeException e) {
				throw new RuntimeException(e);
		}
	}

	@Override
	public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
		log.debug("Handling stripe event {}", type);
		
		
		switch (type) {
		case "checkout.session.completed" -> handleCheckoutSessionCompleted((Session) stripeObject, metadata);
		case "customer.subscription.updated" -> handleCustomerSubscriptionUpdated((Subscription) stripeObject);
		case "customer.subscription.deleted" -> handleCustomerSubscriptionDeleted((Subscription) stripeObject);
		case "invoice.paid" -> handleInvoicePaid((Invoice) stripeObject);
		case "invoice.payment_failed" -> handleInvoicePaymentFailed((Invoice) stripeObject);
		default -> log.debug("Ignoring the event {}", type);
		}
		
	}
		
	public void handleCheckoutSessionCompleted(Session session, Map<String, String> metadata) {
		if (session == null) {
			log.error("session object is null");
			return;
		}
		Long userId = Long.parseLong(metadata.get("user_id"));
		Long planId = Long.parseLong(metadata.get("plan_id"));
		
		User user = getUser(userId); 
		
		String subscriptionId = session.getSubscription();
		String customerId = session.getCustomer();
		
		if (user.getStripeCustomerId() == null) {
			user.setStripeCustomerId(customerId);
			userRepository.save(user);
		}
		
		subscriptionService.activateSubscription(userId, planId, subscriptionId, customerId);
		
	}
	public void handleCustomerSubscriptionUpdated(Subscription subscription) {
		if (subscription == null) {
			log.error("subscription object is null");
			return;
		}
		
		SubscriptionStatus status = mapStripeStatusToEnum(subscription.getStatus());
		
		if (status == null) {
			log.warn("Unknown status '{}' for subscription '{}'", subscription.getStatus(), subscription.getId());
			
			return;
		}
		
		SubscriptionItem item = subscription.getItems().getData().get(0);
		Instant periodStart = mapToInstant(item.getCurrentPeriodStart());
		Instant periodEnd = mapToInstant(item.getCurrentPeriodEnd());
		
		Long planId = resolvePlanId(item.getPrice());
		
		subscriptionService.updateSubscription(
				subscription.getId(), 
				status, 
				periodStart,
				periodEnd, 
				planId, 
				subscription.getCancelAtPeriodEnd()
		);
	}
	
	public void handleCustomerSubscriptionDeleted(Subscription subscription) {
		if (subscription == null) {
			log.error("subscription object is null");
			return;
		}
		
		subscriptionService.cancelSubscription(subscription.getId());
	}
	
	public void handleInvoicePaid(Invoice invoice) {
		String subId = extractSubscriptionId(invoice);
		if (subId == null) return;
		Subscription subscription;
		try {
			subscription = Subscription.retrieve(subId);
			
			var item = subscription.getItems().getData().get(0);
			Instant periodStart = mapToInstant(item.getCurrentPeriodStart());
			Instant periodEnd = mapToInstant(item.getCurrentPeriodEnd());
			
			subscriptionService.renewSubscriptionPeriod(
					subId,
					periodStart,
					periodEnd
			);
			
		} catch (StripeException e) {
			
			throw new RuntimeException(e);
		}
		
		 
	}
	
	
	public void handleInvoicePaymentFailed(Invoice invoice) {
		String subId = extractSubscriptionId(invoice);
		if (subId == null) return;
		
		subscriptionService.markSubscriptionPastDue(subId);
	}
	
	// utility methods
	
	
	private User getUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found", userId));
	}
	
	private SubscriptionStatus mapStripeStatusToEnum(String status) {
		return switch (status) {
		
		case "active" -> SubscriptionStatus.ACTIVE;
		case "trailing" -> SubscriptionStatus.TRAILING;
		case "canceled" -> SubscriptionStatus.CANCELED;
		case "incomplete" -> SubscriptionStatus.INCOMPLETE;
		case "incomplete_expired", "past_due", "unpaid", "paused" -> SubscriptionStatus.PAST_DUE;
		default -> {
			log.warn("unmapped subscription status");
			yield null;
		}
		};
		
	}
	
	private Long resolvePlanId(Price price) {
		if (price == null || price.getId() == null) {
			return null;
		}
		
		return planRepository.findByStripePriceId(price.getId())
							.map(Plan::getId)
							.orElse(null);
	}



	private Instant mapToInstant(Long epoch) {
		return epoch != null ? Instant.ofEpochSecond(epoch) : null;
	}
	
	private String extractSubscriptionId(Invoice invoice) {
		if (invoice == null) return null;
		Parent parent = invoice.getParent();
		
		if (parent == null) return null;
		SubscriptionDetails subscriptionDetails = parent.getSubscriptionDetails();
		if (subscriptionDetails == null) return null;
		return subscriptionDetails.getSubscription();
	}



	
}
