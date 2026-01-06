package com.finding_a_partner.message_service.model.response

import com.finding_a_partner.message_service.enum.MessageStatus
import java.time.OffsetDateTime

data class MessageResponse(
    val id: Long,
    val chatId: Long,
    val senderId: Long,
    val content: String,
    val createdAt: OffsetDateTime,
    val status: MessageStatus,
)
