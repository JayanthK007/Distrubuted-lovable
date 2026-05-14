package com.example.distributed_lovable.workspace_service.consumer;


import com.example.distributed_lovable.common_lib.event.FileStoreRequestEvent;
import com.example.distributed_lovable.common_lib.event.FileStoreResponseEvent;
import com.example.distributed_lovable.workspace_service.entity.ProcessedEvent;
import com.example.distributed_lovable.workspace_service.repository.ProcessedEventRepository;
import com.example.distributed_lovable.workspace_service.service.ProjectFileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageConsumer {

    private final ProjectFileService projectFileService;
    private final ProcessedEventRepository processedEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Transactional
    @KafkaListener(topics = "file-storage-request-event", groupId = "workspace-group")
    public void consumeEvent(FileStoreRequestEvent fileStoreRequestEvent) {

        if (processedEventRepository.existsById(fileStoreRequestEvent.sagaId())) {
            log.info("Duplicate Saga Detected: {}, Resending previous Ack.", fileStoreRequestEvent.sagaId());
            sendResponse(fileStoreRequestEvent, true, null);
            return;
        }
        try {
            log.info("Saving file to {}", fileStoreRequestEvent.filePath());
            projectFileService.saveFile(fileStoreRequestEvent.projectId(), fileStoreRequestEvent.filePath(), fileStoreRequestEvent.content());
            processedEventRepository.save(new ProcessedEvent(fileStoreRequestEvent.sagaId(), LocalDateTime.now()));
            sendResponse(fileStoreRequestEvent, true, null);
        } catch (Exception e) {
            log.error("Error Saving file: {}", e.getMessage());
            sendResponse(fileStoreRequestEvent, false, e.getMessage());
        }
    }

    private void sendResponse(FileStoreRequestEvent fileStoreRequestEvent, boolean success, String error) {
        FileStoreResponseEvent responseEvent = FileStoreResponseEvent.builder()
                .sagaId(fileStoreRequestEvent.sagaId())
                .projectId(fileStoreRequestEvent.projectId())
                .success(success)
                .errorMessage(error)
                .build();

        kafkaTemplate.send("file-store-reponses", responseEvent);

    }
}
