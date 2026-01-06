package com.finding_a_partner.group_service.controller

import com.finding_a_partner.group_service.model.request.GroupRequest
import com.finding_a_partner.group_service.service.GroupService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/groups")
class GroupController(
    val groupService: GroupService,
) {
    @GetMapping
    fun getAll() = groupService.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Long) = groupService.getById(id)

    @GetMapping("/search")
    fun searchUsers(@RequestParam query: String) = groupService.searchGroups(query)

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody request: GroupRequest,
    ) = groupService.update(id, request)

    @PostMapping
    fun create(
        @RequestBody request: GroupRequest,
        @RequestHeader("X-User-Id") userId: Long,
    ) = groupService.create(request, userId)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long) = groupService.delete(id)
}
