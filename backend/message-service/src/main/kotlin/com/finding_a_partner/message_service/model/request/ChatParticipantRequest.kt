package com.finding_a_partner.message_service.model.request

import com.finding_a_partner.message_service.enum.ChatRole
import com.finding_a_partner.message_service.enum.ParticipantType

data class ChatParticipantRequest(
    val participantId: Long,
    val participantType: ParticipantType,
    val role: ChatRole,
)
