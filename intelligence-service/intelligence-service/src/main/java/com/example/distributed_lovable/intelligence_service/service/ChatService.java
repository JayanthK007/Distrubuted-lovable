package com.example.distributed_lovable.intelligence_service.service;

import java.util.List;

import com.example.distributed_lovable.intelligence_service.dto.ChatResponse;


public interface ChatService {

	List<ChatResponse> getProjectChatHistory(Long projectId);
}
