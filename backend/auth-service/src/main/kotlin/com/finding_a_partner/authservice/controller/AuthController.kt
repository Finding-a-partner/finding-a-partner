package com.finding_a_partner.authservice.controller

import com.finding_a_partner.authservice.config.JwtUtils
import com.finding_a_partner.authservice.feign.UserClient
import com.finding_a_partner.authservice.model.AuthResponse
import com.finding_a_partner.authservice.model.AuthenticationRequest
import com.finding_a_partner.authservice.model.RegisterRequest
import com.finding_a_partner.authservice.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val jwtUtils: JwtUtils,
        private val userClient: UserClient
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody
        registerRequest: RegisterRequest,
    ): ResponseEntity<Any> {
        return try {
            val user = userService.registerUser(registerRequest)
            val token = jwtUtils.generateToken(user.login, user.id.toString())
            ResponseEntity(AuthResponse(accessToken = token, user = user), HttpStatus.CREATED)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody
        authRequest: AuthenticationRequest,
    ): ResponseEntity<Any> {
        val user = userService.authenticate(authRequest.login, authRequest.password)
        return if (user != null) {
            val token = jwtUtils.generateToken(user.login, user.id.toString())
            ResponseEntity(AuthResponse(accessToken = token, user = user), HttpStatus.OK)
        } else {
            ResponseEntity("Неверное имя пользователя или пароль", HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("/verify")
    fun verifyToken(@RequestParam token: String): ResponseEntity<Any> {
        val username = jwtUtils.extractUsername(token)
        return if (jwtUtils.validateToken(token, username)) {
            ResponseEntity("Token valid for user: $username", HttpStatus.OK)
        } else {
            ResponseEntity("Invalid token", HttpStatus.UNAUTHORIZED)
        }
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestParam token: String): ResponseEntity<Any> {
        val username = jwtUtils.extractUsername(token)
        return if (jwtUtils.validateToken(token, username)) {
            val newToken = jwtUtils.generateToken(username, jwtUtils.extractUserId(token))
            val user = userClient.getByLogin(username)
            ResponseEntity(AuthResponse(accessToken = newToken, user = user), HttpStatus.OK)
        } else {
            ResponseEntity("Invalid token", HttpStatus.UNAUTHORIZED)
        }
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") authorization: String?): ResponseEntity<Any> {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header")
        }
        val token = authorization.substring(7)
        return ResponseEntity.ok("Logged out successfully")
    }
}
