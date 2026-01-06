package com.findingpartners.event_service.database.repository

import com.findingpartners.event_service.database.entity.Event
import com.findingpartners.event_service.enum.OwnerType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EventDao : JpaRepository<Event, Long> {
    fun findAllByOwnerIdAndOwnerType(id: Long, type: OwnerType): List<Event>

    @Query(
        """
    SELECT e FROM Event e 
    WHERE e.ownerId IN :ownerIds 
    AND e.ownerType = :ownerType
""",
    )
    fun findAllByOwnerIdInAndOwnerType(
        @Param("ownerIds") ownerIds: List<Long>,
        @Param("ownerType") ownerType: OwnerType,
    ): List<Event>
}
