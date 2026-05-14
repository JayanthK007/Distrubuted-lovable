package com.example.distributed_lovable.workspace_service.repository;

import java.util.List;
import java.util.Optional;

import com.example.distributed_lovable.common_lib.enums.ProjectRole;
import com.example.distributed_lovable.workspace_service.entity.ProjectMember;
import com.example.distributed_lovable.workspace_service.entity.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface ProjectMembeRepository extends JpaRepository<ProjectMember, ProjectMemberId>{
	
	List<ProjectMember> findByIdProjectId(Long projectId);

	@Query("""
			Select pm.role from ProjectMember pm
			WHERE pm.id.userId = :userId
			AND pm.id.projectId = :projectId
			""")
	Optional<ProjectRole> findRoleByUserIdAndProjectId(@Param("userId") Long userId,
	                                                   @Param("projectId")	Long projectId);
	
	
	@Query("""
			SELECT COUNT(pm) FROM ProjectMember pm
			WHERE pm.id.userId = :userId
			AND pm.role = 'OWNER'
			""")
	int countProjectOwnedByUser(@Param("userId") Long userId);
}
