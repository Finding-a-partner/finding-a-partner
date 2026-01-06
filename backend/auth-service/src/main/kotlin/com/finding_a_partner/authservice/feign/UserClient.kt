package com.finding_a_partner.authservice.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "user-service", path = "/users")
interface UserClient {
    @PostMapping
    fun create(@RequestBody request: UserRequest): UserResponse

    @GetMapping("/{login}/login")
    fun getByLogin(@PathVariable("login") login: String): UserResponse
}