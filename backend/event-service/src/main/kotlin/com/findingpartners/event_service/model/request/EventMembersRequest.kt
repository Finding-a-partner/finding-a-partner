package com.findingpartners.event_service.model.request

data class EventMembersRequest (
    val eventId: Long,
    val userId: Long
)
