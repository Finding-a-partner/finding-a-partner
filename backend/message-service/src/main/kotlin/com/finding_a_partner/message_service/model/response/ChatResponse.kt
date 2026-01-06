package com.finding_a_partner.message_service.model.response

import com.finding_a_partner.message_service.enum.ChatType
import java.time.OffsetDateTime

data class ChatResponse(
    val id: Long,
    val type: ChatType,
    val name: String?,
    val eventId: Long?,
    val createdAt: OffsetDateTime,
)
