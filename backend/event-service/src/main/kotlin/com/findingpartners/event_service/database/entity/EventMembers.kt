package com.findingpartners.event_service.database.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "event_members",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["user_id", "event_id"],
            name = "uk_event_members_user_event",
        ),
    ],
)
class EventMembers(

    @Column(name = "user_id", updatable = false)
    val userId: Long,

    @ManyToOne
    @JoinColumn(name = "event_id")
    val event: Event,

) : AbstractEntity()
