package com.findingpartners.user_service.util

import com.findingpartners.user_service.database.entity.Friendship
import com.findingpartners.user_service.model.response.FriendshipResponse
import org.springframework.stereotype.Component

@Component
class FriendshipMapper{
    fun entityToResponse (entity: Friendship) : FriendshipResponse {
        return FriendshipResponse(
            id = entity.id ?: throw IllegalArgumentException("Friendship ID cannot be null"),
            userId = entity.user1.id,
            friendId = entity.user2.id,
            status = entity.statusForUser1,
        )
    }
}