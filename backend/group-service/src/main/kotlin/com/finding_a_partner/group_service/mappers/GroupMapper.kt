package com.finding_a_partner.group_service.mappers

import com.finding_a_partner.group_service.database.entity.Group
import com.finding_a_partner.group_service.model.response.GroupResponse
import org.springframework.stereotype.Component

@Component
class GroupMapper {
    fun entityToResponse(entity: Group): GroupResponse =
        GroupResponse(
            id = entity.id,
            createdAt = entity.createdAt,
            name = entity.name,
            description = entity.description,
            creatorUserId = entity.creatorUserId,
        )
}
