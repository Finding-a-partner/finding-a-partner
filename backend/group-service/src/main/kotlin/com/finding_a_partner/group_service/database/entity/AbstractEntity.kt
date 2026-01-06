package com.finding_a_partner.group_service.database.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

@MappedSuperclass
abstract class AbstractEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    val id: Long = 0,
) {
    @Column(name = "created_at")
    @CreationTimestamp
    val createdAt: OffsetDateTime = OffsetDateTime.now()
}
