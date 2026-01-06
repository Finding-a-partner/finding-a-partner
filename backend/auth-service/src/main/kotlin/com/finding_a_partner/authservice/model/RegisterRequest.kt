package com.finding_a_partner.authservice.model

import com.finding_a_partner.authservice.enum.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RegisterRequest(
    @field:NotBlank(message = "Login не может быть пустым")
    val login: String,

    @field:Email(message = "Email должен быть валидным")
    val email: String,

    @field:NotBlank(message = "Password не может быть пустым")
    val password: String,

    val role: Role?,

    val name: String,
    val surname: String,
    val description: String? = null,
)
