package com.example.distributed_lovable.intelligence_service.repository;

import java.util.List;

import com.example.distributed_lovable.intelligence_service.entity.ChatMessage;
import com.example.distributed_lovable.intelligence_service.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{
	@Query("""
			SELECT DISTINCT m FROM ChatMessage m
            LEFT JOIN FETCH m.events e
            WHERE m.chatSession = :chatSession
            ORDER BY m.createdAt ASC
			""")
	List<ChatMessage> findByChatSession(ChatSession chatSession);

}
