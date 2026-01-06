package com.finding_a_partner.message_service.model.response

import com.finding_a_partner.message_service.enum.ChatRole
import com.finding_a_partner.message_service.enum.ParticipantType
import java.time.OffsetDateTime

data class ChatParticipantDetailResponse(
    val id: Long,
    val participantId: Long,
    val participantType: ParticipantType,
    val role: ChatRole,
    val name: String,
    // user
    val surname: String? = null,
    val login: String? = null,
    // all
    val avatarUrl: String? = null,
    val description: String? = null,
    val createdAt: OffsetDateTime,
)
