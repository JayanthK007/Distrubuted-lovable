package com.example.distributed_lovable.account_service.entity;

import java.time.Instant;

import com.example.distributed_lovable.common_lib.enums.SubscriptionStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscription")
public class Subscription {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "user_id")
	User user;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "plan_id")
	Plan plan;

	String stripeSubscriptionId;
	
	@Enumerated(EnumType.STRING)
	SubscriptionStatus status;
	
	Instant currentPeriodStart;
	
	Instant currentPeriodEnd;
	
	Boolean cancelAtPeriodEnd = false;
	
	@CreationTimestamp
	Instant createdAt;
	
	@UpdateTimestamp
	Instant updatedAt;
}
