package com.finding_a_partner.message_service.database.repository

import com.finding_a_partner.message_service.database.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatDao : JpaRepository<Chat, Long> {
}
