package com.finding_a_partner.authservice.feign

data class UserRequest(
    var name: String,
    var surname: String,
    var email: String,
    var login: String,
    var description: String? = null,
)
