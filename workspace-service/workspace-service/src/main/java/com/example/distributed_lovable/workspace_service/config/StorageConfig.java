package com.example.distributed_lovable.workspace_service.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;
import lombok.Data;

@Configuration
@Data
@ConfigurationProperties(prefix = "minio")
public class StorageConfig {

	String accessKey;

	String secretKey;

	String url;
	
	@Bean
	public MinioClient minioClient() {
		return MinioClient.builder()
				.credentials(accessKey, secretKey)
				.endpoint(url)
				.build();
	}

}
