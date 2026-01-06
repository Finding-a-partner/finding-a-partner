package com.finding_a_partner.message_service.controller

import com.finding_a_partner.message_service.model.response.MessageResponse
import com.finding_a_partner.message_service.service.MessageService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/messages")
class MessageRestController(
    private val service: MessageService
) {
    @GetMapping("/test")
    fun test(): Map<String, String> {
        return mapOf("status" to "ok", "message" to "MessageRestController is working")
    }
    
    @GetMapping("/chat/{chatId}")
    fun getMessagesByChatId(@PathVariable("chatId") chatId: Long): List<MessageResponse> {
        try {
            val messages = service.getMessagesByChatId(chatId)
            return messages
        } catch (e: Exception) {
            println("[MessageRestController] ===== ERROR getting messages ===== ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}

