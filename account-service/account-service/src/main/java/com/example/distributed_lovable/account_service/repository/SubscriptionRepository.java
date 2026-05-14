package com.example.distributed_lovable.account_service.repository;

import java.util.Optional;
import java.util.Set;

import com.example.distributed_lovable.account_service.entity.Subscription;
import com.example.distributed_lovable.common_lib.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;



public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{

	Optional<Subscription> findByUserIdAndStatusIn(Long userId, Set<SubscriptionStatus> statusSet);

	boolean existsByStripeSubscriptionId(String subscriptionId);
	
	Optional<Subscription> findByStripeSubscriptionId(String subscriptionId);
}
