package com.findingpartners.user_service.controller

import com.findingpartners.user_service.model.request.UserRequest
import com.findingpartners.user_service.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    val userService: UserService,
) {
    @GetMapping
    fun getAll() = userService.getAll()

    @GetMapping("/{login}/login")
    fun getByLogin(@PathVariable("login") login: String) = userService.getByLogin(login)

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long) = userService.getById(id)

    @GetMapping("/search")
    fun searchUsers(@RequestParam query: String) = userService.searchUsers(query)

    @PostMapping("/batch")
    fun getUsersByIds(@RequestBody ids: List<Long>) = userService.getByIds(ids)

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody request: UserRequest) = userService.update(id, request)

    @PostMapping
    fun create(@RequestBody request: UserRequest) = userService.create(request)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long) =
        userService.delete(id)
}
