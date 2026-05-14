package com.example.distributed_lovable.intelligence_service.service.impl;

import java.util.List;

import com.example.distributed_lovable.common_lib.security.AuthUtil;
import com.example.distributed_lovable.intelligence_service.dto.ChatResponse;
import com.example.distributed_lovable.intelligence_service.entity.ChatMessage;
import com.example.distributed_lovable.intelligence_service.entity.ChatSession;
import com.example.distributed_lovable.intelligence_service.entity.ChatSessionId;
import com.example.distributed_lovable.intelligence_service.mapper.ChatMapper;
import com.example.distributed_lovable.intelligence_service.repository.ChatMessageRepository;
import com.example.distributed_lovable.intelligence_service.repository.ChatSessionRepository;
import com.example.distributed_lovable.intelligence_service.service.ChatService;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
	
	private final ChatMessageRepository chatMessageRepository;
	private final ChatSessionRepository chatSessionRepository;
	private final AuthUtil authUtil;
	private final ChatMapper chatMapper;

	@Override
	public List<ChatResponse> getProjectChatHistory(Long projectId) {
		Long userId = authUtil.getCurrentUserId();
		
		
		ChatSession chatSession = chatSessionRepository.getReferenceById(
				new ChatSessionId(userId, projectId)
				);
		
		List<ChatMessage> chatMessages = chatMessageRepository.findByChatSession(chatSession);
		
		return chatMapper.toListOfChatResponse(chatMessages);
		
	}
	
	

}
