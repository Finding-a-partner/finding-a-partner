package com.finding_a_partner.message_service.model.response

import com.finding_a_partner.message_service.enum.ChatRole
import com.finding_a_partner.message_service.enum.ParticipantType
import java.time.OffsetDateTime

data class ChatParticipantResponse(
    val id: Long,
    val chatId: Long,
    val participantId: Long,
    val type: ParticipantType,
    val role: ChatRole,
    val createdAt: OffsetDateTime,
)
