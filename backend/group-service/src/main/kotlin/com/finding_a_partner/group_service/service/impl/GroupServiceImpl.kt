package com.finding_a_partner.group_service.service.impl

import com.finding_a_partner.group_service.database.entity.Group
import com.finding_a_partner.group_service.database.repository.GroupDao
import com.finding_a_partner.group_service.enums.GroupRoleType
import com.finding_a_partner.group_service.errors.ResourceNotFoundException
import com.finding_a_partner.group_service.mappers.GroupMapper
import com.finding_a_partner.group_service.model.request.GroupMembershipRequest
import com.finding_a_partner.group_service.model.request.GroupRequest
import com.finding_a_partner.group_service.model.response.GroupResponse
import com.finding_a_partner.group_service.service.GroupMembershipService
import com.finding_a_partner.group_service.service.GroupService
import org.springframework.stereotype.Service

@Service
class GroupServiceImpl(
    val dao: GroupDao,
    val mapper: GroupMapper,
    val memberService: GroupMembershipService,
) : GroupService {
    override fun getAll(): List<GroupResponse> {
        return dao.findAll().map { mapper.entityToResponse(it) }
    }

    override fun getById(id: Long): GroupResponse =
        mapper.entityToResponse(dao.findById(id).orElseThrow { throw ResourceNotFoundException(id) })

    override fun getEntityById(id: Long): Group =
        dao.findById(id).orElseThrow { throw ResourceNotFoundException(id) }

    override fun searchGroups(query: String): List<GroupResponse> {
        return dao.findByNameContainingOrDescriptionContaining(query, query)
            .map { mapper.entityToResponse(it) }
    }

    override fun update(id: Long, request: GroupRequest): GroupResponse {
        val entity = dao.findById(id).orElseThrow { throw ResourceNotFoundException(id) }
            .apply {
                name = request.name
                description = request.description
            }
        return mapper.entityToResponse(dao.save(entity))
    }

    override fun create(request: GroupRequest, userId: Long): GroupResponse {
        val entity = Group(
            name = request.name,
            description = request.description,
            creatorUserId = userId,
        )
        val savedEntity = dao.save(entity)
        memberService.create(
            groupId = savedEntity.id,
            userId = savedEntity.creatorUserId,
            request = GroupMembershipRequest(role = GroupRoleType.OWNER),
        )
        return mapper.entityToResponse(savedEntity)
    }

    override fun delete(id: Long) {
        val entity = dao.findById(id).orElseThrow { throw ResourceNotFoundException(id) }
        dao.delete(entity)
    }
}
