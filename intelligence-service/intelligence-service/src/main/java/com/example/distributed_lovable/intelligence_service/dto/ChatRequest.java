package com.example.distributed_lovable.intelligence_service.dto;

public record ChatRequest(
		String message,
		Long projectId) {

}
