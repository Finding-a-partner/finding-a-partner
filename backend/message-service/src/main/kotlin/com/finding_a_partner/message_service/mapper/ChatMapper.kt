package com.finding_a_partner.message_service.mapper

import com.finding_a_partner.message_service.database.entity.Chat
import com.finding_a_partner.message_service.model.response.ChatResponse
import org.springframework.stereotype.Component

@Component
class ChatMapper {
    fun entityToResponse(entity: Chat): ChatResponse =
        ChatResponse(
            id = entity.id!!,
            type = entity.type,
            name = entity.name,
            createdAt = entity.createdAt,
            eventId = entity.eventId,
        )
}
