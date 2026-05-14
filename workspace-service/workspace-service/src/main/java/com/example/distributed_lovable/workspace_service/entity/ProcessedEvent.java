package com.example.distributed_lovable.workspace_service.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "processed_events")
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {
    @Id
    private String sagaId;
    private LocalDateTime processedAt;
}
