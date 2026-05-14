package com.example.distributed_lovable.account_service.service;

import java.time.Instant;


import com.example.distributed_lovable.account_service.dto.subscription.SubscriptionResponse;
import com.example.distributed_lovable.common_lib.dto.PlanDto;
import com.example.distributed_lovable.common_lib.enums.SubscriptionStatus;


public interface SubscriptionService {

	SubscriptionResponse getCurrentSubscription();

	void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

	void updateSubscription(String id, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Long planId,
	                        Boolean cancelAtPeriodEnd);

	void cancelSubscription(String gatewaySubscriptionId);

	void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd);

	void markSubscriptionPastDue(String gatewaySubscriptionId);


    PlanDto getCurrentSubscribedPlanByUser();
}
