package com.finding_a_partner.feed_service.feign.client.response

import com.finding_a_partner.feed_service.enum.FriendshipStatus

data class FriendResponse(
    val id: Long,
    val status: FriendshipStatus,
    val friend: UserResponse,
)
