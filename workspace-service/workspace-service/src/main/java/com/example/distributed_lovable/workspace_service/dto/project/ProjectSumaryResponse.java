package com.example.distributed_lovable.workspace_service.dto.project;

import java.time.Instant;

import com.example.distributed_lovable.common_lib.enums.ProjectRole;

public record ProjectSumaryResponse(
		Long id,
		String name,
		Instant createdAt,
		Instant updatedAt,
		ProjectRole role
		
		) {

}
