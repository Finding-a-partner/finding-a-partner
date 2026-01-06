package com.finding_a_partner.message_service.database.entity

import com.finding_a_partner.message_service.enum.ChatType
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "chats")
data class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: ChatType = ChatType.PRIVATE,

    @Column(nullable = true)
    var name: String? = null, // для групповых чатов

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @OneToMany(mappedBy = "chat", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val participants: List<ChatParticipant> = mutableListOf(),

    @Column(name = "event_id", nullable = true)
    var eventId: Long? = null, // для чатов мероприятий
)
