package com.example.distributed_lovable.workspace_service.service;

import com.example.distributed_lovable.workspace_service.dto.project.DeploymentResponse;

public interface DeploymentService {
	
	DeploymentResponse deploy(Long projectId);

}
