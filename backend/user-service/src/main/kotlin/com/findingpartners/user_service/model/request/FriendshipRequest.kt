package com.findingpartners.user_service.model.request

import com.findingpartners.user_service.enum.FriendshipStatus

data class FriendshipRequest(
    val userId: Long,
    val friendId: Long,
    val status: FriendshipStatus = FriendshipStatus.PENDING,
)
