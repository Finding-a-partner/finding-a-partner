package com.findingpartners.user_service.service.impl

import com.findingpartners.user_service.database.entity.User
import com.findingpartners.user_service.database.repository.UserDao
import com.findingpartners.user_service.errors.ResourceNotFoundException
import com.findingpartners.user_service.model.request.UserRequest
import com.findingpartners.user_service.model.response.UserResponse
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

class UserServiceImplTest {

    private lateinit var userDao: UserDao
    private lateinit var userMapper: UserMapper
    private lateinit var userService: UserServiceImpl

    @BeforeEach
    fun setUp() {
        userDao = mockk()
        userMapper = mockk()
        userService = UserServiceImpl(userDao, userMapper)
    }

    @Test
    fun `getById should return user when found`() {
        // Given
        val userId = 1L
        val user = User(
            id = userId,
            login = "testuser",
            email = "test@example.com",
            description = "Test description",
            name = "Test",
            surname = "User",
            createdAt = LocalDateTime.now()
        )
        val userResponse = UserResponse(
            id = userId,
            login = "testuser",
            email = "test@example.com",
            description = "Test description",
            name = "Test",
            surname = "User",
            createdAt = "2024-01-01T00:00:00"
        )

        every { userDao.findById(userId) } returns Optional.of(user)
        every { userMapper.entityToResponse(user) } returns userResponse

        // When
        val result = userService.getById(userId)

        // Then
        assertNotNull(result)
        assertEquals(userResponse.id, result.id)
        assertEquals(userResponse.login, result.login)
        verify(exactly = 1) { userDao.findById(userId) }
        verify(exactly = 1) { userMapper.entityToResponse(user) }
    }

