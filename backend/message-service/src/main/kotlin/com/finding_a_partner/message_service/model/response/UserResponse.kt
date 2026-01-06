package com.finding_a_partner.message_service.model.response

import java.time.OffsetDateTime

data class UserResponse(
    val id: Long,
    val createdAt: OffsetDateTime,
    val login: String,
    val email: String,
    val description: String? = null,
    val name: String,
    val surname: String? = null,
    val avatarUrl: String? = null,
)
