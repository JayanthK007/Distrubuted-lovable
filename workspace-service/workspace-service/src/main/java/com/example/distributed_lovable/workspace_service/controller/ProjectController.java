package com.example.distributed_lovable.workspace_service.controller;

import java.util.List;

import com.example.distributed_lovable.workspace_service.dto.project.DeploymentResponse;
import com.example.distributed_lovable.workspace_service.dto.project.ProjectRequest;
import com.example.distributed_lovable.workspace_service.dto.project.ProjectResponse;
import com.example.distributed_lovable.workspace_service.dto.project.ProjectSumaryResponse;
import com.example.distributed_lovable.workspace_service.service.DeploymentService;
import com.example.distributed_lovable.workspace_service.service.ProjectService;
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
@RequestMapping("/projects")
public class ProjectController {
	
	private final ProjectService projectService;
	private final DeploymentService deploymentService;
	
	
	@GetMapping
	public ResponseEntity<List<ProjectSumaryResponse>> getMyProjects(){
		
		return ResponseEntity.ok(projectService.getUserProjects());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProjectSumaryResponse> getProjectById(@PathVariable Long id){
		return ResponseEntity.ok(projectService.getProjectById(id));
		
	}
	
	@PostMapping
	public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest request){
		return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
	}
	
	
	@PatchMapping("/{id}")
	public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,@RequestBody @Valid ProjectRequest request){
		return ResponseEntity.ok(projectService.updateProject(id,request));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ProjectResponse> deleteProject(@PathVariable Long id){
		projectService.softDelete(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/{id}/deploy")
	public ResponseEntity<DeploymentResponse> deployProject(@PathVariable Long id) {
		return ResponseEntity.ok(deploymentService.deploy(id));
	}

}
