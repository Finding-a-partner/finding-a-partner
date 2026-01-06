package com.finding_a_partner.group_service.model.response

import com.finding_a_partner.group_service.enums.GroupRoleType
import java.time.OffsetDateTime

class GroupMembershipResponse(
    val createdAt: OffsetDateTime,
    val groupId: Long,
    val userId: Long,
    val role: GroupRoleType,
)
