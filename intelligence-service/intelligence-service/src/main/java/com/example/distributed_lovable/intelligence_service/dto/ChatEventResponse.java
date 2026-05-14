package com.example.distributed_lovable.intelligence_service.dto;

import com.example.distributed_lovable.common_lib.enums.ChatEventType;


public record ChatEventResponse(
	
		Long id,
		ChatEventType chatEventType,
		Integer sequenceOrder,
		String content,
		String filePath,
		String metadata
		
		) {

}
