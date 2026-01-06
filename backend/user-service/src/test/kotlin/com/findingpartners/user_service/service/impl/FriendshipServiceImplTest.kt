package com.findingpartners.user_service.service.impl

import com.findingpartners.user_service.database.entity.Friendship
import com.findingpartners.user_service.database.entity.User
import com.findingpartners.user_service.database.repository.FriendshipDao
import com.findingpartners.user_service.database.repository.UserDao
import com.findingpartners.user_service.enum.FriendshipStatus
import com.findingpartners.user_service.model.request.FriendshipRequest
import com.findingpartners.user_service.model.response.FriendResponse
import com.findingpartners.user_service.model.response.FriendshipResponse
import com.findingpartners.user_service.util.FriendshipMapper
import com.findingpartners.user_service.util.UserMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

class FriendshipServiceImplTest {

    private lateinit var friendshipDao: FriendshipDao
    private lateinit var userDao: UserDao
    private lateinit var userMapper: UserMapper
    private lateinit var friendshipMapper: FriendshipMapper
    private lateinit var friendshipService: FriendshipServiceImpl

    @BeforeEach
    fun setUp() {
        friendshipDao = mockk()
        userDao = mockk()
        userMapper = mockk()
        friendshipMapper = mockk()
        friendshipService = FriendshipServiceImpl(friendshipDao, userDao, userMapper, friendshipMapper)
    }

    @Test
    fun `sendFriendRequest should create friendship request successfully`() {
        // Given
        val userId = 1L
        val friendId = 2L
        val request = FriendshipRequest(
            userId = userId,
            friendId = friendId,
            status = FriendshipStatus.PENDING
        )
        val sender = User(1L, "user1", "user1@example.com", null, "User", "One", LocalDateTime.now())
        val receiver = User(2L, "user2", "user2@example.com", null, "User", "Two", LocalDateTime.now())
        val friendship = Friendship(
            user1 = sender,
            user2 = receiver,
            statusForUser1 = FriendshipStatus.PENDING,
            statusForUser2 = FriendshipStatus.NO_CONNECTION
        ).apply { id = 1L }
        val response = FriendshipResponse(
            id = 1L,
            user1Id = userId,
            user2Id = friendId,
            statusForUser1 = FriendshipStatus.PENDING,
            statusForUser2 = FriendshipStatus.NO_CONNECTION
        )

        every { userDao.findById(userId) } returns Optional.of(sender)
        every { userDao.findById(friendId) } returns Optional.of(receiver)
        every { friendshipDao.findBetweenUsers(userId, friendId) } returns null
        every { friendshipDao.save(any()) } returns friendship
        every { friendshipMapper.entityToResponse(friendship) } returns response

        // When
        val result = friendshipService.sendFriendRequest(request)

        // Then
        assertNotNull(result)
        assertEquals(response.id, result.id)
        verify(exactly = 1) { userDao.findById(userId) }
        verify(exactly = 1) { userDao.findById(friendId) }
        verify(exactly = 1) { friendshipDao.findBetweenUsers(userId, friendId) }
        verify(exactly = 1) { friendshipDao.save(any()) }
    }

    @Test
    fun `sendFriendRequest should throw exception when trying to send request to yourself`() {
        // Given
        val userId = 1L
        val request = FriendshipRequest(
            userId = userId,
            friendId = userId,
            status = FriendshipStatus.PENDING
        )

        // When & Then
        assertThrows<IllegalArgumentException> {
            friendshipService.sendFriendRequest(request)
        }
        verify(exactly = 0) { userDao.findById(any()) }
        verify(exactly = 0) { friendshipDao.save(any()) }
    }

    @Test
    fun `respondToFriendRequest should accept friend request successfully`() {
        // Given
        val requestId = 1L
        val userId = 1L
        val friendId = 2L
        val request = FriendshipRequest(
            userId = userId,
            friendId = friendId,
            status = FriendshipStatus.ACCEPTED
        )
        val user1 = User(1L, "user1", "user1@example.com", null, "User", "One", LocalDateTime.now())
        val user2 = User(2L, "user2", "user2@example.com", null, "User", "Two", LocalDateTime.now())
        val friendship = Friendship(
            user1 = user1,
            user2 = user2,
            statusForUser1 = FriendshipStatus.PENDING,
            statusForUser2 = FriendshipStatus.NO_CONNECTION
        ).apply { id = requestId }
        val response = FriendshipResponse(
            id = requestId,
            user1Id = userId,
            user2Id = friendId,
            statusForUser1 = FriendshipStatus.ACCEPTED,
            statusForUser2 = FriendshipStatus.ACCEPTED
        )

        every { friendshipDao.findById(requestId) } returns Optional.of(friendship)
        every { friendshipDao.save(any()) } returns friendship
        every { friendshipMapper.entityToResponse(any()) } returns response

        // When
        val result = friendshipService.respondToFriendRequest(requestId, request)

        // Then
        assertNotNull(result)
        assertEquals(FriendshipStatus.ACCEPTED, result.statusForUser1)
        assertEquals(FriendshipStatus.ACCEPTED, result.statusForUser2)
        verify(exactly = 1) { friendshipDao.findById(requestId) }
        verify(exactly = 1) { friendshipDao.save(any()) }
    }

