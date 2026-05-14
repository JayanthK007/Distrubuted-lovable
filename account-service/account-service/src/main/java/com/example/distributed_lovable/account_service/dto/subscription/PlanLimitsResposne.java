package com.example.distributed_lovable.account_service.dto.subscription;

public record PlanLimitsResposne(
		String planName,
		Integer maxTokensPerDay,
		Integer maxProjects,
		Boolean unlimitedAi) {

}
