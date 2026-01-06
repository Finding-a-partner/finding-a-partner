package com.finding_a_partner.authservice.controller

import com.finding_a_partner.authservice.config.JwtUtils
import com.finding_a_partner.authservice.feign.UserClient
import com.finding_a_partner.authservice.feign.UserResponse
import com.finding_a_partner.authservice.model.AuthResponse
import com.finding_a_partner.authservice.model.AuthenticationRequest
import com.finding_a_partner.authservice.model.RegisterRequest
import com.finding_a_partner.authservice.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class AuthControllerTest {

    private lateinit var userService: UserService
    private lateinit var jwtUtils: JwtUtils
    private lateinit var userClient: UserClient
    private lateinit var authController: AuthController

    @BeforeEach
    fun setUp() {
        userService = mockk()
        jwtUtils = mockk()
        userClient = mockk()
        authController = AuthController(userService, jwtUtils, userClient)
    }

    @Test
    fun `register should return CREATED with token when registration is successful`() {
        // Given
        val registerRequest = RegisterRequest(
            login = "newuser",
            email = "new@example.com",
            password = "password123",
            role = null,
            name = "New",
            surname = "User",
            description = null
        )
        val userResponse = UserResponse(
            id = 1L,
            name = "New",
            surname = "User",
            email = "new@example.com",
            login = "newuser",
            description = null,
            createdAt = "2024-01-01T00:00:00Z"
        )
        val token = "test_token"

        every { userService.registerUser(registerRequest) } returns userResponse
        every { jwtUtils.generateToken(userResponse.login, userResponse.id.toString()) } returns token

        // When
        val response = authController.register(registerRequest)

        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        val body = response.body as AuthResponse
        assertEquals(token, body.accessToken)
        assertEquals(userResponse.id, body.user.id)
        verify(exactly = 1) { userService.registerUser(registerRequest) }
        verify(exactly = 1) { jwtUtils.generateToken(userResponse.login, userResponse.id.toString()) }
    }

    @Test
    fun `register should return BAD_REQUEST when user already exists`() {
        // Given
        val registerRequest = RegisterRequest(
            login = "existinguser",
            email = "existing@example.com",
            password = "password123",
            role = null,
            name = "Existing",
            surname = "User",
            description = null
        )

        every { userService.registerUser(registerRequest) } throws IllegalArgumentException("Пользователь с таким login уже существует.")

        // When
        val response = authController.register(registerRequest)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Пользователь с таким login уже существует.", response.body)
        verify(exactly = 1) { userService.registerUser(registerRequest) }
        verify(exactly = 0) { jwtUtils.generateToken(any(), any()) }
    }

    @Test
    fun `login should return OK with token when credentials are valid`() {
        // Given
        val authRequest = AuthenticationRequest(
            login = "testuser",
            password = "password123"
        )
        val userResponse = UserResponse(
            id = 1L,
            name = "Test",
            surname = "User",
            email = "test@example.com",
            login = "testuser",
            description = null,
            createdAt = "2024-01-01T00:00:00Z"
        )
        val token = "test_token"

        every { userService.authenticate(authRequest.login, authRequest.password) } returns userResponse
        every { jwtUtils.generateToken(userResponse.login, userResponse.id.toString()) } returns token

        // When
        val response = authController.login(authRequest)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        val body = response.body as AuthResponse
        assertEquals(token, body.accessToken)
        assertEquals(userResponse.id, body.user.id)
        verify(exactly = 1) { userService.authenticate(authRequest.login, authRequest.password) }
        verify(exactly = 1) { jwtUtils.generateToken(userResponse.login, userResponse.id.toString()) }
    }

    @Test
    fun `login should return UNAUTHORIZED when credentials are invalid`() {
        // Given
        val authRequest = AuthenticationRequest(
            login = "testuser",
            password = "wrong_password"
        )

        every { userService.authenticate(authRequest.login, authRequest.password) } returns null

        // When
        val response = authController.login(authRequest)

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("Неверное имя пользователя или пароль", response.body)
        verify(exactly = 1) { userService.authenticate(authRequest.login, authRequest.password) }
        verify(exactly = 0) { jwtUtils.generateToken(any(), any()) }
    }

    @Test
    fun `verifyToken should return OK when token is valid`() {
        // Given
        val token = "valid_token"
        val username = "testuser"

        every { jwtUtils.extractUsername(token) } returns username
        every { jwtUtils.validateToken(token, username) } returns true

        // When
        val response = authController.verifyToken(token)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Token valid for user: $username", response.body)
        verify(exactly = 1) { jwtUtils.extractUsername(token) }
        verify(exactly = 1) { jwtUtils.validateToken(token, username) }
    }

    @Test
    fun `verifyToken should return UNAUTHORIZED when token is invalid`() {
        // Given
        val token = "invalid_token"
        val username = "testuser"

        every { jwtUtils.extractUsername(token) } returns username
        every { jwtUtils.validateToken(token, username) } returns false

        // When
        val response = authController.verifyToken(token)

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("Invalid token", response.body)
        verify(exactly = 1) { jwtUtils.extractUsername(token) }
        verify(exactly = 1) { jwtUtils.validateToken(token, username) }
    }

    @Test
    fun `logout should return OK when authorization header is valid`() {
        // Given
        val authorization = "Bearer test_token"

        // When
        val response = authController.logout(authorization)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Logged out successfully", response.body)
    }

    @Test
    fun `logout should return BAD_REQUEST when authorization header is missing`() {
        // When
        val response = authController.logout(null)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Missing or invalid Authorization header", response.body)
    }

    @Test
    fun `logout should return BAD_REQUEST when authorization header is invalid`() {
        // Given
        val authorization = "InvalidHeader test_token"

        // When
        val response = authController.logout(authorization)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Missing or invalid Authorization header", response.body)
    }
}

