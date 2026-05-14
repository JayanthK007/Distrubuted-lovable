package com.example.distributed_lovable.workspace_service.security;

import com.example.distributed_lovable.common_lib.enums.ProjectPermission;
import com.example.distributed_lovable.common_lib.security.AuthUtil;
import com.example.distributed_lovable.workspace_service.repository.ProjectMembeRepository;
import org.springframework.stereotype.Component;


import lombok.RequiredArgsConstructor;

@Component("security")
@RequiredArgsConstructor
public class SecurityExpressions {
	
	private final ProjectMembeRepository projectMembeRepository;
	private final AuthUtil authUtil;
	
	public boolean hasPermission(Long projectId, ProjectPermission permission) {
		Long userId = authUtil.getCurrentUserId();
		
		boolean flag = projectMembeRepository.findRoleByUserIdAndProjectId(userId, projectId)
				.map(role -> role.getPermissions().contains(permission))
				.orElse(false);
		return flag;
	}

	public boolean canViewProject(Long projectId) {
		return hasPermission(projectId, ProjectPermission.VIEW);
	}
	
	public boolean canEditProject(Long projectId) {
		return hasPermission(projectId, ProjectPermission.EDIT);
	}
	
	public boolean canDeleteProject(Long projectId) {
		return hasPermission(projectId, ProjectPermission.DELETE);
	}
	
	public boolean canViewMembers(Long projectId) {
		return hasPermission(projectId, ProjectPermission.VIEW_MEMBERS);
	}
	
	public boolean canManageMembers(Long projectId) {
		return hasPermission(projectId, ProjectPermission.MANAGE_MEMBERS);
	}
	
}