    @Test
    fun `getUserRequests should return friends with ACCEPTED status`() {
        // Given
        val userId = 1L
        val user = User(1L, "user1", "user1@example.com", null, "User", "One", LocalDateTime.now())
        val friend = User(2L, "user2", "user2@example.com", null, "User", "Two", LocalDateTime.now())
        val friendship = Friendship(
            user1 = user,
            user2 = friend,
            statusForUser1 = FriendshipStatus.ACCEPTED,
            statusForUser2 = FriendshipStatus.ACCEPTED
        ).apply { id = 1L }
        val friendResponse = FriendResponse(
            friend = com.findingpartners.user_service.model.response.UserResponse(
                2L, "user2", "user2@example.com", null, "User", "Two", "2024-01-01T00:00:00"
            ),
            status = FriendshipStatus.ACCEPTED,
            id = 1L
        )

        every { userDao.findById(userId) } returns Optional.of(user)
        every { friendshipDao.findByUserAndStatus(userId, FriendshipStatus.ACCEPTED) } returns listOf(friendship)
        every { userMapper.entityToResponse(friend) } returns friendResponse.friend
        every { friendship.getOtherUser(userId) } returns friend
        every { friendship.getStatusForUser(userId) } returns FriendshipStatus.ACCEPTED

        // When
        val result = friendshipService.getUserRequests(userId, FriendshipStatus.ACCEPTED)

        // Then
        assertEquals(1, result.size)
        assertEquals(friendResponse.status, result[0].status)
        verify(exactly = 1) { userDao.findById(userId) }
        verify(exactly = 1) { friendshipDao.findByUserAndStatus(userId, FriendshipStatus.ACCEPTED) }
    }

    @Test
    fun `deleteFriend should delete friendship successfully`() {
        // Given
        val currentUserId = 1L
        val friendId = 2L
        val currentUser = User(1L, "user1", "user1@example.com", null, "User", "One", LocalDateTime.now())
        val friend = User(2L, "user2", "user2@example.com", null, "User", "Two", LocalDateTime.now())
        val friendship = Friendship(
            user1 = currentUser,
            user2 = friend,
            statusForUser1 = FriendshipStatus.ACCEPTED,
            statusForUser2 = FriendshipStatus.ACCEPTED
        ).apply { id = 1L }

        every { userDao.findById(currentUserId) } returns Optional.of(currentUser)
        every { userDao.findById(friendId) } returns Optional.of(friend)
        every { friendshipDao.findBetweenUsers(currentUserId, friendId) } returns friendship
        every { friendshipDao.delete(friendship) } returns Unit

        // When
        friendshipService.deleteFriend(currentUserId, friendId)

        // Then
        verify(exactly = 1) { userDao.findById(currentUserId) }
        verify(exactly = 1) { userDao.findById(friendId) }
        verify(exactly = 1) { friendshipDao.findBetweenUsers(currentUserId, friendId) }
        verify(exactly = 1) { friendshipDao.delete(friendship) }
    }

    @Test
    fun `deleteFriend should throw exception when friendship not found`() {
        // Given
        val currentUserId = 1L
        val friendId = 2L
        val currentUser = User(1L, "user1", "user1@example.com", null, "User", "One", LocalDateTime.now())
        val friend = User(2L, "user2", "user2@example.com", null, "User", "Two", LocalDateTime.now())

        every { userDao.findById(currentUserId) } returns Optional.of(currentUser)
        every { userDao.findById(friendId) } returns Optional.of(friend)
        every { friendshipDao.findBetweenUsers(currentUserId, friendId) } returns null

        // When & Then
        assertThrows<jakarta.ws.rs.NotFoundException> {
            friendshipService.deleteFriend(currentUserId, friendId)
        }
        verify(exactly = 1) { userDao.findById(currentUserId) }
        verify(exactly = 1) { userDao.findById(friendId) }
        verify(exactly = 1) { friendshipDao.findBetweenUsers(currentUserId, friendId) }
        verify(exactly = 0) { friendshipDao.delete(any()) }
    }
}

