package com.findingpartners.user_service.database.entity

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

@MappedSuperclass
abstract class AbstractEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) {
    @Column(name = "created_at")
    @CreationTimestamp
    val createdAt: OffsetDateTime = OffsetDateTime.now()
}
