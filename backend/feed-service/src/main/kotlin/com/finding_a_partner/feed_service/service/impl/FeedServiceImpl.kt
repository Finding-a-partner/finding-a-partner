package com.finding_a_partner.feed_service.service.impl

import com.finding_a_partner.feed_service.enum.FriendshipStatus
import com.finding_a_partner.feed_service.enum.OwnerType
import com.finding_a_partner.feed_service.feign.client.EventClient
import com.finding_a_partner.feed_service.feign.client.GroupClient
import com.finding_a_partner.feed_service.feign.client.UserClient
import com.finding_a_partner.feed_service.mappers.FeedMapper
import com.finding_a_partner.feed_service.model.FeedResponse
import com.finding_a_partner.feed_service.service.FeedService
import org.springframework.stereotype.Service

@Service
class FeedServiceImpl(
    val userClient: UserClient,
    val groupClient: GroupClient,
    val eventClient: EventClient,
    val mapper: FeedMapper,
) : FeedService {

    override fun getFeedForUser(userId: Long): List<FeedResponse> {
        val friends = userClient.getRequests(userId, FriendshipStatus.ACCEPTED)
        val friendEvents = eventClient.getByOwnerTypeBatch(OwnerType.USER, friends.map { it.friend.id })
        val groups = groupClient.getUserGroups(userId)
        val groupEvents = eventClient.getByOwnerTypeBatch(OwnerType.GROUP, groups.map { it.id })

        val events = friendEvents.union(groupEvents)
        return events
            .map { mapper.entityToResponse(it) }
            .sortedByDescending { it.createdAt }
    }
}
