package com.finding_a_partner.group_service.controller

import com.finding_a_partner.group_service.model.request.GroupMembershipRequest
import com.finding_a_partner.group_service.service.GroupMembershipService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/groups")
class GroupMembershipController(
    val service: GroupMembershipService,
) {
    @GetMapping("/{groupId}/member")
    fun getById(@PathVariable("groupId") groupId: Long) = service.getAllByGroupId(groupId)

    @GetMapping("/{userId}/group")
    fun getByUserId(@PathVariable("userId") groupId: Long) = service.getAllByUserId(groupId)

    @PutMapping("/{groupId}/member")
    fun update(
        @PathVariable("groupId") groupId: Long,
        @RequestHeader("X-User-Id") userId: Long,
        @RequestBody request: GroupMembershipRequest,
    ) = service.update(groupId, userId, request)

    @PostMapping("/{groupId}/member")
    fun create(
        @PathVariable("groupId") groupId: Long,
        @RequestHeader("X-User-Id") userId: Long,
        @RequestBody request: GroupMembershipRequest,
    ) = service.create(groupId, userId, request)

    @DeleteMapping("/{groupId}/member")
    fun delete(
        @PathVariable("groupId") groupId: Long,
        @RequestHeader("X-User-Id") userId: Long,
    ) = service.delete(groupId, userId)
}
