package com.example.distributed_lovable.workspace_service.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.example.distributed_lovable.workspace_service.entity.Project;
import com.example.distributed_lovable.workspace_service.entity.ProjectFile;
import com.example.distributed_lovable.workspace_service.repository.ProjectFileRepository;
import com.example.distributed_lovable.workspace_service.repository.ProjectRepository;
import com.example.distributed_lovable.workspace_service.service.ProjectTemplateService;
import org.springframework.stereotype.Service;


import io.minio.CopyObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import io.minio.CopySource;

@Service
@RequiredArgsConstructor
public class ProjectTemplateServiceImpl implements ProjectTemplateService {
	
	private final MinioClient minioClient;
	private final ProjectFileRepository projectFileRepository;
	private final ProjectRepository projectRepository;
	
	private static final String TEMPLATE_BUCKET = "starter-projects";
	private static final String TARGET_BUCKET = "project";
	private static final String TEMPLATE_NAME = "react-vite-tailwind-daisyui-starter-main";
	
	
	@Override
	public void initializeProjectFromTemplate(Long projectId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
		
		try {
			
			Iterable<Result<Item>> results = minioClient.listObjects(
					ListObjectsArgs.builder()
								.bucket(TEMPLATE_BUCKET)
								.prefix(TEMPLATE_NAME + "/")
								.recursive(true)
								.build()
						);
			
			List<ProjectFile> filesToSave = new ArrayList<ProjectFile>();
			
			for (Result<Item> result : results) {
				Item item = result.get();
				
				String sourceKey = item.objectName();
				
				String cleanPath = sourceKey.replaceFirst(TEMPLATE_NAME + "/", "");
				
				String destkey = projectId + "/" + cleanPath;
				
				
				minioClient.copyObject(
						CopyObjectArgs.builder()
										.bucket(TARGET_BUCKET)
										.object(destkey)
										.source(
												CopySource.builder()
												.bucket(TEMPLATE_BUCKET)
												.object(sourceKey)
												.build()
												)
										.build()
						
						);
				
				
				
				ProjectFile pf = ProjectFile.builder()
											.project(project)
											.path(cleanPath)
											.minioObjectKey(destkey)
											.createdAt(Instant.now())
											.updatedAt(Instant.now())
											.build();
				
				filesToSave.add(pf);
			}
			
			projectFileRepository.saveAll(filesToSave);
			
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize project from template", e);
		}
	}

}
