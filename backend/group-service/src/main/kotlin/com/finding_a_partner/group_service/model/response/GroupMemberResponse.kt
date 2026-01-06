package com.finding_a_partner.group_service.model.response

import com.finding_a_partner.group_service.enums.GroupRoleType

data class GroupMemberResponse(
    val userId: Long,
    val name: String?,
    val role: GroupRoleType,
)
