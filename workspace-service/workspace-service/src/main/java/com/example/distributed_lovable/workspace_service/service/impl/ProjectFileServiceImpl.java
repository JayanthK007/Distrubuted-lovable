package com.example.distributed_lovable.workspace_service.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import com.example.distributed_lovable.common_lib.dto.FileNode;
import com.example.distributed_lovable.common_lib.dto.FileTreeDto;
import com.example.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.example.distributed_lovable.workspace_service.dto.project.FileContentResponse;
import com.example.distributed_lovable.workspace_service.dto.project.FileTreeResponse;
import com.example.distributed_lovable.workspace_service.entity.Project;
import com.example.distributed_lovable.workspace_service.entity.ProjectFile;
import com.example.distributed_lovable.workspace_service.mapper.ProjectFileMapper;
import com.example.distributed_lovable.workspace_service.repository.ProjectFileRepository;
import com.example.distributed_lovable.workspace_service.repository.ProjectRepository;
import com.example.distributed_lovable.workspace_service.service.ProjectFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {
	
	@Value("${minio.project-bucket}")
	private String bucket;
	
	private final ProjectRepository projectRepository;
	private final MinioClient  minioClient;
	private final ProjectFileRepository projectFileRepository;
	private final ProjectFileMapper projectFileMapper;
	
	private static final String BUCKET_NAME = "project";

	@Override
	public FileTreeDto getFileTree(Long projectId) {
		List<ProjectFile> projectFiles = projectFileRepository.findByProjectId(projectId);
		
		List<FileNode> fileNodes = projectFileMapper.toFileNode(projectFiles);
		return new FileTreeDto(fileNodes);
	}

	@Override
	public String getFileContent(Long projectId, String path) {
		String objectName = projectId + "/" + path;
		
		try {
			
			InputStream is = minioClient.getObject(
					GetObjectArgs.builder()
					.bucket(BUCKET_NAME)
					.object(objectName)
					.build()
					
					);
			
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);

		} catch (Exception e) {
			log.error("Failed to read file: {}/{}",projectId, path, e );
			throw new RuntimeException("Failed to read file content", e);
		}
	}

	@Override
	public void saveFile(Long projectId, String filePath, String content) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
		
		String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
		
		String objectKey = projectId + "/" + cleanPath;
		
		try {
			byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
			InputStream inputStream = new ByteArrayInputStream(contentBytes);
			
			
			minioClient.putObject(
					PutObjectArgs.builder()
						.bucket(bucket)
						.object(objectKey)
						.stream(inputStream, (long) contentBytes.length, -1L)
						.contentType(determineContentType(cleanPath))
						.build()
					);
			
			ProjectFile projectFile = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
										.orElseGet(() -> ProjectFile.builder()
														.project(project)
														.minioObjectKey(objectKey)
														.path(cleanPath)
														.createdAt(Instant.now())
														.build()
												);
			projectFile.setUpdatedAt(Instant.now());
			projectFileRepository.save(projectFile);
			
			log.info("saved file: {}", objectKey);
			
		} catch (Exception e) {
			log.error("Failed to save file {}/{}", projectId, cleanPath, e);
			throw new RuntimeException("File save failed", e);
		}
		
		
	}
	
	private String determineContentType(String path) {
		String type = URLConnection.guessContentTypeFromName(path);
		if (type != null) return type;
		if (path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) return "text/javascript";
		if (path.endsWith(".json")) return "application/json";
		if (path.endsWith(".css")) return "text/css";
		
		return "text/plain";
	}

	
	
}
