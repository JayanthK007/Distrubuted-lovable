package com.example.distributed_lovable.workspace_service.dto.member;

import java.time.Instant;

import com.example.distributed_lovable.common_lib.enums.ProjectRole;

public record MemberResponse(
		Long userId,
		ProjectRole role,
		Instant invitedAt) {

}
