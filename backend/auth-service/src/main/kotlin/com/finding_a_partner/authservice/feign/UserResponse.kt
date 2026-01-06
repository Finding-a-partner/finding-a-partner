package com.finding_a_partner.authservice.feign

data class UserResponse(
    var id: Long,
    var name: String,
    var surname: String,
    var email: String,
    var login: String,
    var description: String? = null,
    var createdAt: String,
)
