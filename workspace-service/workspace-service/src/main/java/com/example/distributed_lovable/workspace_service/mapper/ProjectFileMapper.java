package com.example.distributed_lovable.workspace_service.mapper;

import java.util.List;

import com.example.distributed_lovable.common_lib.dto.FileNode;
import com.example.distributed_lovable.workspace_service.entity.ProjectFile;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring")
public interface ProjectFileMapper {
	
	List<FileNode> toFileNode(List<ProjectFile> projectFile);
}
