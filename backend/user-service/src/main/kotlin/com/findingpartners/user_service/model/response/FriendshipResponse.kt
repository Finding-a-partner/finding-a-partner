package com.findingpartners.user_service.model.response

import com.findingpartners.user_service.enum.FriendshipStatus

data class FriendshipResponse(
    val id: Long,
    val userId: Long,
    val friendId: Long,
    val status: FriendshipStatus,
)
