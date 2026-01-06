package com.finding_a_partner.authservice.model

import jakarta.validation.constraints.NotBlank

data class AuthenticationRequest(
    @field:NotBlank(message = "Login не может быть пустым")
    val login: String,

    @field:NotBlank(message = "Password не может быть пустым")
    val password: String,
)
