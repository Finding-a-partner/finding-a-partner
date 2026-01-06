package com.finding_a_partner.message_service.feign

import com.finding_a_partner.message_service.model.response.UserResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(name = "user-service", path = "/users")
interface UserClient {
    @GetMapping
    fun getAll(): List<UserResponse>

    @GetMapping("/{login}")
    fun getByLogin(@PathVariable("login") login: String): UserResponse

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long): UserResponse

    @GetMapping("/search")
    fun searchUsers(@RequestParam query: String): List<UserResponse>

    @PostMapping("/batch")
    fun getUsersByIds(@RequestBody ids: List<Long>): List<UserResponse>
}
