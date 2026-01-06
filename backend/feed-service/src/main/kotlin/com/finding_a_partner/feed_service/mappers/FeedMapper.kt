package com.finding_a_partner.feed_service.mappers

import com.finding_a_partner.feed_service.feign.client.response.EventResponse
import com.finding_a_partner.feed_service.model.FeedResponse
import org.springframework.stereotype.Component

@Component
class FeedMapper {
    fun entityToResponse(entity: EventResponse): FeedResponse {
        return FeedResponse(
            id = entity.id,
            createdAt = entity.createdAt,
            ownerId = entity.ownerId,
            ownerType = entity.ownerType,
            title = entity.title,
            description = entity.description,
            visibility = entity.visibility,
            time = entity.time,
            date = entity.date,
        )
    }
}
