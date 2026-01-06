package com.findingpartners.event_service.service.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

data class Group(
    val id: Long,
    val name: String,
)

@FeignClient(name = "group-service", path = "/groups")
interface GroupServiceClient {
    @GetMapping("/{id}")
    fun getGroupById(@PathVariable id: Long): Group
}
