package com.finding_a_partner.feed_service.feign.client

import com.finding_a_partner.feed_service.feign.client.response.GroupResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "group-service", path = "/groups")
interface GroupClient {
    @GetMapping(value = ["/{id}"])
    fun getById(@PathVariable id: Long): GroupResponse

    @GetMapping("/{userId}/group")
    fun getUserGroups(@PathVariable userId: Long): List<GroupResponse>
}