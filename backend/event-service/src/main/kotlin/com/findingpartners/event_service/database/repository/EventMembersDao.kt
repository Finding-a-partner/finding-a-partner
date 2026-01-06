package com.findingpartners.event_service.database.repository

import com.findingpartners.event_service.database.entity.EventMembers
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface EventMembersDao : JpaRepository<EventMembers, Long> {
    fun findAllByEventId(eventId: Long): List<EventMembers>
    fun findAllByUserId(userId: Long): List<EventMembers>
    fun findByUserIdAndEventId(userId: Long, eventId: Long): Optional<EventMembers>
}
