package com.example.distributed_lovable.intelligence_service.entity;


import java.time.Instant;
import java.util.List;

import com.example.distributed_lovable.common_lib.enums.MessageRole;
import org.hibernate.annotations.CreationTimestamp;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_message")
public class ChatMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns({
		@JoinColumn(name = "project_id", referencedColumnName = "projectId", nullable = false),
		@JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
	})
	ChatSession chatSession;
	
	@Column(columnDefinition = "text")
	String content;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	MessageRole role;
	
	@OneToMany(mappedBy = "chatMessage", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OrderBy("sequenceOrder ASC")
	List<ChatEvent> events;
	
	
	Integer tokensUsed = 0;
	
	@CreationTimestamp
	Instant createdAt;
	
}
