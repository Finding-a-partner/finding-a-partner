package com.finding_a_partner.message_service.database.entity

import com.finding_a_partner.message_service.enum.MessageStatus
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "messages")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    val chat: Chat,

    @Column(nullable = false)
    val senderId: Long,

    @Column(nullable = false)
    val content: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: MessageStatus = MessageStatus.SENT,
)
