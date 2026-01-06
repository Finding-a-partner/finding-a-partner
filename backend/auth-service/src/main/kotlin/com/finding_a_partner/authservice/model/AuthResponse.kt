package com.finding_a_partner.authservice.model

import com.finding_a_partner.authservice.feign.UserResponse

data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val user: UserResponse,
)
