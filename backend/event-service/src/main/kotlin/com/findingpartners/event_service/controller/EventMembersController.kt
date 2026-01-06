package com.findingpartners.event_service.controller

import com.findingpartners.event_service.model.request.EventMembersRequest
import com.findingpartners.event_service.service.EventMembersService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/event-members")
class EventMembersController(
    val eventService: EventMembersService,
) {
    @GetMapping()
    fun getAll() = eventService.getAll()

    @GetMapping("/event/{eventId}")
    fun getAllByEventId(@PathVariable("eventId") eventId: Long) = eventService.getAllByEventId(eventId)

    @GetMapping("/user/{userId}")
    fun getAllByUserId(@PathVariable("userId") userId: Long) = eventService.getAllByUserId(userId)

    @PostMapping
    fun create(@RequestBody request: EventMembersRequest) = eventService.create(request)

    @DeleteMapping("/{eventId}/{userId}")
    fun delete(@PathVariable("eventId") eventId: Long, @PathVariable("userId") userId: Long) =
        eventService.delete(eventId, userId)
}
