package com.example.distributed_lovable.workspace_service.mapper;


import java.util.List;

import com.example.distributed_lovable.common_lib.enums.ProjectRole;
import com.example.distributed_lovable.workspace_service.dto.project.ProjectResponse;
import com.example.distributed_lovable.workspace_service.dto.project.ProjectSumaryResponse;
import com.example.distributed_lovable.workspace_service.entity.Project;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProjectMapper {
	
	ProjectResponse toProjectResponse(Project project);
	ProjectSumaryResponse toProjectSummaryResponse(Project project, ProjectRole role);

}
