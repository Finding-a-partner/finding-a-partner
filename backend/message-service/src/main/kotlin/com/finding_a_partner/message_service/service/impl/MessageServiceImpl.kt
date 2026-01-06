package com.finding_a_partner.message_service.service.impl

import com.finding_a_partner.message_service.database.entity.Message
import com.finding_a_partner.message_service.database.repository.MessageDao
import com.finding_a_partner.message_service.mapper.MessageMapper
import com.finding_a_partner.message_service.model.request.MessageRequest
import com.finding_a_partner.message_service.model.response.MessageResponse
import com.finding_a_partner.message_service.service.ChatService
import com.finding_a_partner.message_service.service.MessageService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MessageServiceImpl(
    val dao: MessageDao,
    val chatService: ChatService,
    val mapper: MessageMapper,
) : MessageService {
    override fun sendMessage(request: MessageRequest): MessageResponse {
        try {
            val chat = chatService.getEntityById(request.chatId)

            val entity = Message(
                chat = chat,
                senderId = request.senderId,
                content = request.content,
            )

            val saved = dao.save(entity)

            val response = mapper.entityToResponse(saved)

            return response
        } catch (e: Exception) {
            println("[MessageServiceImpl] ERROR in sendMessage(): ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    @Transactional
    override fun getMessagesByChatId(chatId: Long): List<MessageResponse> {
        try {
            val messages = dao.findAllByChatIdOrderByCreatedAtAscWithChat(chatId)
            
            val responses = messages.map { message ->
                try {
                    val response = mapper.entityToResponse(message)
                    response
                } catch (e: Exception) {
                    println("[MessageServiceImpl] ===== ERROR mapping message ${message.id} ===== ${e.message}")
                    e.printStackTrace()
                    throw e
                }
            }
            return responses
        } catch (e: Exception) {
            println("[MessageServiceImpl] ===== ERROR in getMessagesByChatId ===== ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
