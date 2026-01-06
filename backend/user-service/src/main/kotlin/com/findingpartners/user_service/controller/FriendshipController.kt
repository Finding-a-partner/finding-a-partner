package com.findingpartners.user_service.controller

import com.findingpartners.user_service.enum.FriendshipStatus
import com.findingpartners.user_service.model.request.FriendshipRequest
import com.findingpartners.user_service.service.impl.FriendshipServiceImpl
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class FriendshipController(private val friendshipService: FriendshipServiceImpl) {

    @PostMapping("/friends/requests")
    fun sendRequest(@RequestBody request: FriendshipRequest) = friendshipService.sendFriendRequest(request)

    @PostMapping("/friends/{requestId}")
    fun respondToRequest(
        @PathVariable requestId: Long,
        @RequestBody request: FriendshipRequest,
    ) = friendshipService.respondToFriendRequest(requestId, request)

    @DeleteMapping("/friends/delete")
    fun deleteFriend(
        @RequestParam friendId: Long,
        @RequestParam currentUser: Long,
    ) = friendshipService.deleteFriend(currentUser, friendId)

    @GetMapping("/friends/{id}")
    fun getRequests(
        @PathVariable id: Long,
        @RequestParam(required = false) status: FriendshipStatus,
    ) = friendshipService.getUserRequests(id, status ?: FriendshipStatus.ACCEPTED)

    @GetMapping("/friends/{id}/incoming")
    fun getIncomingRequests(@PathVariable id: Long) = friendshipService.getIncomingRequests(id)
}
