package com.findingpartners.event_service.util

import com.findingpartners.event_service.database.entity.Event
import com.findingpartners.event_service.database.entity.EventMembers
import com.findingpartners.event_service.model.response.EventMembersResponse
import com.findingpartners.event_service.model.response.EventResponse
import org.springframework.stereotype.Component

@Component
class EventMembersMapper {
    fun entityToResponse (entity: EventMembers) : EventMembersResponse {
        return EventMembersResponse(
            id = entity.id,
            userId = entity.userId,
            eventId = entity.event.id
        )
    }
}