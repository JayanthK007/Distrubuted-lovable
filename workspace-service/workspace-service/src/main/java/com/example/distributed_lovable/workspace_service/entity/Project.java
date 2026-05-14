package com.example.distributed_lovable.workspace_service.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "projects",
	indexes = {
			@Index(name = "idx_projects_updated_at_desc", columnList = "updated_at DESC, deleted_at"),
			@Index(name = "idx_projects_deleted_at", columnList = "deleted_at")
	}
		
		)
public class Project {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@Column(nullable = false)
	String name;
	
	
	Boolean isPublic;
	
	@CreationTimestamp
	Instant CreatedAt;
	
	@UpdateTimestamp
	Instant updatedAt;
	
	Instant deletedAt;
	
}
