package com.example.distributed_lovable.account_service.service.impl;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import com.example.distributed_lovable.account_service.dto.subscription.SubscriptionResponse;
import com.example.distributed_lovable.account_service.entity.Plan;
import com.example.distributed_lovable.account_service.entity.Subscription;
import com.example.distributed_lovable.account_service.entity.User;
import com.example.distributed_lovable.account_service.mapper.SubscriptionMapper;
import com.example.distributed_lovable.account_service.repository.PlanRepository;
import com.example.distributed_lovable.account_service.repository.SubscriptionRepository;
import com.example.distributed_lovable.account_service.repository.UserRepository;
import com.example.distributed_lovable.account_service.service.SubscriptionService;
import com.example.distributed_lovable.common_lib.dto.PlanDto;
import com.example.distributed_lovable.common_lib.enums.SubscriptionStatus;
import com.example.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.example.distributed_lovable.common_lib.security.AuthUtil;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
	
	private final Integer FREE_TIER_PROJECTS_ALLOWED = 3;
	private final SubscriptionRepository subscriptionRepository;
	private final AuthUtil authUtil;
	private final SubscriptionMapper subscriptionMapper;
	private final UserRepository userRepository;
	private final PlanRepository planRepository;

	@Override
	public SubscriptionResponse getCurrentSubscription() {
		Long userId = authUtil.getCurrentUserId();
		var currentSubscription = subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
				 SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE,
				 SubscriptionStatus.TRAILING
				 )).orElse(new Subscription());
		
		return subscriptionMapper.toSubscriptionResponse(currentSubscription);
	}

	@Override
	public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
		boolean exist = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
		if (exist) {
			return;	
		}
		User user = getUser(userId);
		Plan plan = getPlan(planId);
		
		Subscription subscription = Subscription.builder()
				.user(user)
				.plan(plan)
				.stripeSubscriptionId(subscriptionId)
				.status(SubscriptionStatus.INCOMPLETE)
				.build();
		subscriptionRepository.save(subscription);
	}

	@Override
	public void updateSubscription(String subscriptionId,
	                               SubscriptionStatus status,
	                               Instant periodStart,
	                               Instant periodEnd,
	                               Long planId,
	                               Boolean cancelAtPeriodEnd) {

	    Subscription subscription = getSubscription(subscriptionId);
	    boolean updated = false;

	    if (!Objects.equals(subscription.getStatus(), status) && status != null) {
	        subscription.setStatus(status);
	        updated = true;
	    }

	    if (periodStart != null && !Objects.equals(subscription.getCurrentPeriodStart(), periodStart)) {
	        subscription.setCurrentPeriodStart(periodStart);
	        updated = true;
	    }

	    if (periodEnd != null && !Objects.equals(subscription.getCurrentPeriodEnd(), periodEnd)) {
	        subscription.setCurrentPeriodEnd(periodEnd);
	        updated = true;
	    }

	    if (cancelAtPeriodEnd != null &&
	        !Objects.equals(subscription.getCancelAtPeriodEnd(), cancelAtPeriodEnd)) {
	        subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
	        updated = true;
	    }

	    if (planId != null &&
	        (subscription.getPlan() == null ||
	         !Objects.equals(subscription.getPlan().getId(), planId))) {

	        Plan plan = getPlan(planId);
	        subscription.setPlan(plan);
	        updated = true;
	    }

	    if (updated) {
	        log.debug("subscription has been updated");
	        subscriptionRepository.save(subscription);
	    }
	}
	
	@Override
	public void cancelSubscription(String gatewaySubscriptionId) {
		Subscription subscription = getSubscription(gatewaySubscriptionId);
		subscription.setStatus(SubscriptionStatus.CANCELED);
		subscriptionRepository.save(subscription);
	}

	@Override
	public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
		Subscription subscription = getSubscription(gatewaySubscriptionId);
		
		Instant start = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();
		subscription.setCurrentPeriodStart(start);
		subscription.setCurrentPeriodEnd(periodEnd);
		
		if (subscription.getStatus() == SubscriptionStatus.PAST_DUE || 
				subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
			subscription.setStatus(SubscriptionStatus.ACTIVE);
		}
		
		subscriptionRepository.save(subscription);
	}


	@Override
	public void markSubscriptionPastDue(String gatewaySubscriptionId) {
		
		Subscription subscription = getSubscription(gatewaySubscriptionId);
		
		if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
			log.debug("subscription past duw for gatewaySubscrptionId: {}", gatewaySubscriptionId);
			
		}
		subscription.setStatus(SubscriptionStatus.PAST_DUE);
		subscriptionRepository.save(subscription);
	}

	@Override
	public PlanDto getCurrentSubscribedPlanByUser() {

		SubscriptionResponse subscriptionResponse = getCurrentSubscription();

		return subscriptionResponse.plan();
	}


	private Subscription getSubscription(String gatewaySubscrptionId) {
		return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscrptionId)
				.orElseThrow(() -> new ResourceNotFoundException("Subscription", Long.parseLong(gatewaySubscrptionId)));
	}
	private User getUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("user", userId));
	}
	
	private Plan getPlan(Long planId) {
		return planRepository.findById(planId)
				.orElseThrow(() -> new ResourceNotFoundException("Plan", planId));
	}

	

}
