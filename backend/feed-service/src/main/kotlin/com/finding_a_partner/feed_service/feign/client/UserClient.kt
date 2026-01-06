package com.finding_a_partner.feed_service.feign.client

import com.finding_a_partner.feed_service.enum.FriendshipStatus
import com.finding_a_partner.feed_service.feign.client.response.FriendResponse
import com.finding_a_partner.feed_service.feign.client.response.UserResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(name = "user-service", path = "/users")
interface UserClient {
    @GetMapping(value = ["/{id}"])
    fun getById(@PathVariable id: Long): UserResponse

    @PostMapping("/batch")
    fun getUsersByIds(@RequestBody ids: List<Long>): List<UserResponse>

    // поиск друзей пользователя с id
    @GetMapping("/friends/{id}")
    fun getRequests(
        @PathVariable id: Long,
        @RequestParam(required = false) status: FriendshipStatus,
    ): List<FriendResponse>
}
