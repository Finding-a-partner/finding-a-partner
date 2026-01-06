package com.finding_a_partner.message_service.service

import com.finding_a_partner.message_service.database.entity.Chat
import com.finding_a_partner.message_service.model.request.ChatRequest
import com.finding_a_partner.message_service.model.response.ChatDetailResponse
import com.finding_a_partner.message_service.model.response.ChatResponse

interface ChatService {
    fun getAll(): List<ChatResponse>
    fun getById(groupId: Long): ChatResponse
    fun getEntityById(groupId: Long): Chat
    fun getChatWithParticipantsById(chatId: Long): ChatDetailResponse
    fun create(userId: Long, request: ChatRequest): ChatResponse
    fun update(id: Long, request: ChatRequest): ChatResponse
    fun delete(id: Long)
    fun getOrCreatePrivateChat(userId1: Long, userId2: Long): ChatResponse
    fun getChatsByUserId(userId: Long): List<ChatResponse>
}