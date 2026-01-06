package com.findingpartners.event_service.model.response

data class EventMembersResponse(
    val id: Long,
    val userId: Long,
    val eventId: Long,
)
