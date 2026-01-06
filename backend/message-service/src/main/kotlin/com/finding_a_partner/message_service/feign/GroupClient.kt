package com.finding_a_partner.message_service.feign

import com.finding_a_partner.message_service.model.response.GroupResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "group-service", path = "/groups")
interface GroupClient {

    @GetMapping("/{id}")
    fun getGroupById(@PathVariable id: Long): GroupResponse
}