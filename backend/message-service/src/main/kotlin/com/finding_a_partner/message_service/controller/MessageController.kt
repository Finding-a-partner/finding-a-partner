package com.finding_a_partner.message_service.controller

import com.finding_a_partner.message_service.model.request.MessageRequest
import com.finding_a_partner.message_service.model.response.MessageResponse
import com.finding_a_partner.message_service.service.MessageService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class MessageController(
    private val service: MessageService,
    private val messagingTemplate: SimpMessagingTemplate
) {

    @MessageMapping("/chat.sendMessage")
    fun sendMessage(message: MessageRequest) {
        try {
            val response = service.sendMessage(message)

            val topic = "/topic/chat.${message.chatId}"
            messagingTemplate.convertAndSend(topic, response)
        } catch (e: Exception) {
            println("[MessageController] ERROR processing message: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}