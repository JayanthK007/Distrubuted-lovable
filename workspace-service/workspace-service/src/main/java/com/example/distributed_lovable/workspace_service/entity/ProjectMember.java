package com.example.distributed_lovable.workspace_service.entity;

import java.time.Instant;

import com.example.distributed_lovable.common_lib.enums.ProjectRole;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project_members")
public class ProjectMember {
	@EmbeddedId
	ProjectMemberId id;
	
	@ManyToOne
	@MapsId("projectId")
	Project project;

	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	ProjectRole role;
	
	
	Instant invitedAt;
	Instant acceptedAt;
}
