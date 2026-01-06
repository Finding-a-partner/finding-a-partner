package com.finding_a_partner.message_service.service

import com.finding_a_partner.message_service.model.request.ChatParticipantRequest
import com.finding_a_partner.message_service.model.response.ChatParticipantResponse
import com.finding_a_partner.message_service.model.response.ChatResponse

interface ChatParticipantService {
    fun getAll(): List<ChatParticipantResponse>
    fun join(chatId: Long, userId: Long, request: ChatParticipantRequest): ChatParticipantResponse
    fun create(chatId: Long, request: ChatParticipantRequest): ChatParticipantResponse
    fun getChatsByUserId(userId: Long): List<ChatResponse>
    fun getChatsByGroupId(groupId: Long): List<ChatResponse>
    fun update(chatParticipantId: Long, request: ChatParticipantRequest): ChatParticipantResponse
    fun delete(id: Long)
}
