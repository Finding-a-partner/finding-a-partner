package com.findingpartners.event_service.controller

import com.findingpartners.event_service.enum.OwnerType
import com.findingpartners.event_service.model.request.EventRequest
import com.findingpartners.event_service.service.EventService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/events")
class EventController(
    val eventService: EventService,
) {
    @GetMapping
    fun getAll() = eventService.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long) = eventService.getById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody request: EventRequest) = eventService.update(id, request)

    @PostMapping
    fun create(
        @RequestBody request: EventRequest,
        @RequestHeader("X-User-Id") userId: Long,
    ) = eventService.create(request, userId)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long) = eventService.delete(id)

    @GetMapping("/{ownerType}/{ownerId}")
    fun getByOwnerId(@PathVariable("ownerId") id: Long, @PathVariable("ownerType") type: OwnerType) =
        eventService.getByOwnerId(id, type)

    @PostMapping("/{ownerType}/batch")
    fun getByOwnerTypeBatch(
        @PathVariable("ownerType") type: OwnerType,
        @RequestBody ids: List<Long>,
    ) = eventService.getByOwnerIds(ids, type)
}
