package com.finding_a_partner.message_service.database.repository

import com.finding_a_partner.message_service.database.entity.Chat
import com.finding_a_partner.message_service.database.entity.ChatParticipant
import com.finding_a_partner.message_service.enum.ParticipantType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatParticipantDao : JpaRepository<ChatParticipant, Long> {
    fun findAllByParticipantIdAndParticipantType(
        participantId: Long,
        participantType: ParticipantType,
    ): List<ChatParticipant>

    @Query("""
        SELECT DISTINCT cp 
        FROM ChatParticipant cp
        JOIN FETCH cp.chat
        WHERE cp.participantId = :participantId 
          AND cp.participantType = :participantType
    """)
    fun findAllByParticipantIdAndParticipantTypeWithChat(
        @Param("participantId") participantId: Long,
        @Param("participantType") participantType: ParticipantType,
    ): List<ChatParticipant>

    fun findAllByChatId(
        chatId: Long,
    ): List<ChatParticipant>

    @Query("""
        SELECT DISTINCT cp1.chat 
        FROM ChatParticipant cp1
        INNER JOIN ChatParticipant cp2 ON cp1.chat.id = cp2.chat.id
        WHERE cp1.participantId = :userId1 
          AND cp1.participantType = :participantType
          AND cp2.participantId = :userId2 
          AND cp2.participantType = :participantType
          AND cp1.chat.type = 'PRIVATE'
          AND (SELECT COUNT(DISTINCT cp.participantId) 
               FROM ChatParticipant cp 
               WHERE cp.chat.id = cp1.chat.id) = 2
    """)
    fun findPrivateChatBetweenUsers(
        userId1: Long,
        userId2: Long,
        participantType: ParticipantType
    ): Chat?
}
