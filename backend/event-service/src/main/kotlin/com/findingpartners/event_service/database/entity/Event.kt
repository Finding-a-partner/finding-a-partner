package com.findingpartners.event_service.database.entity

import com.findingpartners.event_service.enum.OwnerType
import com.findingpartners.event_service.enum.Visibility
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "event")
class Event(
    @Column(name = "owner_id")
    var ownerId: Long,

    @Column(name = "owner_type")
    @Enumerated(EnumType.STRING)
    var ownerType: OwnerType,

    @Column(nullable = false)
    var title: String,

    @Column
    var description: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var visibility: Visibility,

    @Column
    var date: LocalDate,

    @Column
    var time: LocalTime,
) : AbstractEntity()
