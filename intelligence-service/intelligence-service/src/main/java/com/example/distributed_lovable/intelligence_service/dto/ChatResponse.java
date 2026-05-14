package com.example.distributed_lovable.intelligence_service.dto;

import java.time.Instant;
import java.util.List;

import com.example.distributed_lovable.common_lib.enums.MessageRole;


public record ChatResponse(

		
		Long id,
		
		String content,
		
		MessageRole role,
		
		List<ChatEventResponse> events,
		
		Integer tokensUsed,
		
		Instant createdAt

		) {

}
