package com.findingpartners.event_service.service.impl

import com.findingpartners.event_service.database.entity.Event
import com.findingpartners.event_service.database.entity.EventMembers
import com.findingpartners.event_service.database.repository.EventDao
import com.findingpartners.event_service.database.repository.EventMembersDao
import com.findingpartners.event_service.enum.OwnerType
import com.findingpartners.event_service.enum.Visibility
import com.findingpartners.event_service.errors.ResourceNotFoundException
import com.findingpartners.event_service.model.request.EventMembersRequest
import com.findingpartners.event_service.model.response.EventMembersResponse
import com.findingpartners.event_service.model.response.MemberResponse
import com.findingpartners.event_service.service.client.UserServiceClient
import com.findingpartners.event_service.util.EventMapper
import com.findingpartners.event_service.util.EventMembersMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class EventMembersServiceImplTest {

    private lateinit var eventDao: EventDao
    private lateinit var eventMembersDao: EventMembersDao
    private lateinit var eventMembersMapper: EventMembersMapper
    private lateinit var eventMapper: EventMapper
    private lateinit var userServiceClient: UserServiceClient
    private lateinit var eventMembersService: EventMembersServiceImpl

    @BeforeEach
    fun setUp() {
        eventDao = mockk()
        eventMembersDao = mockk()
        eventMembersMapper = mockk()
        eventMapper = mockk()
        userServiceClient = mockk()
        eventMembersService = EventMembersServiceImpl(
            eventDao,
            eventMembersDao,
            eventMembersMapper,
            eventMapper,
            userServiceClient
        )
    }

    @Test
    fun `create should add member to event successfully`() {
        // Given
        val eventId = 1L
        val userId = 2L
        val request = EventMembersRequest(
            eventId = eventId,
            userId = userId
        )
        val event = Event(
            ownerId = 1L,
            ownerType = OwnerType.USER,
            title = "Test Event",
            description = null,
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(12, 0),
            date = LocalDate.now()
        ).apply { id = eventId }
        val eventMember = EventMembers(
            userId = userId,
            event = event
        ).apply { id = 1L }
        val response = EventMembersResponse(
            id = 1L,
            eventId = eventId,
            userId = userId
        )

        every { eventDao.findById(eventId) } returns Optional.of(event)
        every { eventMembersDao.save(any()) } returns eventMember
        every { eventMembersMapper.entityToResponse(eventMember) } returns response

        // When
        val result = eventMembersService.create(request)

        // Then
        assertNotNull(result)
        assertEquals(response.id, result.id)
        assertEquals(response.eventId, result.eventId)
        assertEquals(response.userId, result.userId)
        verify(exactly = 1) { eventDao.findById(eventId) }
        verify(exactly = 1) { eventMembersDao.save(any()) }
        verify(exactly = 1) { eventMembersMapper.entityToResponse(any()) }
    }

    @Test
    fun `create should throw ResourceNotFoundException when event not found`() {
        // Given
        val eventId = 999L
        val userId = 2L
        val request = EventMembersRequest(
            eventId = eventId,
            userId = userId
        )

        every { eventDao.findById(eventId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            eventMembersService.create(request)
        }
        verify(exactly = 1) { eventDao.findById(eventId) }
        verify(exactly = 0) { eventMembersDao.save(any()) }
    }

    @Test
    fun `getAllByEventId should return all members for event`() {
        // Given
        val eventId = 1L
        val event = Event(
            ownerId = 1L,
            ownerType = OwnerType.USER,
            title = "Test Event",
            description = null,
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(12, 0),
            date = LocalDate.now()
        ).apply { id = eventId }
        val memberships = listOf(
            EventMembers(userId = 1L, event = event).apply { id = 1L },
            EventMembers(userId = 2L, event = event).apply { id = 2L }
        )
        val userResponses = listOf(
            com.findingpartners.event_service.service.client.User(
                id = 1L,
                login = "user1",
                name = "User",
                surname = "One"
            ),
            com.findingpartners.event_service.service.client.User(
                id = 2L,
                login = "user2",
                name = "User",
                surname = "Two"
            )
        )
        val memberResponses = listOf(
            MemberResponse(id = 1L, login = "user1", name = "User", surname = "One"),
            MemberResponse(id = 2L, login = "user2", name = "User", surname = "Two")
        )

        every { eventDao.findById(eventId) } returns Optional.of(event)
        every { eventMembersDao.findAllByEventId(eventId) } returns memberships
        every { userServiceClient.getUsersByIds(listOf(1L, 2L)) } returns userResponses

        // When
        val result = eventMembersService.getAllByEventId(eventId)

        // Then
        assertEquals(2, result.size)
        assertEquals(memberResponses[0].id, result[0].id)
        assertEquals(memberResponses[1].id, result[1].id)
        verify(exactly = 1) { eventDao.findById(eventId) }
        verify(exactly = 1) { eventMembersDao.findAllByEventId(eventId) }
        verify(exactly = 1) { userServiceClient.getUsersByIds(listOf(1L, 2L)) }
    }

    @Test
    fun `getAllByEventId should throw ResourceNotFoundException when event not found`() {
        // Given
        val eventId = 999L

        every { eventDao.findById(eventId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            eventMembersService.getAllByEventId(eventId)
        }
        verify(exactly = 1) { eventDao.findById(eventId) }
        verify(exactly = 0) { eventMembersDao.findAllByEventId(any()) }
    }

    @Test
    fun `getAllByUserId should return all events for user`() {
        // Given
        val userId = 1L
        val event1 = Event(
            ownerId = 1L,
            ownerType = OwnerType.USER,
            title = "Event 1",
            description = null,
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(12, 0),
            date = LocalDate.now()
        ).apply { id = 1L }
        val event2 = Event(
            ownerId = 2L,
            ownerType = OwnerType.GROUP,
            title = "Event 2",
            description = null,
            visibility = Visibility.GROUP,
            time = LocalTime.of(14, 0),
            date = LocalDate.now()
        ).apply { id = 2L }
        val memberships = listOf(
            EventMembers(userId = userId, event = event1).apply { id = 1L },
            EventMembers(userId = userId, event = event2).apply { id = 2L }
        )
        val eventResponses = listOf(
            com.findingpartners.event_service.model.response.EventResponse(
                id = 1L,
                ownerId = 1L,
                ownerType = OwnerType.USER,
                title = "Event 1",
                description = null,
                visibility = Visibility.EVERYONE,
                time = LocalTime.of(12, 0),
                date = LocalDate.now(),
                createdAt = null
            ),
            com.findingpartners.event_service.model.response.EventResponse(
                id = 2L,
                ownerId = 2L,
                ownerType = OwnerType.GROUP,
                title = "Event 2",
                description = null,
                visibility = Visibility.GROUP,
                time = LocalTime.of(14, 0),
                date = LocalDate.now(),
                createdAt = null
            )
        )

        every { eventMembersDao.findAllByUserId(userId) } returns memberships
        every { eventDao.findAllById(listOf(1L, 2L)) } returns listOf(event1, event2)
        every { eventMapper.entityToResponse(event1) } returns eventResponses[0]
        every { eventMapper.entityToResponse(event2) } returns eventResponses[1]

        // When
        val result = eventMembersService.getAllByUserId(userId)

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { eventMembersDao.findAllByUserId(userId) }
        verify(exactly = 1) { eventDao.findAllById(listOf(1L, 2L)) }
        verify(exactly = 2) { eventMapper.entityToResponse(any()) }
    }

    @Test
    fun `delete should remove member from event successfully`() {
        // Given
        val eventId = 1L
        val userId = 2L
        val event = Event(
            ownerId = 1L,
            ownerType = OwnerType.USER,
            title = "Test Event",
            description = null,
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(12, 0),
            date = LocalDate.now()
        ).apply { id = eventId }
        val eventMember = EventMembers(
            userId = userId,
            event = event
        ).apply { id = 1L }

        every { eventMembersDao.findByUserIdAndEventId(userId, eventId) } returns Optional.of(eventMember)
        every { eventMembersDao.delete(eventMember) } returns Unit

        // When
        eventMembersService.delete(eventId, userId)

        // Then
        verify(exactly = 1) { eventMembersDao.findByUserIdAndEventId(userId, eventId) }
        verify(exactly = 1) { eventMembersDao.delete(eventMember) }
    }

    @Test
    fun `delete should throw ResourceNotFoundException when membership not found`() {
        // Given
        val eventId = 1L
        val userId = 2L

        every { eventMembersDao.findByUserIdAndEventId(userId, eventId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            eventMembersService.delete(eventId, userId)
        }
        verify(exactly = 1) { eventMembersDao.findByUserIdAndEventId(userId, eventId) }
        verify(exactly = 0) { eventMembersDao.delete(any()) }
    }
}

