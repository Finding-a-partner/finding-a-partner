package com.findingpartners.event_service.model.request

import com.findingpartners.event_service.enum.OwnerType
import com.findingpartners.event_service.enum.Visibility
import java.time.LocalDate
import java.time.LocalTime

data class EventRequest(
    var ownerId: Long,
    var ownerType: OwnerType,
    var title: String,
    var description: String? = null,
    var visibility: Visibility,
    var time: LocalTime,
    var date: LocalDate,
)
