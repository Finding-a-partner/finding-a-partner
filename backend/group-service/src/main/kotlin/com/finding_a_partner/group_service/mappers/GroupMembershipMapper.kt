package com.finding_a_partner.group_service.mappers

import com.finding_a_partner.group_service.database.entity.GroupMembership
import com.finding_a_partner.group_service.model.response.GroupMembershipResponse
import org.springframework.stereotype.Component

@Component
class GroupMembershipMapper {
    fun entityToResponse(entity: GroupMembership): GroupMembershipResponse =
        GroupMembershipResponse(
            groupId = entity.group.id,
            userId = entity.userId,
            role = entity.role,
            createdAt = entity.createdAt,
        )
}
