package com.findingpartners.user_service.model.response

import com.findingpartners.user_service.enum.FriendshipStatus

data class FriendResponse(
    val id: Long,
    val status: FriendshipStatus,
    val friend: UserResponse,
)
