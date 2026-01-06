package com.finding_a_partner.feed_service.feign.client.response

import com.finding_a_partner.feed_service.enum.OwnerType
import com.finding_a_partner.feed_service.enum.Visibility
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

data class EventResponse(
    val id: Long,
    val createdAt: OffsetDateTime,
    val ownerId: Long,
    val ownerType: OwnerType,
    val title: String,
    val description: String? = null,
    val visibility: Visibility,
    val time: LocalTime,
    val date: LocalDate,
)
