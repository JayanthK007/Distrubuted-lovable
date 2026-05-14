package com.example.distributed_lovable.workspace_service.service;

import com.example.distributed_lovable.workspace_service.dto.member.InviteMemberRequest;
import com.example.distributed_lovable.workspace_service.dto.member.MemberResponse;
import com.example.distributed_lovable.workspace_service.dto.member.UpdateMemberRoleRequest;

import java.util.List;




public interface ProjectMemberService {

	List<MemberResponse> getProjectMembers(Long projectId);

	MemberResponse inviteMemberByEmail(Long projectId, InviteMemberRequest request);

	MemberResponse updateMemberRole(Long memberId, Long projectId, UpdateMemberRoleRequest request);

	void removeProjectMember(Long memberId, Long projectId);

}
