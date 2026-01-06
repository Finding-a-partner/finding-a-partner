package com.finding_a_partner.group_service.service.impl

import com.finding_a_partner.group_service.database.entity.GroupMembership
import com.finding_a_partner.group_service.database.entity.GroupMembershipId
import com.finding_a_partner.group_service.database.repository.GroupDao
import com.finding_a_partner.group_service.database.repository.GroupMembershipDao
import com.finding_a_partner.group_service.errors.ResourceNotFoundException
import com.finding_a_partner.group_service.mappers.GroupMapper
import com.finding_a_partner.group_service.mappers.GroupMembershipMapper
import com.finding_a_partner.group_service.model.request.GroupMembershipRequest
import com.finding_a_partner.group_service.model.response.GroupMemberResponse
import com.finding_a_partner.group_service.model.response.GroupMembershipResponse
import com.finding_a_partner.group_service.model.response.GroupResponse
import com.finding_a_partner.group_service.service.GroupMembershipService
import com.finding_a_partner.group_service.service.UserService
import org.springframework.stereotype.Service

@Service
class GroupMembershipServiceImpl(
    val dao: GroupMembershipDao,
    val mapper: GroupMembershipMapper,
    val groupMapper: GroupMapper,
    val groupDao: GroupDao,
    val userService: UserService,
) : GroupMembershipService {
    override fun getAll(): List<GroupMembershipResponse> =
        dao.findAll().map { mapper.entityToResponse(it) }

    override fun getAllByGroupId(groupId: Long): List<GroupMemberResponse> {
        groupDao.findById(groupId).orElseThrow { throw ResourceNotFoundException(groupId) }

        val memberships = dao.findAllByIdGroupId(groupId)
        val userIds = memberships.map { it.userId }

        val userInfo = userService.getUsersByIds(userIds)

        return memberships.map { membership ->
            val user = userInfo.find { it.id == membership.userId }
            GroupMemberResponse(
                userId = membership.userId,
                name = user?.name,
                role = membership.role,
            )
        }
    }

    override fun getAllByUserId(userId: Long): List<GroupResponse> {
        val memberships = dao.findAllWithGroupByUserId(userId)
        return memberships.map { groupMapper.entityToResponse(it.group) }
    }

    override fun update(groupId: Long, userId: Long, request: GroupMembershipRequest): GroupMembershipResponse {
        val entity = dao.findById(GroupMembershipId(groupId, userId)).orElseThrow { throw ResourceNotFoundException() }
            .apply {
                role = request.role
            }

        return mapper.entityToResponse(dao.save(entity))
    }

    override fun create(groupId: Long, userId: Long, request: GroupMembershipRequest): GroupMembershipResponse {
        val group = groupDao.findById(groupId).orElseThrow { throw ResourceNotFoundException(groupId) }

        val id = GroupMembershipId(groupId = groupId, userId = userId)

        val entity = GroupMembership(
            id = id,
            group = group,
            userId = userId,
            role = request.role,
        )
        return mapper.entityToResponse(dao.save(entity))
    }

    override fun delete(groupId: Long, userId: Long) {
        val entity = dao.findById(GroupMembershipId(groupId, userId)).orElseThrow { throw ResourceNotFoundException() }
        dao.delete(entity)
    }
}
