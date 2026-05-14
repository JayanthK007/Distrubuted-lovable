package com.example.distributed_lovable.workspace_service.controller;

import java.util.List;

import com.example.distributed_lovable.workspace_service.dto.member.InviteMemberRequest;
import com.example.distributed_lovable.workspace_service.dto.member.MemberResponse;
import com.example.distributed_lovable.workspace_service.dto.member.UpdateMemberRoleRequest;
import com.example.distributed_lovable.workspace_service.service.ProjectMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/members")
public class ProjectMemberController {
	
	private final ProjectMemberService projectMemberService;
	
	@GetMapping
	public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId){
		return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId));
	}
	
	
	@PostMapping
	public ResponseEntity<MemberResponse> inviteMemberByEmail(@PathVariable Long projectId, @RequestBody @Valid InviteMemberRequest request){
		return ResponseEntity.status(HttpStatus.CREATED).body(projectMemberService.inviteMemberByEmail(projectId,request));
	}
	
	@PatchMapping("/{memberId}")
	public ResponseEntity<MemberResponse> updateMemberRole(@PathVariable Long memberId, @PathVariable Long projectId, 
			@RequestBody @Valid UpdateMemberRoleRequest request ){
		return ResponseEntity.ok(projectMemberService.updateMemberRole(memberId,projectId,request));
	}
	
	@DeleteMapping("/{memberId}")
	public ResponseEntity<Void> removeProjectMember(@PathVariable Long memberId, @PathVariable Long projectId){
		projectMemberService.removeProjectMember(memberId,projectId);
		return ResponseEntity.noContent().build();
	}
					
}
