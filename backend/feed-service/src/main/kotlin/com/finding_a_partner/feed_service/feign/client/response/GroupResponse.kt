package com.finding_a_partner.feed_service.feign.client.response

import java.time.OffsetDateTime

data class GroupResponse(
    val id: Long,
    val createdAt: OffsetDateTime,
    val name: String,
    val description: String?,
    val creatorUserId: Long,
)
