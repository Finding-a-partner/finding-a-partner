package com.finding_a_partner.message_service.model.request

import com.finding_a_partner.message_service.database.entity.Chat

data class MessageRequest(
    val chatId: Long,
    val senderId: Long,
    val content: String,
)
