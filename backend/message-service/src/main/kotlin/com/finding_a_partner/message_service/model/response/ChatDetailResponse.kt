package com.finding_a_partner.message_service.model.response

import com.finding_a_partner.message_service.enum.ChatType
import java.time.OffsetDateTime

data class ChatDetailResponse(
    val id: Long,
    val type: ChatType,
    val name: String?,
    val createdAt: OffsetDateTime,
    val participants: List<ChatParticipantDetailResponse>,
    val eventId: Long?,
//    val messages: List<MessageResponse>
)