package com.findingpartners.event_service.service.impl

import com.findingpartners.event_service.database.entity.EventMembers
import com.findingpartners.event_service.database.repository.EventDao
import com.findingpartners.event_service.database.repository.EventMembersDao
import com.findingpartners.event_service.errors.ResourceNotFoundException
import com.findingpartners.event_service.model.request.EventMembersRequest
import com.findingpartners.event_service.model.response.EventMembersResponse
import com.findingpartners.event_service.model.response.EventResponse
import com.findingpartners.event_service.model.response.MemberResponse
import com.findingpartners.event_service.service.EventMembersService
import com.findingpartners.event_service.service.client.UserServiceClient
import com.findingpartners.event_service.util.EventMapper
import com.findingpartners.event_service.util.EventMembersMapper
import org.springframework.stereotype.Service

@Service
class EventMembersServiceImpl(
    val eventDao: EventDao,
    val dao: EventMembersDao,
    val mapper: EventMembersMapper,
    val eventMapper: EventMapper,
    val userService: UserServiceClient,
) : EventMembersService {

    override fun getAll(): List<EventMembersResponse> {
        return dao.findAll().map { mapper.entityToResponse(it) }
    }

    override fun getAllByEventId(eventId: Long): List<MemberResponse> {
        eventDao.findById(eventId).orElseThrow { throw ResourceNotFoundException(eventId) }
        val memberships = dao.findAllByEventId(eventId)

        val userIds = memberships.map { it.userId }.distinct()

        val users = userService.getUsersByIds(userIds)
            .associateBy { it.id }

        return memberships.mapNotNull { membership ->
            users[membership.userId]?.let { user ->
                MemberResponse(
                    id = user.id,
                    login = user.login,
                    name = user.name,
                    surname = user.surname,
                )
            }
        }
    }

    override fun getAllByUserId(userId: Long): List<EventResponse> {
        val memberships = dao.findAllByUserId(userId)
        val eventIds = memberships.map { it.event.id }
        val events = eventDao.findAllById(eventIds)
        return events.map { event ->
            eventMapper.entityToResponse(event)
        }
    }

    override fun create(request: EventMembersRequest): EventMembersResponse {
        val event = eventDao.findById(request.eventId)
            .orElseThrow { ResourceNotFoundException("Event not found with id: ${request.eventId}") }

        val entity = EventMembers(
            userId = request.userId,
            event = event,
        )

        return mapper.entityToResponse(dao.save(entity))
    }

    override fun delete(eventId: Long, userId: Long) {
        val eventMember = dao.findByUserIdAndEventId(userId, eventId)
            .orElseThrow { ResourceNotFoundException("Membership not found for user $userId in event $eventId") }
        dao.delete(eventMember)
    }
}
