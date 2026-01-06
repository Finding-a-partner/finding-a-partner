package com.finding_a_partner.feed_service.service

import com.finding_a_partner.feed_service.model.FeedResponse

interface FeedService {
    fun getFeedForUser(userId: Long): List<FeedResponse>
}