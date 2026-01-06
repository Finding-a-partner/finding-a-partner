package com.findingpartners.event_service.service.impl

import com.findingpartners.event_service.database.entity.Event
import com.findingpartners.event_service.database.entity.EventMembers
import com.findingpartners.event_service.database.repository.EventDao
import com.findingpartners.event_service.database.repository.EventMembersDao
import com.findingpartners.event_service.enum.OwnerType
import com.findingpartners.event_service.enum.Visibility
import com.findingpartners.event_service.errors.ResourceNotFoundException
import com.findingpartners.event_service.model.request.EventRequest
import com.findingpartners.event_service.model.response.EventResponse
import com.findingpartners.event_service.service.client.GroupServiceClient
import com.findingpartners.event_service.service.client.UserServiceClient
import com.findingpartners.event_service.util.EventMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.slot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class EventServiceImplTest {

    private lateinit var eventDao: EventDao
    private lateinit var eventMapper: EventMapper
    private lateinit var userServiceClient: UserServiceClient
    private lateinit var groupServiceClient: GroupServiceClient
    private lateinit var eventMembersDao: EventMembersDao
    private lateinit var eventService: EventServiceImpl

    @BeforeEach
    fun setUp() {
        eventDao = mockk()
        eventMapper = mockk()
        userServiceClient = mockk()
        groupServiceClient = mockk()
        eventMembersDao = mockk()
        eventService = EventServiceImpl(eventDao, eventMapper, userServiceClient, groupServiceClient, eventMembersDao)
    }

    @Test
    fun `create should create event and add owner as member when ownerType is USER`() {
        // Given
        val userId = 1L
        val eventRequest = EventRequest(
            ownerId = userId,
            ownerType = OwnerType.USER,
            title = "Test Event",
            description = "Test Description",
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(12, 0),
            date = LocalDate.now()
        )
        val savedEvent = Event(
            ownerId = userId,
            ownerType = OwnerType.USER,
            title = "Test Event",
            description = "Test Description",
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(12, 0),
            date = LocalDate.now()
        ).apply { id = 1L }
        val eventResponse = EventResponse(
            id = 1L,
            ownerId = userId,
            ownerType = OwnerType.USER,
            title = "Test Event",
            description = "Test Description",
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(12, 0),
            date = LocalDate.now(),
            createdAt = null
        )

        every { eventDao.save(any()) } returns savedEvent
        every { eventMembersDao.save(any()) } returns mockk<EventMembers>()
        every { eventMapper.entityToResponse(any()) } returns eventResponse

        // When
        val result = eventService.create(eventRequest, userId)

        // Then
        assertNotNull(result)
        assertEquals(eventResponse.id, result.id)
        assertEquals(eventResponse.title, result.title)
        
        val eventSlot = slot<Event>()
        verify(exactly = 1) { eventDao.save(capture(eventSlot)) }
        verify(exactly = 1) { eventMembersDao.save(any()) }
        verify(exactly = 1) { eventMapper.entityToResponse(any()) }
        
        assertEquals(eventRequest.title, eventSlot.captured.title)
        assertEquals(eventRequest.ownerId, eventSlot.captured.ownerId)
        assertEquals(eventRequest.ownerType, eventSlot.captured.ownerType)
    }

    @Test
    fun `create should create event without adding member when ownerType is GROUP`() {
        // Given
        val userId = 1L
        val groupId = 2L
        val eventRequest = EventRequest(
            ownerId = groupId,
            ownerType = OwnerType.GROUP,
            title = "Group Event",
            description = "Group Description",
            visibility = Visibility.GROUP,
            time = LocalTime.of(14, 0),
            date = LocalDate.now()
        )
        val savedEvent = Event(
            ownerId = groupId,
            ownerType = OwnerType.GROUP,
            title = "Group Event",
            description = "Group Description",
            visibility = Visibility.GROUP,
            time = LocalTime.of(14, 0),
            date = LocalDate.now()
        ).apply { id = 1L }
        val eventResponse = EventResponse(
            id = 1L,
            ownerId = groupId,
            ownerType = OwnerType.GROUP,
            title = "Group Event",
            description = "Group Description",
            visibility = Visibility.GROUP,
            time = LocalTime.of(14, 0),
            date = LocalDate.now(),
            createdAt = null
        )

        every { eventDao.save(any()) } returns savedEvent
        every { eventMapper.entityToResponse(any()) } returns eventResponse

        // When
        val result = eventService.create(eventRequest, userId)

        // Then
        assertNotNull(result)
        assertEquals(eventResponse.id, result.id)
        verify(exactly = 1) { eventDao.save(any()) }
        verify(exactly = 0) { eventMembersDao.save(any()) }
        verify(exactly = 1) { eventMapper.entityToResponse(any()) }
    }

    @Test
    fun `getById should return event when found`() {
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
        val eventResponse = EventResponse(
            id = eventId,
            ownerId = 1L,
            ownerType = OwnerType.USER,
            title = "Test Event",
            description = null,
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(12, 0),
            date = LocalDate.now(),
            createdAt = null
        )

        every { eventDao.findById(eventId) } returns Optional.of(event)
        every { eventMapper.entityToResponse(event) } returns eventResponse

        // When
        val result = eventService.getById(eventId)

        // Then
        assertNotNull(result)
        assertEquals(eventResponse.id, result.id)
        assertEquals(eventResponse.title, result.title)
        verify(exactly = 1) { eventDao.findById(eventId) }
        verify(exactly = 1) { eventMapper.entityToResponse(event) }
    }

    @Test
    fun `getById should throw ResourceNotFoundException when event not found`() {
        // Given
        val eventId = 999L

        every { eventDao.findById(eventId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            eventService.getById(eventId)
        }
        verify(exactly = 1) { eventDao.findById(eventId) }
        verify(exactly = 0) { eventMapper.entityToResponse(any()) }
    }

    @Test
    fun `getAll should return all events`() {
        // Given
        val events = listOf(
            Event(1L, OwnerType.USER, "Event 1", null, Visibility.EVERYONE, LocalDate.now(), LocalTime.now()).apply { id = 1L },
            Event(2L, OwnerType.GROUP, "Event 2", "Desc", Visibility.GROUP, LocalDate.now(), LocalTime.now()).apply { id = 2L }
        )
        val eventResponses = listOf(
            EventResponse(1L, 1L, OwnerType.USER, "Event 1", null, Visibility.EVERYONE, LocalTime.now(), LocalDate.now(), null),
            EventResponse(2L, 2L, OwnerType.GROUP, "Event 2", "Desc", Visibility.GROUP, LocalTime.now(), LocalDate.now(), null)
        )

        every { eventDao.findAll() } returns events
        every { eventMapper.entityToResponse(events[0]) } returns eventResponses[0]
        every { eventMapper.entityToResponse(events[1]) } returns eventResponses[1]

        // When
        val result = eventService.getAll()

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { eventDao.findAll() }
        verify(exactly = 2) { eventMapper.entityToResponse(any()) }
    }

    @Test
    fun `update should update event successfully`() {
        // Given
        val eventId = 1L
        val existingEvent = Event(
            ownerId = 1L,
            ownerType = OwnerType.USER,
            title = "Old Title",
            description = "Old Description",
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(10, 0),
            date = LocalDate.now()
        ).apply { id = eventId }
        val updateRequest = EventRequest(
            ownerId = 2L,
            ownerType = OwnerType.GROUP,
            title = "New Title",
            description = "New Description",
            visibility = Visibility.FRIENDS,
            time = LocalTime.of(15, 0),
            date = LocalDate.now().plusDays(1)
        )
        val updatedEventResponse = EventResponse(
            id = eventId,
            ownerId = 2L,
            ownerType = OwnerType.GROUP,
            title = "New Title",
            description = "New Description",
            visibility = Visibility.FRIENDS,
            time = LocalTime.of(15, 0),
            date = LocalDate.now().plusDays(1),
            createdAt = null
        )

        every { eventDao.findById(eventId) } returns Optional.of(existingEvent)
        every { eventDao.save(any()) } returns existingEvent
        every { eventMapper.entityToResponse(any()) } returns updatedEventResponse

        // When
        val result = eventService.update(eventId, updateRequest)

        // Then
        assertNotNull(result)
        assertEquals(updateRequest.title, result.title)
        assertEquals(updateRequest.ownerId, result.ownerId)
        verify(exactly = 1) { eventDao.findById(eventId) }
        verify(exactly = 1) { eventDao.save(any()) }
        verify(exactly = 1) { eventMapper.entityToResponse(any()) }
    }

    @Test
    fun `delete should delete event successfully`() {
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

        every { eventDao.findById(eventId) } returns Optional.of(event)
        every { eventDao.delete(event) } returns Unit

        // When
        eventService.delete(eventId)

        // Then
        verify(exactly = 1) { eventDao.findById(eventId) }
        verify(exactly = 1) { eventDao.delete(event) }
    }

    @Test
    fun `getByOwnerId should return events for specific owner`() {
        // Given
        val ownerId = 1L
        val ownerType = OwnerType.USER
        val events = listOf(
            Event(ownerId, ownerType, "Event 1", null, Visibility.EVERYONE, LocalDate.now(), LocalTime.now()).apply { id = 1L },
            Event(ownerId, ownerType, "Event 2", null, Visibility.FRIENDS, LocalDate.now(), LocalTime.now()).apply { id = 2L }
        )
        val eventResponses = listOf(
            EventResponse(1L, ownerId, ownerType, "Event 1", null, Visibility.EVERYONE, LocalTime.now(), LocalDate.now(), null),
            EventResponse(2L, ownerId, ownerType, "Event 2", null, Visibility.FRIENDS, LocalTime.now(), LocalDate.now(), null)
        )

        every { eventDao.findAllByOwnerIdAndOwnerType(ownerId, ownerType) } returns events
        every { eventMapper.entityToResponse(events[0]) } returns eventResponses[0]
        every { eventMapper.entityToResponse(events[1]) } returns eventResponses[1]

        // When
        val result = eventService.getByOwnerId(ownerId, ownerType)

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { eventDao.findAllByOwnerIdAndOwnerType(ownerId, ownerType) }
        verify(exactly = 2) { eventMapper.entityToResponse(any()) }
    }

    @Test
    fun `getByOwnerIds should return events for multiple owners`() {
        // Given
        val ownerIds = listOf(1L, 2L)
        val ownerType = OwnerType.USER
        val events = listOf(
            Event(1L, ownerType, "Event 1", null, Visibility.EVERYONE, LocalDate.now(), LocalTime.now()).apply { id = 1L },
            Event(2L, ownerType, "Event 2", null, Visibility.FRIENDS, LocalDate.now(), LocalTime.now()).apply { id = 2L }
        )
        val eventResponses = listOf(
            EventResponse(1L, 1L, ownerType, "Event 1", null, Visibility.EVERYONE, LocalTime.now(), LocalDate.now(), null),
            EventResponse(2L, 2L, ownerType, "Event 2", null, Visibility.FRIENDS, LocalTime.now(), LocalDate.now(), null)
        )

        every { eventDao.findAllByOwnerIdInAndOwnerType(ownerIds, ownerType) } returns events
        every { eventMapper.entityToResponse(events[0]) } returns eventResponses[0]
        every { eventMapper.entityToResponse(events[1]) } returns eventResponses[1]

        // When
        val result = eventService.getByOwnerIds(ownerIds, ownerType)

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { eventDao.findAllByOwnerIdInAndOwnerType(ownerIds, ownerType) }
        verify(exactly = 2) { eventMapper.entityToResponse(any()) }
    }
}

