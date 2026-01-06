package com.finding_a_partner.feed_service.service.impl

import com.finding_a_partner.feed_service.enum.FriendshipStatus
import com.finding_a_partner.feed_service.enum.OwnerType
import com.finding_a_partner.feed_service.enum.Visibility
import com.finding_a_partner.feed_service.feign.client.EventClient
import com.finding_a_partner.feed_service.feign.client.GroupClient
import com.finding_a_partner.feed_service.feign.client.UserClient
import com.finding_a_partner.feed_service.feign.client.response.EventResponse
import com.finding_a_partner.feed_service.feign.client.response.FriendResponse
import com.finding_a_partner.feed_service.feign.client.response.GroupResponse
import com.finding_a_partner.feed_service.feign.client.response.UserResponse
import com.finding_a_partner.feed_service.mappers.FeedMapper
import com.finding_a_partner.feed_service.model.FeedResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

class FeedServiceImplTest {

    private lateinit var userClient: UserClient
    private lateinit var groupClient: GroupClient
    private lateinit var eventClient: EventClient
    private lateinit var feedMapper: FeedMapper
    private lateinit var feedService: FeedServiceImpl

    @BeforeEach
    fun setUp() {
        userClient = mockk()
        groupClient = mockk()
        eventClient = mockk()
        feedMapper = mockk()
        feedService = FeedServiceImpl(userClient, groupClient, eventClient, feedMapper)
    }

