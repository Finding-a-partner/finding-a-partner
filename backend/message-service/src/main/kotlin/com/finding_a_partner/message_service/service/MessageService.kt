package com.finding_a_partner.message_service.service

import com.finding_a_partner.message_service.model.request.MessageRequest
import com.finding_a_partner.message_service.model.response.MessageResponse

interface MessageService {
    fun sendMessage(request: MessageRequest): MessageResponse
    fun getMessagesByChatId(chatId: Long): List<MessageResponse>
}