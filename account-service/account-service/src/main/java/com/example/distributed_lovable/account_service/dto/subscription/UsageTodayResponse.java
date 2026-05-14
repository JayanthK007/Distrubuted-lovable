package com.example.distributed_lovable.account_service.dto.subscription;

public record UsageTodayResponse(
		Integer tokenUsed,
		Integer tokenLimit,
		Integer previewsRunning,
		Integer previewsLimit
		) {

}
