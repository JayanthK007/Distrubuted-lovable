package com.example.distributed_lovable.intelligence_service.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionId implements Serializable {

    private Long userId;
    private Long projectId;
}