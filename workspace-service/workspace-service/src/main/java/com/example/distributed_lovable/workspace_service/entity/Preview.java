package com.example.distributed_lovable.workspace_service.entity;

import java.time.Instant;

import com.example.distributed_lovable.common_lib.enums.PreviewStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preview {
	
	Long id;
	Project project;
	String namespace;
	PreviewStatus status;
	String podName;
	String previewURL;
	Instant startedAt;
	Instant terminatedAt;
	Instant createdAt;
	
}
