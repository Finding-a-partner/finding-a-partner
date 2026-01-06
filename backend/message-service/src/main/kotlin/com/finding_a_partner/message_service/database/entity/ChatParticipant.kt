package com.finding_a_partner.message_service.database.entity

import com.finding_a_partner.message_service.enum.ChatRole
import com.finding_a_partner.message_service.enum.MessageStatus
import com.finding_a_partner.message_service.enum.ParticipantType
import com.finding_a_partner.message_service.model.request.ChatParticipantRequest
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(
    name = "chat_participants",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["chat_id", "participant_id", "participant_type"],
        ),
    ],
)
data class ChatParticipant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    val chat: Chat,

    @Column(name = "participant_id", nullable = false)
    val participantId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_type", nullable = false)
    val participantType: ParticipantType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: ChatRole = ChatRole.MEMBER,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
)
