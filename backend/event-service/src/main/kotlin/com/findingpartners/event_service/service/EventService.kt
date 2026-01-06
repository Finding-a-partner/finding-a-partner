package com.findingpartners.event_service.service

import com.findingpartners.event_service.enum.OwnerType
import com.findingpartners.event_service.model.request.EventRequest
import com.findingpartners.event_service.model.response.EventResponse

interface EventService {
    fun update(id: Long, request: EventRequest): EventResponse
    fun delete(id: Long)
    fun create(request: EventRequest, userId: Long): EventResponse
    fun getAll(): List<EventResponse>
    fun getById(id: Long): EventResponse
    fun getByOwnerId(id: Long, type: OwnerType): List<EventResponse>
    fun getByOwnerIds(ids: List<Long>, type: OwnerType): List<EventResponse>
}
