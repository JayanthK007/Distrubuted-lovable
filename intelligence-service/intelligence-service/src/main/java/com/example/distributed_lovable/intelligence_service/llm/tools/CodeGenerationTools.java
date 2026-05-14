package com.example.distributed_lovable.intelligence_service.llm.tools;

import java.util.ArrayList;
import java.util.List;

import com.example.distributed_lovable.intelligence_service.client.WorkspaceClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CodeGenerationTools {
	
	private final Long projectId;
	private final WorkspaceClient workspaceClient;
	
	@Tool(
			name = "read_files",
			description = "Read the content of files. Only input the file name present inside the FILE_TREE. Do not input any path which is not present under the FILE_TREE"
			)
	public List<String> readFiles(
			@ToolParam(description = "List of relative path (e.g. ['src/App.jsx'])")
			List<String> paths
	) {
		
		List<String> results = new ArrayList<String>();
		
		for (String path : paths) {
			String cleanPath = path.startsWith("/") ? path.substring(1) : path;
			
			String content = workspaceClient.getFileContent(projectId, path);
			
			results.add(String.format(
					"--- Start of File: %s ---\n%s\n--- End of File ---",
					cleanPath, content));
			
			
		}
		
			return results;
		
	}
	

}
