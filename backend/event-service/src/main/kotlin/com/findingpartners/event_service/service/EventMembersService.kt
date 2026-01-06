package com.findingpartners.event_service.service

import com.findingpartners.event_service.model.request.EventMembersRequest
import com.findingpartners.event_service.model.response.EventMembersResponse
import com.findingpartners.event_service.model.response.EventResponse
import com.findingpartners.event_service.model.response.MemberResponse

interface EventMembersService {
    fun getAll(): List<EventMembersResponse>
    fun getAllByEventId(eventId: Long): List<MemberResponse>
    fun getAllByUserId(userId: Long): List<EventResponse>
    fun create(request: EventMembersRequest): EventMembersResponse
    fun delete(eventId: Long, userId: Long)
}