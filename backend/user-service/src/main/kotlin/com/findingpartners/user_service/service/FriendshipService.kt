package com.findingpartners.user_service.service

import com.findingpartners.user_service.enum.FriendshipStatus
import com.findingpartners.user_service.model.request.FriendshipRequest
import com.findingpartners.user_service.model.response.FriendResponse
import com.findingpartners.user_service.model.response.FriendshipResponse

interface FriendshipService {
    fun sendFriendRequest(request: FriendshipRequest): FriendshipResponse
    fun respondToFriendRequest(requestId: Long, request: FriendshipRequest): FriendshipResponse
    fun getUserRequests(userId: Long, status: FriendshipStatus): List<FriendResponse>
    fun getIncomingRequests(userId: Long): List<FriendResponse>
    fun deleteFriend(currentUserId: Long, friendId: Long)
}
