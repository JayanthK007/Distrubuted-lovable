package com.example.distributed_lovable.intelligence_service.mapper;

import java.util.List;

import com.example.distributed_lovable.intelligence_service.dto.ChatEventResponse;
import com.example.distributed_lovable.intelligence_service.dto.ChatResponse;
import com.example.distributed_lovable.intelligence_service.entity.ChatEvent;
import com.example.distributed_lovable.intelligence_service.entity.ChatMessage;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<ChatResponse> toListOfChatResponse(List<ChatMessage> chatMessage);

    ChatEventResponse toChatEventResponse(ChatEvent chatEvent);

    List<ChatEventResponse> map(List<ChatEvent> events);
}