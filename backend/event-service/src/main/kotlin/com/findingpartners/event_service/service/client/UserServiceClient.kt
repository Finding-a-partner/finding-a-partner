package com.findingpartners.event_service.service.client

import com.findingpartners.event_service.enum.OwnerType
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val surname: String,
)

@FeignClient(name = "user-service", path = "/users")
interface UserServiceClient {
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): User

    @PostMapping("/batch")
    fun getUsersByIds(@RequestBody ids: List<Long>): List<User>

}
