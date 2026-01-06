package com.findingpartners.event_service.model.response

import com.findingpartners.event_service.enum.OwnerType

data class OwnerResponse (
    val id: Long,
    val type: OwnerType,
    val login: String? = null,
    val name: String,
    val surname: String? = null,
)