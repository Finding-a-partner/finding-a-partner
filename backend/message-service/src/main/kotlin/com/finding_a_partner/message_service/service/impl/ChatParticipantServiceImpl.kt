package com.finding_a_partner.message_service.service.impl

import com.finding_a_partner.message_service.errors.ResourceNotFoundException
import com.finding_a_partner.message_service.database.entity.ChatParticipant
import com.finding_a_partner.message_service.database.repository.ChatParticipantDao
import com.finding_a_partner.message_service.enum.ChatType
import com.finding_a_partner.message_service.enum.ParticipantType
import com.finding_a_partner.message_service.errors.NotEnoughRightsException
import com.finding_a_partner.message_service.mapper.ChatMapper
import com.finding_a_partner.message_service.mapper.ChatParticipantMapper
import com.finding_a_partner.message_service.model.request.ChatParticipantRequest
import com.finding_a_partner.message_service.model.response.ChatParticipantResponse
import com.finding_a_partner.message_service.model.response.ChatResponse
import com.finding_a_partner.message_service.service.ChatParticipantService
import com.finding_a_partner.message_service.service.ChatService
import org.springframework.stereotype.Service

@Service
class ChatParticipantServiceImpl(
    val dao: ChatParticipantDao,
    val chatService: ChatService,
    val mapper: ChatParticipantMapper,
    val chatMapper: ChatMapper,
) : ChatParticipantService {
    override fun getAll(): List<ChatParticipantResponse> =
        dao.findAll().map { mapper.entityToResponse(it) }

    override fun join(chatId: Long, userId: Long, request: ChatParticipantRequest): ChatParticipantResponse {
        val chat = chatService.getEntityById(chatId)

        if (chat.type == ChatType.PRIVATE) throw NotEnoughRightsException()

        val entity = ChatParticipant(
            chat = chatService.getEntityById(chatId),
            participantId = userId,
            participantType = ParticipantType.USER,
        )
        return mapper.entityToResponse(dao.save(entity))
    }

    override fun create(chatId: Long, request: ChatParticipantRequest): ChatParticipantResponse {
        val entity = ChatParticipant(
            chat = chatService.getEntityById(chatId),
            participantId = request.participantId,
            participantType = request.participantType,
        )
        return mapper.entityToResponse(dao.save(entity))
    }

    override fun getChatsByUserId(userId: Long): List<ChatResponse> {
        return dao.findAllByParticipantIdAndParticipantType(userId, ParticipantType.USER)
            .map { it.chat }
            .distinct()
            .map { chatMapper.entityToResponse(it) }
    }

    override fun getChatsByGroupId(groupId: Long): List<ChatResponse> {
        return dao.findAllByParticipantIdAndParticipantType(groupId, ParticipantType.GROUP)
            .map { it.chat }
            .distinct()
            .map { chatMapper.entityToResponse(it) }
    }

    override fun update(chatParticipantId: Long, request: ChatParticipantRequest): ChatParticipantResponse {
        val entity = dao.findById(chatParticipantId).orElseThrow { throw ResourceNotFoundException() }
            .apply {
                role = request.role
            }

        return mapper.entityToResponse(dao.save(entity))
    }

    override fun delete(id: Long) {
        val entity = dao.findById(id).orElseThrow { throw ResourceNotFoundException() }
        dao.delete(entity)
    }
}
