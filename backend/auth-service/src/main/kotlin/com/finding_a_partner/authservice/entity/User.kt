package com.finding_a_partner.authservice.entity

import com.finding_a_partner.authservice.enum.Role
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

@Entity
@Table(name = "`users`")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val login: String,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER,

    @Column(name = "created_at")
    @CreationTimestamp
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
)
