package com.finding_a_partner.authservice.service.impl

import com.finding_a_partner.authservice.entity.User
import com.finding_a_partner.authservice.feign.UserClient
import com.finding_a_partner.authservice.feign.UserRequest
import com.finding_a_partner.authservice.feign.UserResponse
import com.finding_a_partner.authservice.model.RegisterRequest
import com.finding_a_partner.authservice.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.slot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceImplTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var userClient: UserClient
    private lateinit var userService: UserServiceImpl

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        passwordEncoder = mockk()
        userClient = mockk()
        userService = UserServiceImpl(userRepository, passwordEncoder, userClient)
    }

    @Test
    fun `registerUser should throw exception when user with login already exists`() {
        // Given
        val registerRequest = RegisterRequest(
            login = "testuser",
            email = "test@example.com",
            password = "password123",
            role = null,
            name = "Test",
            surname = "User",
            description = null
        )
        val existingUser = User(
            id = 1L,
            login = "testuser",
            email = "test@example.com",
            password = "encoded",
            role = com.finding_a_partner.authservice.enum.Role.USER
        )

        every { userRepository.findByLogin(registerRequest.login) } returns existingUser

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            userService.registerUser(registerRequest)
        }
        assertEquals("Пользователь с таким login уже существует.", exception.message)
        verify(exactly = 1) { userRepository.findByLogin(registerRequest.login) }
        verify(exactly = 0) { userClient.create(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `registerUser should create new user successfully`() {
        // Given
        val registerRequest = RegisterRequest(
            login = "newuser",
            email = "new@example.com",
            password = "password123",
            role = null,
            name = "New",
            surname = "User",
            description = "Test description"
        )
        val userResponse = UserResponse(
            id = 1L,
            name = "New",
            surname = "User",
            email = "new@example.com",
            login = "newuser",
            description = "Test description",
            createdAt = "2024-01-01T00:00:00Z"
        )
        val savedUser = User(
            id = 1L,
            login = "newuser",
            email = "new@example.com",
            password = "encoded_password",
            role = com.finding_a_partner.authservice.enum.Role.USER
        )

        every { userRepository.findByLogin(registerRequest.login) } returns null
        every { userClient.create(any()) } returns userResponse
        every { passwordEncoder.encode(registerRequest.password) } returns "encoded_password"
        every { userRepository.save(any()) } returns savedUser

        // When
        val result = userService.registerUser(registerRequest)

        // Then
        assertNotNull(result)
        assertEquals(userResponse.id, result.id)
        assertEquals(userResponse.login, result.login)
        
        val userRequestSlot = slot<UserRequest>()
        verify(exactly = 1) { userRepository.findByLogin(registerRequest.login) }
        verify(exactly = 1) { userClient.create(capture(userRequestSlot)) }
        verify(exactly = 1) { passwordEncoder.encode(registerRequest.password) }
        verify(exactly = 1) { userRepository.save(any()) }
        
        assertEquals(registerRequest.name, userRequestSlot.captured.name)
        assertEquals(registerRequest.surname, userRequestSlot.captured.surname)
        assertEquals(registerRequest.email, userRequestSlot.captured.email)
        assertEquals(registerRequest.login, userRequestSlot.captured.login)
    }

    @Test
    fun `authenticate should return user when credentials are valid`() {
        // Given
        val login = "testuser"
        val password = "password123"
        val encodedPassword = "encoded_password"
        val user = User(
            id = 1L,
            login = login,
            email = "test@example.com",
            password = encodedPassword,
            role = com.finding_a_partner.authservice.enum.Role.USER
        )
        val userResponse = UserResponse(
            id = 1L,
            name = "Test",
            surname = "User",
            email = "test@example.com",
            login = login,
            description = null,
            createdAt = "2024-01-01T00:00:00Z"
        )

        every { userRepository.findByLogin(login) } returns user
        every { passwordEncoder.matches(password, encodedPassword) } returns true
        every { userClient.getByLogin(login) } returns userResponse

        // When
        val result = userService.authenticate(login, password)

        // Then
        assertNotNull(result)
        assertEquals(userResponse.id, result!!.id)
        assertEquals(userResponse.login, result.login)
        verify(exactly = 1) { userRepository.findByLogin(login) }
        verify(exactly = 1) { passwordEncoder.matches(password, encodedPassword) }
        verify(exactly = 1) { userClient.getByLogin(login) }
    }

    @Test
    fun `authenticate should return null when user not found`() {
        // Given
        val login = "nonexistent"
        val password = "password123"

        every { userRepository.findByLogin(login) } returns null

        // When
        val result = userService.authenticate(login, password)

        // Then
        assertNull(result)
        verify(exactly = 1) { userRepository.findByLogin(login) }
        verify(exactly = 0) { passwordEncoder.matches(any(), any()) }
        verify(exactly = 0) { userClient.getByLogin(any()) }
    }

    @Test
    fun `authenticate should return null when password is incorrect`() {
        // Given
        val login = "testuser"
        val password = "wrong_password"
        val encodedPassword = "encoded_password"
        val user = User(
            id = 1L,
            login = login,
            email = "test@example.com",
            password = encodedPassword,
            role = com.finding_a_partner.authservice.enum.Role.USER
        )

        every { userRepository.findByLogin(login) } returns user
        every { passwordEncoder.matches(password, encodedPassword) } returns false

        // When
        val result = userService.authenticate(login, password)

        // Then
        assertNull(result)
        verify(exactly = 1) { userRepository.findByLogin(login) }
        verify(exactly = 1) { passwordEncoder.matches(password, encodedPassword) }
        verify(exactly = 0) { userClient.getByLogin(any()) }
    }
}

