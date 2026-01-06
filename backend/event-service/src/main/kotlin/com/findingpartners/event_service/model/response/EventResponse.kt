package com.findingpartners.event_service.model.response

import com.findingpartners.event_service.enum.OwnerType
import com.findingpartners.event_service.enum.Visibility
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

data class EventResponse(
    val id: Long,
    val createdAt: OffsetDateTime,
    val ownerId: Long,
    val ownerType: OwnerType, // тут enum
    val title: String,
    val description: String? = null,
    val visibility: Visibility,
    val time: LocalTime,
    val date: LocalDate,
)
