package com.finding_a_partner.message_service.service.impl

import com.finding_a_partner.message_service.errors.ResourceNotFoundException
import com.finding_a_partner.message_service.database.entity.Chat
import com.finding_a_partner.message_service.database.entity.ChatParticipant
import com.finding_a_partner.message_service.database.repository.ChatDao
import com.finding_a_partner.message_service.database.repository.ChatParticipantDao
import com.finding_a_partner.message_service.enum.ChatRole
import com.finding_a_partner.message_service.enum.ChatType
import com.finding_a_partner.message_service.enum.ParticipantType
import com.finding_a_partner.message_service.mapper.ChatMapper
import com.finding_a_partner.message_service.mapper.ChatParticipantMapper
import com.finding_a_partner.message_service.model.request.ChatParticipantRequest
import com.finding_a_partner.message_service.model.request.ChatRequest
import com.finding_a_partner.message_service.model.response.ChatDetailResponse
import com.finding_a_partner.message_service.model.response.ChatResponse
import com.finding_a_partner.message_service.service.ChatService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ChatServiceImpl(
    val dao: ChatDao,
    val mapper: ChatMapper,
    val participantMapper: ChatParticipantMapper,
    val participantDao: ChatParticipantDao,
) : ChatService {

    override fun getAll(): List<ChatResponse> {
        return dao.findAll().map { mapper.entityToResponse(it) }
    }

    override fun getById(chatId: Long): ChatResponse =
        mapper.entityToResponse(dao.findById(chatId).orElseThrow { throw ResourceNotFoundException(chatId) })

    override fun getEntityById(chatId: Long): Chat {
        val optionalChat = dao.findById(chatId)
        if (optionalChat.isPresent) {
            val chat = optionalChat.get()
            return chat
        } else {
            println("[ChatServiceImpl] ERROR: Chat not found with id: $chatId")
            throw ResourceNotFoundException(chatId)
        }
    }

    override fun getChatWithParticipantsById(chatId: Long): ChatDetailResponse {
        val chat = dao.findById(chatId).orElseThrow { throw ResourceNotFoundException(chatId) }

        val participants = participantDao.findAllByChatId(chatId)

        val participantResponses = participants.map {
            participantMapper.entityToDetailResponse(it)
        }

        return ChatDetailResponse(
            id = chat.id,
            type = chat.type,
            name = chat.name,
            createdAt = chat.createdAt,
            participants = participantResponses,
            eventId = chat.eventId,
//            messages = listOf()
        )
    }

    fun saveParticipants(chat: Chat, requests: List<ChatParticipantRequest>): List<ChatParticipant> {
        val participants = requests.map { request ->
            ChatParticipant(
                chat = chat,
                participantId = request.participantId,
                participantType = request.participantType,
                role = request.role,
            )
        }

        return participantDao.saveAll(participants)
    }

    override fun create(userId: Long, request: ChatRequest): ChatResponse {
        val chat = Chat(
            type = request.type,
            name = request.name,
            eventId = request.eventId,
        )
        val savedEntity = dao.save(chat)

        when (request.type) {
            ChatType.GROUP -> {
                var ownerId = userId
                var ownerType = ParticipantType.USER
                if (request.ownerId != null && request.ownerType != null) {
                    ownerId = request.ownerId
                    ownerType = request.ownerType
                }

                val participant = ChatParticipant(
                    chat = chat,
                    participantId = ownerId,
                    participantType = ownerType,
                    role = ChatRole.OWNER,
                )
                participantDao.save(participant)

            }
            ChatType.EVENT -> {
                var ownerId = userId
                var ownerType = ParticipantType.USER
                if (request.ownerId != null && request.ownerType != null) {
                    ownerId = request.ownerId
                    ownerType = request.ownerType
                }

                val participant = ChatParticipant(
                    chat = chat,
                    participantId = ownerId,
                    participantType = ownerType,
                    role = ChatRole.OWNER,
                )
                participantDao.save(participant)
            }
            ChatType.PRIVATE -> {
                val participant1 = ChatParticipant(
                    chat = chat,
                    participantId = userId,
                    participantType = ParticipantType.USER,
                    role = ChatRole.MEMBER,
                )
                participantDao.save(participant1)
            }
        }

        if (request.participants != null) {
            saveParticipants(chat, request.participants)
        }

        return mapper.entityToResponse(savedEntity)
    }

    override fun update(id: Long, request: ChatRequest): ChatResponse {
        val entity = dao.findById(id).orElseThrow { throw ResourceNotFoundException(id) }
        if (entity.type == ChatType.GROUP) {
            entity.apply {
                name = request.name
            }
        }
        return mapper.entityToResponse(dao.save(entity))
    }

    override fun delete(id: Long) {
        val entity = dao.findById(id).orElseThrow { throw ResourceNotFoundException(id) }
        dao.delete(entity)
    }

    override fun getOrCreatePrivateChat(userId1: Long, userId2: Long): ChatResponse {
        if (userId1 == userId2) {
            throw IllegalArgumentException("Cannot create private chat with yourself")
        }

        val existingChat = participantDao.findPrivateChatBetweenUsers(
            userId1, 
            userId2, 
            ParticipantType.USER
        )
        
        if (existingChat != null) {
            return mapper.entityToResponse(existingChat)
        }

        val chat = Chat(
            type = ChatType.PRIVATE,
            name = null,
            eventId = null,
        )
        val savedChat = dao.save(chat)

        val participant1 = ChatParticipant(
            chat = savedChat,
            participantId = userId1,
            participantType = ParticipantType.USER,
            role = ChatRole.MEMBER,
        )
        val participant2 = ChatParticipant(
            chat = savedChat,
            participantId = userId2,
            participantType = ParticipantType.USER,
            role = ChatRole.MEMBER,
        )
        
        participantDao.save(participant1)
        participantDao.save(participant2)

        return mapper.entityToResponse(savedChat)
    }

    @Transactional
    override fun getChatsByUserId(userId: Long): List<ChatResponse> {
        try {
            val participants = participantDao.findAllByParticipantIdAndParticipantTypeWithChat(
                userId, 
                ParticipantType.USER
            )

            val chats = participants
                .map { it.chat }
                .distinctBy { it.id }
            
            val result = chats.map { chat ->
                try {
                    mapper.entityToResponse(chat)
                } catch (e: Exception) {
                    println("[ChatServiceImpl] Error mapping chat ${chat.id}: ${e.message}")
                    e.printStackTrace()
                    throw e
                }
            }
            
            return result
        } catch (e: Exception) {
            println("[ChatServiceImpl] Error in getChatsByUserId: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
