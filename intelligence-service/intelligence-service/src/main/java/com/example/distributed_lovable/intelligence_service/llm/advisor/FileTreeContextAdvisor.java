package com.example.distributed_lovable.intelligence_service.llm.advisor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.distributed_lovable.common_lib.dto.FileNode;
import com.example.distributed_lovable.common_lib.dto.FileTreeDto;
import com.example.distributed_lovable.intelligence_service.client.WorkspaceClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor{
	
	private  final WorkspaceClient workspaceClient;
	@Override
	public String getName() {
		return "FileTreeContextAdvisor";
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Flux<ChatClientResponse> adviseStream(ChatClientRequest request,
			StreamAdvisorChain streamAdvisorChain) {
		Map<String, Object> context = request.context();
		
		Long projectId = Long.parseLong(context.getOrDefault("projectId", 0L).toString());
		
		ChatClientRequest augmentedChatClientRequest = augmentRequestWithFileTree(request, projectId);
		
		return streamAdvisorChain.nextStream(augmentedChatClientRequest);
 	}

	private ChatClientRequest augmentRequestWithFileTree(ChatClientRequest request, Long projectId) {
		List<Message> incomingMessages = request.prompt().getInstructions();
		
		Message systemMessage = incomingMessages.stream()
								.filter(m -> m.getMessageType() == MessageType.SYSTEM)
								.findFirst()
								.orElse(null);
		
		List<Message> userMessage = incomingMessages.stream()
									.filter(m -> m.getMessageType() != MessageType.SYSTEM)
									.toList();
		
		List<Message> allMessages = new ArrayList<>();
		
		if (systemMessage != null) {
			allMessages.add(systemMessage);
		}
		
		
		List<FileNode> fileTree = workspaceClient.getFileTree(projectId).files();
		
		String fileTreeContext = "\n\n------- File Tree  ----------\n\n" + fileTree.toString();
		allMessages.add(new SystemMessage(fileTreeContext));
		
		allMessages.addAll(userMessage);
		
		return request.mutate()
				.prompt(new Prompt(allMessages, request.prompt().getOptions()))
				.build();
		
	}
	
	

}
