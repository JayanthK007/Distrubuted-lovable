package com.example.distributed_lovable.account_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	String name;
	
	@Column(unique = true)
	String stripePriceId;
	
	Integer maxProjects;
	
	Integer maxTokensPerDay;
	
	Integer maxPreviews;
	
	Boolean unlimitedAi;
	
	Boolean active;

}
