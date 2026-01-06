package com.finding_a_partner.group_service.service

import com.finding_a_partner.group_service.model.request.GroupMembershipRequest
import com.finding_a_partner.group_service.model.response.GroupMemberResponse
import com.finding_a_partner.group_service.model.response.GroupMembershipResponse
import com.finding_a_partner.group_service.model.response.GroupResponse

interface GroupMembershipService {
    fun getAll(): List<GroupMembershipResponse>
    fun getAllByGroupId(groupId: Long): List<GroupMemberResponse>
    fun getAllByUserId(userId: Long): List<GroupResponse>
    fun update(groupId: Long, userId: Long, request: GroupMembershipRequest): GroupMembershipResponse
    fun create(groupId: Long, userId: Long, request: GroupMembershipRequest): GroupMembershipResponse
    fun delete(groupId: Long, userId: Long)
}