package com.finding_a_partner.message_service.feign

import com.finding_a_partner.message_service.model.response.EventResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "event-service", path = "/events")
interface EventClient {

    @GetMapping("/{id}")
    fun getEventById(@PathVariable id: Long): EventResponse
}
