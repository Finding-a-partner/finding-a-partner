package com.finding_a_partner.message_service.model.response

import java.time.OffsetDateTime

data class GroupResponse(
    val id: Long,
    val createdAt: OffsetDateTime,
    val name: String,
    val description: String?,
    val creatorUserId: Long,
    val avatarUrl: String? = null,
)
