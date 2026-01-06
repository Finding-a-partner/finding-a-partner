package com.finding_a_partner.group_service.service

import com.finding_a_partner.group_service.database.entity.Group
import com.finding_a_partner.group_service.model.request.GroupRequest
import com.finding_a_partner.group_service.model.response.GroupResponse

interface GroupService {
    fun getAll(): List<GroupResponse>
    fun getById(id: Long): GroupResponse
    fun getEntityById(id: Long): Group
    fun searchGroups(query: String): List<GroupResponse>
    fun update(id: Long, request: GroupRequest): GroupResponse
    fun create(request: GroupRequest, userId: Long): GroupResponse
    fun delete(id: Long)
}
