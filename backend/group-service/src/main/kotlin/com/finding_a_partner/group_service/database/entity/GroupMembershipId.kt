package com.finding_a_partner.group_service.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class GroupMembershipId(
    @Column(name = "group_id", nullable = false)
    val groupId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,
) : Serializable
