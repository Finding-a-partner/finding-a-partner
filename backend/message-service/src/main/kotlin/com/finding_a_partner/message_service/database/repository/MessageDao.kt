package com.finding_a_partner.message_service.database.repository

import com.finding_a_partner.message_service.database.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MessageDao : JpaRepository<Message, Long> {
    fun findAllByChatIdOrderByCreatedAtAsc(chatId: Long): List<Message>

    @Query("""
        SELECT m 
        FROM Message m
        JOIN FETCH m.chat
        WHERE m.chat.id = :chatId
        ORDER BY m.createdAt ASC
    """)
    fun findAllByChatIdOrderByCreatedAtAscWithChat(
        @Param("chatId") chatId: Long
    ): List<Message>
}