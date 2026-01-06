package com.finding_a_partner.message_service.mapper

import com.finding_a_partner.message_service.database.entity.Message
import com.finding_a_partner.message_service.model.response.MessageResponse
import org.springframework.stereotype.Component

@Component
class MessageMapper {
    fun entityToResponse(entity: Message): MessageResponse {
        println("[MessageMapper] Mapping entity to response")
        println("[MessageMapper] Entity: id=${entity.id}, chatId=${entity.chat.id}, senderId=${entity.senderId}, content='${entity.content}'")
        
        val response = MessageResponse(
            id = entity.id,
            chatId = entity.chat.id,
            senderId = entity.senderId,
            content = entity.content,
            createdAt = entity.createdAt,
            status = entity.status,
        )
        
        println("[MessageMapper] Response created: id=${response.id}, chatId=${response.chatId}, senderId=${response.senderId}")
        return response
    }
}
