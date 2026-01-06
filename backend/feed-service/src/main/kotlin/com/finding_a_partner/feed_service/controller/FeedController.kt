package com.finding_a_partner.feed_service.controller

import com.finding_a_partner.feed_service.model.FeedResponse
import com.finding_a_partner.feed_service.service.FeedService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/feed")
class FeedController(
    val feedService: FeedService
) {
    @GetMapping
    fun getFeed(@RequestHeader("X-User-Id") userId: Long): List<FeedResponse> {
        return feedService.getFeedForUser(userId)
    }

    @GetMapping("/user/{userId}")
    fun getFeedByUserId(@PathVariable("userId") userId: Long): List<FeedResponse> {
        return feedService.getFeedForUser(userId)
    }
}