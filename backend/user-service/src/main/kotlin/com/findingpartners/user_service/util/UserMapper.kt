package com.findingpartners.user_service.util

import com.findingpartners.user_service.database.entity.User
import com.findingpartners.user_service.model.response.UserResponse
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun entityToResponse(entity: User): UserResponse {
        return UserResponse(
            id = entity.id,
            createdAt = entity.createdAt,
            login = entity.login,
            email = entity.email,
            name = entity.name,
            surname = entity.surname,
        )
    }
}