    @Test
    fun `getById should throw ResourceNotFoundException when user not found`() {
        // Given
        val userId = 999L

        every { userDao.findById(userId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            userService.getById(userId)
        }
        verify(exactly = 1) { userDao.findById(userId) }
        verify(exactly = 0) { userMapper.entityToResponse(any()) }
    }

    @Test
    fun `getByLogin should return user when found`() {
        // Given
        val login = "testuser"
        val user = User(
            id = 1L,
            login = login,
            email = "test@example.com",
            description = null,
            name = "Test",
            surname = "User",
            createdAt = LocalDateTime.now()
        )
        val userResponse = UserResponse(
            id = 1L,
            login = login,
            email = "test@example.com",
            description = null,
            name = "Test",
            surname = "User",
            createdAt = "2024-01-01T00:00:00"
        )

        every { userDao.getByLogin(login) } returns Optional.of(user)
        every { userMapper.entityToResponse(user) } returns userResponse

        // When
        val result = userService.getByLogin(login)

        // Then
        assertNotNull(result)
        assertEquals(userResponse.login, result.login)
        verify(exactly = 1) { userDao.getByLogin(login) }
        verify(exactly = 1) { userMapper.entityToResponse(user) }
    }

    @Test
    fun `getByLogin should throw RuntimeException when user not found`() {
        // Given
        val login = "nonexistent"

        every { userDao.getByLogin(login) } returns Optional.empty()

        // When & Then
        assertThrows<RuntimeException> {
            userService.getByLogin(login)
        }
        verify(exactly = 1) { userDao.getByLogin(login) }
        verify(exactly = 0) { userMapper.entityToResponse(any()) }
    }

    @Test
    fun `getAll should return all users`() {
        // Given
        val users = listOf(
            User(1L, "user1", "user1@example.com", null, "User", "One", LocalDateTime.now()),
            User(2L, "user2", "user2@example.com", null, "User", "Two", LocalDateTime.now())
        )
        val userResponses = listOf(
            UserResponse(1L, "user1", "user1@example.com", null, "User", "One", "2024-01-01T00:00:00"),
            UserResponse(2L, "user2", "user2@example.com", null, "User", "Two", "2024-01-01T00:00:00")
        )

        every { userDao.findAll() } returns users
        every { userMapper.entityToResponse(users[0]) } returns userResponses[0]
        every { userMapper.entityToResponse(users[1]) } returns userResponses[1]

        // When
        val result = userService.getAll()

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { userDao.findAll() }
        verify(exactly = 2) { userMapper.entityToResponse(any()) }
    }

    @Test
    fun `searchUsers should return users matching query`() {
        // Given
        val query = "test"
        val users = listOf(
            User(1L, "testuser", "test@example.com", null, "Test", "User", LocalDateTime.now()),
            User(2L, "user", "user@example.com", null, "Test", "Name", LocalDateTime.now())
        )
        val userResponses = listOf(
            UserResponse(1L, "testuser", "test@example.com", null, "Test", "User", "2024-01-01T00:00:00"),
            UserResponse(2L, "user", "user@example.com", null, "Test", "Name", "2024-01-01T00:00:00")
        )

        every { userDao.findByLoginContainingOrNameContainingOrSurnameContaining(query, query, query) } returns users
        every { userMapper.entityToResponse(users[0]) } returns userResponses[0]
        every { userMapper.entityToResponse(users[1]) } returns userResponses[1]

        // When
        val result = userService.searchUsers(query)

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { userDao.findByLoginContainingOrNameContainingOrSurnameContaining(query, query, query) }
        verify(exactly = 2) { userMapper.entityToResponse(any()) }
    }

    @Test
    fun `getByIds should return users by ids`() {
        // Given
        val ids = listOf(1L, 2L)
        val users = listOf(
            User(1L, "user1", "user1@example.com", null, "User", "One", LocalDateTime.now()),
            User(2L, "user2", "user2@example.com", null, "User", "Two", LocalDateTime.now())
        )
        val userResponses = listOf(
            UserResponse(1L, "user1", "user1@example.com", null, "User", "One", "2024-01-01T00:00:00"),
            UserResponse(2L, "user2", "user2@example.com", null, "User", "Two", "2024-01-01T00:00:00")
        )

        every { userDao.findAllById(ids) } returns users
        every { userMapper.entityToResponse(users[0]) } returns userResponses[0]
        every { userMapper.entityToResponse(users[1]) } returns userResponses[1]

        // When
        val result = userService.getByIds(ids)

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { userDao.findAllById(ids) }
        verify(exactly = 2) { userMapper.entityToResponse(any()) }
    }

    @Test
    fun `create should create new user successfully`() {
        // Given
        val userRequest = UserRequest(
            login = "newuser",
            email = "new@example.com",
            description = "New user",
            name = "New",
            surname = "User"
        )
        val savedUser = User(
            id = 1L,
            login = "newuser",
            email = "new@example.com",
            description = "New user",
            name = "New",
            surname = "User",
            createdAt = LocalDateTime.now()
        )
        val userResponse = UserResponse(
            id = 1L,
            login = "newuser",
            email = "new@example.com",
            description = "New user",
            name = "New",
            surname = "User",
            createdAt = "2024-01-01T00:00:00"
        )

        every { userDao.save(any()) } returns savedUser
        every { userMapper.entityToResponse(savedUser) } returns userResponse

        // When
        val result = userService.create(userRequest)

        // Then
        assertNotNull(result)
        assertEquals(userResponse.id, result.id)
        assertEquals(userResponse.login, result.login)
        verify(exactly = 1) { userDao.save(any()) }
        verify(exactly = 1) { userMapper.entityToResponse(savedUser) }
    }

    @Test
    fun `update should update user successfully`() {
        // Given
        val userId = 1L
        val existingUser = User(
            id = userId,
            login = "olduser",
            email = "old@example.com",
            description = "Old description",
            name = "Old",
            surname = "User",
            createdAt = LocalDateTime.now()
        )
        val updateRequest = UserRequest(
            login = "newuser",
            email = "new@example.com",
            description = "New description",
            name = "New",
            surname = "User"
        )
        val updatedUserResponse = UserResponse(
            id = userId,
            login = "newuser",
            email = "new@example.com",
            description = "New description",
            name = "New",
            surname = "User",
            createdAt = "2024-01-01T00:00:00"
        )

        every { userDao.findById(userId) } returns Optional.of(existingUser)
        every { userDao.save(any()) } returns existingUser
        every { userMapper.entityToResponse(any()) } returns updatedUserResponse

        // When
        val result = userService.update(userId, updateRequest)

        // Then
        assertNotNull(result)
        assertEquals(updateRequest.login, result.login)
        assertEquals(updateRequest.email, result.email)
        assertEquals(updateRequest.description, result.description)
        verify(exactly = 1) { userDao.findById(userId) }
        verify(exactly = 1) { userDao.save(any()) }
        verify(exactly = 1) { userMapper.entityToResponse(any()) }
    }

    @Test
    fun `delete should delete user successfully`() {
        // Given
        val userId = 1L
        val user = User(
            id = userId,
            login = "testuser",
            email = "test@example.com",
            description = null,
            name = "Test",
            surname = "User",
            createdAt = LocalDateTime.now()
        )

        every { userDao.findById(userId) } returns Optional.of(user)
        every { userDao.delete(user) } returns Unit

        // When
        userService.delete(userId)

        // Then
        verify(exactly = 1) { userDao.findById(userId) }
        verify(exactly = 1) { userDao.delete(user) }
    }
}

