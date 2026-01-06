package com.finding_a_partner.message_service.model.request

import com.finding_a_partner.message_service.enum.ChatType
import com.finding_a_partner.message_service.enum.ParticipantType

data class ChatRequest(
    val type: ChatType,
    val name: String?,
    val ownerId: Long?,
    val ownerType: ParticipantType?,
    val participants: List<ChatParticipantRequest>?,
    val eventId: Long?
)
