package com.findingpartners.event_service.util

import com.findingpartners.event_service.database.entity.Event
import com.findingpartners.event_service.enum.OwnerType
import com.findingpartners.event_service.enum.Visibility
import com.findingpartners.event_service.model.response.EventResponse
import org.springframework.stereotype.Component
import java.sql.Time
import java.time.LocalDateTime

@Component
class EventMapper {
    fun entityToResponse (entity: Event) : EventResponse{
        return EventResponse(
            id = entity.id,
            createdAt = entity.createdAt,
            ownerId = entity.ownerId,
            ownerType = entity.ownerType,
            title = entity.title,
            description = entity.description,
            visibility = entity.visibility,
            time = entity.time,
            date = entity.date
        )
    }
}