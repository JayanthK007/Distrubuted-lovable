package com.example.distributed_lovable.workspace_service.service.impl;

import java.time.Instant;
import java.util.List;

import com.example.distributed_lovable.common_lib.dto.UserDto;
import com.example.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.example.distributed_lovable.common_lib.security.AuthUtil;
import com.example.distributed_lovable.workspace_service.client.AccountClient;
import com.example.distributed_lovable.workspace_service.dto.member.InviteMemberRequest;
import com.example.distributed_lovable.workspace_service.dto.member.MemberResponse;
import com.example.distributed_lovable.workspace_service.dto.member.UpdateMemberRoleRequest;
import com.example.distributed_lovable.workspace_service.entity.Project;
import com.example.distributed_lovable.workspace_service.entity.ProjectMember;
import com.example.distributed_lovable.workspace_service.entity.ProjectMemberId;
import com.example.distributed_lovable.workspace_service.mapper.ProjectMemberMapper;
import com.example.distributed_lovable.workspace_service.repository.ProjectMembeRepository;
import com.example.distributed_lovable.workspace_service.repository.ProjectRepository;
import com.example.distributed_lovable.workspace_service.service.ProjectMemberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;



import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {
	
	ProjectMembeRepository projectMembeRepository;
	ProjectRepository projectRepository;
	ProjectMemberMapper projectMemberMapper;
	AuthUtil authUtil;
	AccountClient accountClient;

	@Override
	@PreAuthorize("@security.canViewMembers(#projectId)")
	public List<MemberResponse> getProjectMembers(Long projectId) {
		
		return projectMembeRepository.findByIdProjectId(projectId)
				.stream()
				.map(projectMemberMapper::toMemberResponseFromMembers)
				.toList();
		
	}

	@Override
	@PreAuthorize("@security.canManageMembers(#projectId)")
	public MemberResponse inviteMemberByEmail(Long projectId, InviteMemberRequest request) {

	    Long userId = authUtil.getCurrentUserId();
	    
	    Project project = getAccessibleProjectById(projectId, userId);

	    UserDto invitee = accountClient.getUserByEmail(request.username())
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(request.username(), userId));

	    if (invitee.id().equals(userId)) {
	        throw new RuntimeException("Cannot invite yourself");
	    }

	    ProjectMemberId projectMemberId =
	            new ProjectMemberId(projectId, invitee.id());

	    if (projectMembeRepository.existsById(projectMemberId)) {
	        throw new RuntimeException("User is already a member");
	    }

	    ProjectMember member = ProjectMember.builder()
	            .id(projectMemberId)
	            .project(project)
	            .role(request.role())
	            .invitedAt(Instant.now())
	            .build();

	    projectMembeRepository.save(member);

	    return projectMemberMapper.toMemberResponseFromMembers(member);
	}

	@Override
	@PreAuthorize("@security.canManageMembers(#projectId)")
	public MemberResponse updateMemberRole(Long memberId, Long projectId, UpdateMemberRoleRequest request) {
		
		ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
		
		ProjectMember projectMember = projectMembeRepository.findById(projectMemberId)
				.orElseThrow(() -> 
				new RuntimeException("Project with id " + projectMemberId.getProjectId() + " with user id" + projectMemberId.getUserId() + " not found"));
		
		projectMember.setRole(request.role());
		
		projectMember = projectMembeRepository.save(projectMember);
		
		return projectMemberMapper.toMemberResponseFromMembers(projectMember);
	}

	@Override
	@PreAuthorize("@security.canManageMembers(#projectId)")
	public void removeProjectMember(Long memberId, Long projectId) {
		
		ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
		
		if (!projectMembeRepository.existsById(projectMemberId)) {
			throw new RuntimeException("member does not exist");
		}
		
		projectMembeRepository.deleteById(projectMemberId);
		
	}
	
	public Project getAccessibleProjectById(Long id, Long userId) {
		
		return projectRepository.findAccessibleProjectById(userId, id).orElseThrow();
	}

}
