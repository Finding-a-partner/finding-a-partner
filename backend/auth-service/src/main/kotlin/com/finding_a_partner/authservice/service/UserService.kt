package com.finding_a_partner.authservice.service

import com.finding_a_partner.authservice.feign.UserResponse
import com.finding_a_partner.authservice.model.RegisterRequest

interface UserService {
    fun registerUser(registerRequest: RegisterRequest): UserResponse

    fun authenticate(login: String, password: String): UserResponse?
}
