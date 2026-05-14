package com.example.distributed_lovable.intelligence_service.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_sessions")
public class ChatSession {
	
		@EmbeddedId
		ChatSessionId id;


		@CreationTimestamp
		@Column(nullable = false, updatable = false)
		Instant createdAt;
		
		
		@UpdateTimestamp
		Instant updatedAt;
		
		
		Instant deletedAt; // soft delete
}
