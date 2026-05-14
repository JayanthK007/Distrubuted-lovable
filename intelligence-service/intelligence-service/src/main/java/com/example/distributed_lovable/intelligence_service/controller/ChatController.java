package com.example.distributed_lovable.intelligence_service.controller;

import java.util.List;

import com.example.distributed_lovable.intelligence_service.dto.ChatRequest;
import com.example.distributed_lovable.intelligence_service.dto.ChatResponse;
import com.example.distributed_lovable.intelligence_service.dto.StreamResponse;
import com.example.distributed_lovable.intelligence_service.service.AiGenerationService;
import com.example.distributed_lovable.intelligence_service.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
	
	private final AiGenerationService aiGenerationService;
	private final ChatService chatService;
	
	
	@PostMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<StreamResponse>> streamChat(
			@RequestBody ChatRequest request) {
		
		
		return aiGenerationService.streamResponse(request.message(), request.projectId())
				.map(data -> ServerSentEvent.<StreamResponse>builder()
						.data(data)
						.build());
				
	}
	
	@GetMapping("/projects/{projectId}")
	public ResponseEntity<List<ChatResponse>> getChatHistory(@PathVariable Long projectId) {
		return ResponseEntity.ok(chatService.getProjectChatHistory(projectId));
	}

}
