package com.finding_a_partner.group_service.database.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "groups")
class Group(
    @Column(nullable = false, unique = true)
    var name: String,

    @Column(columnDefinition = "text")
    var description: String?,

    @Column(name = "creator_user_id", nullable = false)
    val creatorUserId: Long,
) : AbstractEntity()