    @Test
    fun `getFeedForUser should return sorted feed with friend and group events`() {
        // Given
        val userId = 1L
        val friend1 = FriendResponse(
            friend = UserResponse(
                id = 2L,
                name = "Friend",
                surname = "One",
                email = "friend1@example.com",
                login = "friend1",
                description = null,
                createdAt = "2024-01-01T00:00:00Z"
            ),
            status = FriendshipStatus.ACCEPTED,
            id = 1L
        )
        val friend2 = FriendResponse(
            friend = UserResponse(
                id = 3L,
                name = "Friend",
                surname = "Two",
                email = "friend2@example.com",
                login = "friend2",
                description = null,
                createdAt = "2024-01-01T00:00:00Z"
            ),
            status = FriendshipStatus.ACCEPTED,
            id = 2L
        )
        val friends = listOf(friend1, friend2)

        val group1 = GroupResponse(
            id = 1L,
            name = "Group 1",
            description = "Description 1",
            creatorUserId = 1L,
            createdAt = OffsetDateTime.now()
        )
        val group2 = GroupResponse(
            id = 2L,
            name = "Group 2",
            description = "Description 2",
            creatorUserId = 2L,
            createdAt = OffsetDateTime.now()
        )
        val groups = listOf(group1, group2)

        val friendEvent1 = EventResponse(
            id = 1L,
            ownerId = 2L,
            ownerType = OwnerType.USER,
            title = "Friend Event 1",
            description = null,
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(10, 0),
            date = LocalDate.now(),
            createdAt = OffsetDateTime.now().minusDays(1)
        )
        val friendEvent2 = EventResponse(
            id = 2L,
            ownerId = 3L,
            ownerType = OwnerType.USER,
            title = "Friend Event 2",
            description = null,
            visibility = Visibility.FRIENDS,
            time = LocalTime.of(14, 0),
            date = LocalDate.now(),
            createdAt = OffsetDateTime.now()
        )
        val friendEvents = listOf(friendEvent1, friendEvent2)

        val groupEvent1 = EventResponse(
            id = 3L,
            ownerId = 1L,
            ownerType = OwnerType.GROUP,
            title = "Group Event 1",
            description = null,
            visibility = Visibility.GROUP,
            time = LocalTime.of(16, 0),
            date = LocalDate.now(),
            createdAt = OffsetDateTime.now().minusHours(2)
        )
        val groupEvent2 = EventResponse(
            id = 4L,
            ownerId = 2L,
            ownerType = OwnerType.GROUP,
            title = "Group Event 2",
            description = null,
            visibility = Visibility.GROUP,
            time = LocalTime.of(18, 0),
            date = LocalDate.now(),
            createdAt = OffsetDateTime.now().minusHours(1)
        )
        val groupEvents = listOf(groupEvent1, groupEvent2)

        val feedResponse1 = FeedResponse(
            id = 2L,
            createdAt = friendEvent2.createdAt,
            ownerId = 3L,
            ownerType = OwnerType.USER,
            title = "Friend Event 2",
            description = null,
            visibility = Visibility.FRIENDS,
            time = LocalTime.of(14, 0),
            date = LocalDate.now()
        )
        val feedResponse2 = FeedResponse(
            id = 4L,
            createdAt = groupEvent2.createdAt,
            ownerId = 2L,
            ownerType = OwnerType.GROUP,
            title = "Group Event 2",
            description = null,
            visibility = Visibility.GROUP,
            time = LocalTime.of(18, 0),
            date = LocalDate.now()
        )
        val feedResponse3 = FeedResponse(
            id = 3L,
            createdAt = groupEvent1.createdAt,
            ownerId = 1L,
            ownerType = OwnerType.GROUP,
            title = "Group Event 1",
            description = null,
            visibility = Visibility.GROUP,
            time = LocalTime.of(16, 0),
            date = LocalDate.now()
        )
        val feedResponse4 = FeedResponse(
            id = 1L,
            createdAt = friendEvent1.createdAt,
            ownerId = 2L,
            ownerType = OwnerType.USER,
            title = "Friend Event 1",
            description = null,
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(10, 0),
            date = LocalDate.now()
        )

        every { userClient.getRequests(userId, FriendshipStatus.ACCEPTED) } returns friends
        every { eventClient.getByOwnerTypeBatch(OwnerType.USER, listOf(2L, 3L)) } returns friendEvents
        every { groupClient.getUserGroups(userId) } returns groups
        every { eventClient.getByOwnerTypeBatch(OwnerType.GROUP, listOf(1L, 2L)) } returns groupEvents
        every { feedMapper.entityToResponse(friendEvent2) } returns feedResponse1
        every { feedMapper.entityToResponse(groupEvent2) } returns feedResponse2
        every { feedMapper.entityToResponse(groupEvent1) } returns feedResponse3
        every { feedMapper.entityToResponse(friendEvent1) } returns feedResponse4

        // When
        val result = feedService.getFeedForUser(userId)

        // Then
        assertEquals(4, result.size)
        // Проверяем, что события отсортированы по дате создания (от новых к старым)
        assertEquals(feedResponse1.id, result[0].id) // Самое новое
        assertEquals(feedResponse4.id, result[3].id) // Самое старое
        
        verify(exactly = 1) { userClient.getRequests(userId, FriendshipStatus.ACCEPTED) }
        verify(exactly = 1) { eventClient.getByOwnerTypeBatch(OwnerType.USER, listOf(2L, 3L)) }
        verify(exactly = 1) { groupClient.getUserGroups(userId) }
        verify(exactly = 1) { eventClient.getByOwnerTypeBatch(OwnerType.GROUP, listOf(1L, 2L)) }
        verify(exactly = 4) { feedMapper.entityToResponse(any()) }
    }

    @Test
    fun `getFeedForUser should return empty list when user has no friends and groups`() {
        // Given
        val userId = 1L

        every { userClient.getRequests(userId, FriendshipStatus.ACCEPTED) } returns emptyList()
        every { groupClient.getUserGroups(userId) } returns emptyList()

        // When
        val result = feedService.getFeedForUser(userId)

        // Then
        assertTrue(result.isEmpty())
        verify(exactly = 1) { userClient.getRequests(userId, FriendshipStatus.ACCEPTED) }
        verify(exactly = 1) { groupClient.getUserGroups(userId) }
        verify(exactly = 0) { eventClient.getByOwnerTypeBatch(any(), any()) }
        verify(exactly = 0) { feedMapper.entityToResponse(any()) }
    }

