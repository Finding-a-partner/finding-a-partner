package com.finding_a_partner.group_service.model.request

data class GroupRequest(
    val name: String,
    val description: String?,
    val creatorUserId: Long,
)
