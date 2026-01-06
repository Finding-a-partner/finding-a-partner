package com.findingpartners.user_service.model.request

data class UserRequest(
    var description: String?,
    var email: String,
    var login: String,
    var name: String,
    var surname: String
)
