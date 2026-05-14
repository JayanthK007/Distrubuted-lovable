package com.example.distributed_lovable.workspace_service.repository;

import java.util.List;
import java.util.Optional;

import com.example.distributed_lovable.workspace_service.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long>{
	
	Optional<ProjectFile> findByProjectIdAndPath(Long projectId, String path);

	List<ProjectFile> findByProjectId(Long projectId);
}
