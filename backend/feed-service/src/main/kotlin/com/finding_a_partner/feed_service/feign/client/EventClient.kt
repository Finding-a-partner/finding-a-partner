package com.finding_a_partner.feed_service.feign.client

import com.finding_a_partner.feed_service.enum.OwnerType
import com.finding_a_partner.feed_service.feign.client.response.EventResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "event-service", path = "/events")
interface EventClient {
    @GetMapping(value = ["/{id}"])
    fun getById(@PathVariable id: Long): EventResponse

    @GetMapping("/{ownerType}/{ownerId}")
    fun getByOwnerId(@PathVariable("ownerId") id: Long, @PathVariable("ownerType") type: OwnerType): List<EventResponse>

    @PostMapping("/{ownerType}/batch")
    fun getByOwnerTypeBatch(
        @PathVariable("ownerType") type: OwnerType,
        @RequestBody ids: List<Long>,
    ): List<EventResponse>
}
