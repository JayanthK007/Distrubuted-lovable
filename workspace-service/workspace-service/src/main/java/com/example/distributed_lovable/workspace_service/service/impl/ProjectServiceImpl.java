package com.example.distributed_lovable.workspace_service.service.impl;

import java.time.Instant;
import java.util.List;

import com.example.distributed_lovable.common_lib.dto.PlanDto;
import com.example.distributed_lovable.common_lib.dto.UserDto;
import com.example.distributed_lovable.common_lib.enums.ProjectPermission;
import com.example.distributed_lovable.common_lib.enums.ProjectRole;
import com.example.distributed_lovable.common_lib.error.BadRequestException;
import com.example.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.example.distributed_lovable.common_lib.security.AuthUtil;
import com.example.distributed_lovable.workspace_service.client.AccountClient;
import com.example.distributed_lovable.workspace_service.dto.project.ProjectRequest;
import com.example.distributed_lovable.workspace_service.dto.project.ProjectResponse;
import com.example.distributed_lovable.workspace_service.dto.project.ProjectSumaryResponse;
import com.example.distributed_lovable.workspace_service.entity.Project;
import com.example.distributed_lovable.workspace_service.entity.ProjectMember;
import com.example.distributed_lovable.workspace_service.entity.ProjectMemberId;
import com.example.distributed_lovable.workspace_service.mapper.ProjectMapper;
import com.example.distributed_lovable.workspace_service.repository.ProjectFileRepository;
import com.example.distributed_lovable.workspace_service.repository.ProjectMembeRepository;
import com.example.distributed_lovable.workspace_service.repository.ProjectRepository;
import com.example.distributed_lovable.workspace_service.security.SecurityExpressions;
import com.example.distributed_lovable.workspace_service.service.ProjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;



import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
@Slf4j
public class ProjectServiceImpl implements ProjectService {
	
	ProjectRepository projectRepository;
	ProjectMapper projectMapper;
	ProjectMembeRepository memberRepository;
	AuthUtil authUtil;
	ProjectTemplateServiceImpl projectTemplateServiceImpl;
	AccountClient accountClient;
	ProjectMembeRepository projectMembeRepository;
	SecurityExpressions securityExpressions;
	
	@Override
	public ProjectResponse createProject(ProjectRequest request) {
		
		if (!canCreateNewProject()) {
			throw new BadRequestException("User cannot create a new project with current plan. Upgrade plan now");
		}
		
		Long userId = authUtil.getCurrentUserId();

		
		Project project = Project.builder()
							.name(request.name())
							.isPublic(false)
							.build();
		
		project = projectRepository.save(project);
		
		ProjectMemberId id = new ProjectMemberId(project.getId(), userId);
		
		ProjectMember member = ProjectMember.builder()
									.id(id)	
									.role(ProjectRole.OWNER)
									.acceptedAt(Instant.now())
									.invitedAt(Instant.now())
									.project(project)
									.build();
		
		memberRepository.save(member);
		
		
		projectTemplateServiceImpl.initializeProjectFromTemplate(project.getId());
		
		return projectMapper.toProjectResponse(project);
						
	}

	@Override
	public List<ProjectSumaryResponse> getUserProjects() {
		Long userId = authUtil.getCurrentUserId();
		var projectWithRole = projectRepository.findAllAccessibleProject(userId);
		return projectWithRole.stream()
								.map(p -> projectMapper.toProjectSummaryResponse(p.getProject(), p.getRole()))
								.toList();
	}

	@Override
	@PreAuthorize("@security.canViewProject(#projectId)")
	public ProjectSumaryResponse getProjectById(Long projectId) {
		 Long userId = authUtil.getCurrentUserId();
		 ProjectRepository.ProjectWithRole projectWithRole = projectRepository.findAccessibleProjectByIdWithRole(userId, projectId)
				 															.orElseThrow(() -> new BadRequestException("project not found"));
		 return projectMapper.toProjectSummaryResponse(projectWithRole.getProject(), projectWithRole.getRole());
		 
	}


	@Override
	@PreAuthorize("@security.canEditProject(#projectId)")
	public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
		Long userId = authUtil.getCurrentUserId();
		Project project = getAccessibleProjectById(projectId, userId);
				
		
		project.setName(request.name());
		project = projectRepository.save(project);
		return projectMapper.toProjectResponse(project);
	}

	@Override
	@PreAuthorize("@security.canDeleteProject(#projectId)")
	public void softDelete(Long projectId) {
		Long userId = authUtil.getCurrentUserId();
		Project project = getAccessibleProjectById(projectId, userId);
		
		
		project.setDeletedAt(Instant.now());
		
		projectRepository.save(project);
		
		
	}

	@Override
	public boolean hasPermission(Long projectId, ProjectPermission permission) {
		return securityExpressions.hasPermission(projectId,permission);
	}

	public Project getAccessibleProjectById(Long id, Long userId) {
		
		return projectRepository.findAccessibleProjectById(userId, id)
				.orElseThrow(() -> new ResourceNotFoundException("Project", id));
	}

	private boolean canCreateNewProject() {
		Long userId = authUtil.getCurrentUserId();

		if (userId == null) {
			return false;
		}
		PlanDto plan = accountClient.getCurrentSubscribedPlanByUser();

		int maxAllowedProject = plan.maxProjects();

		int ownedProject = projectMembeRepository.countProjectOwnedByUser(userId);

		return ownedProject < maxAllowedProject;
	}
}
