package com.findingpartners.event_service.model.response


data class MemberResponse (
    val id: Long,
    val login: String,
    val name: String,
    val surname: String
)