    @Test
    fun `getFeedForUser should return only friend events when user has no groups`() {
        // Given
        val userId = 1L
        val friend = FriendResponse(
            friend = UserResponse(
                id = 2L,
                name = "Friend",
                surname = "One",
                email = "friend@example.com",
                login = "friend",
                description = null,
                createdAt = "2024-01-01T00:00:00Z"
            ),
            status = FriendshipStatus.ACCEPTED,
            id = 1L
        )
        val friends = listOf(friend)

        val friendEvent = EventResponse(
            id = 1L,
            ownerId = 2L,
            ownerType = OwnerType.USER,
            title = "Friend Event",
            description = null,
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(12, 0),
            date = LocalDate.now(),
            createdAt = OffsetDateTime.now()
        )

        val feedResponse = FeedResponse(
            id = 1L,
            createdAt = friendEvent.createdAt,
            ownerId = 2L,
            ownerType = OwnerType.USER,
            title = "Friend Event",
            description = null,
            visibility = Visibility.EVERYONE,
            time = LocalTime.of(12, 0),
            date = LocalDate.now()
        )

        every { userClient.getRequests(userId, FriendshipStatus.ACCEPTED) } returns friends
        every { eventClient.getByOwnerTypeBatch(OwnerType.USER, listOf(2L)) } returns listOf(friendEvent)
        every { groupClient.getUserGroups(userId) } returns emptyList()
        every { feedMapper.entityToResponse(friendEvent) } returns feedResponse

        // When
        val result = feedService.getFeedForUser(userId)

        // Then
        assertEquals(1, result.size)
        assertEquals(feedResponse.id, result[0].id)
        verify(exactly = 1) { userClient.getRequests(userId, FriendshipStatus.ACCEPTED) }
        verify(exactly = 1) { eventClient.getByOwnerTypeBatch(OwnerType.USER, listOf(2L)) }
        verify(exactly = 1) { groupClient.getUserGroups(userId) }
        verify(exactly = 0) { eventClient.getByOwnerTypeBatch(OwnerType.GROUP, any()) }
        verify(exactly = 1) { feedMapper.entityToResponse(any()) }
    }

    @Test
    fun `getFeedForUser should return only group events when user has no friends`() {
        // Given
        val userId = 1L
        val group = GroupResponse(
            id = 1L,
            name = "Group 1",
            description = "Description",
            creatorUserId = 1L,
            createdAt = OffsetDateTime.now()
        )
        val groups = listOf(group)

        val groupEvent = EventResponse(
            id = 1L,
            ownerId = 1L,
            ownerType = OwnerType.GROUP,
            title = "Group Event",
            description = null,
            visibility = Visibility.GROUP,
            time = LocalTime.of(12, 0),
            date = LocalDate.now(),
            createdAt = OffsetDateTime.now()
        )

        val feedResponse = FeedResponse(
            id = 1L,
            createdAt = groupEvent.createdAt,
            ownerId = 1L,
            ownerType = OwnerType.GROUP,
            title = "Group Event",
            description = null,
            visibility = Visibility.GROUP,
            time = LocalTime.of(12, 0),
            date = LocalDate.now()
        )

        every { userClient.getRequests(userId, FriendshipStatus.ACCEPTED) } returns emptyList()
        every { groupClient.getUserGroups(userId) } returns groups
        every { eventClient.getByOwnerTypeBatch(OwnerType.GROUP, listOf(1L)) } returns listOf(groupEvent)
        every { feedMapper.entityToResponse(groupEvent) } returns feedResponse

        // When
        val result = feedService.getFeedForUser(userId)

        // Then
        assertEquals(1, result.size)
        assertEquals(feedResponse.id, result[0].id)
        verify(exactly = 1) { userClient.getRequests(userId, FriendshipStatus.ACCEPTED) }
        verify(exactly = 0) { eventClient.getByOwnerTypeBatch(OwnerType.USER, any()) }
        verify(exactly = 1) { groupClient.getUserGroups(userId) }
        verify(exactly = 1) { eventClient.getByOwnerTypeBatch(OwnerType.GROUP, listOf(1L)) }
        verify(exactly = 1) { feedMapper.entityToResponse(any()) }
    }
}

