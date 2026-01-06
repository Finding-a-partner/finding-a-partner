package com.finding_a_partner.message_service.service.impl

import com.finding_a_partner.message_service.database.entity.Chat
import com.finding_a_partner.message_service.database.entity.ChatParticipant
import com.finding_a_partner.message_service.database.repository.ChatDao
import com.finding_a_partner.message_service.database.repository.ChatParticipantDao
import com.finding_a_partner.message_service.enum.ChatRole
import com.finding_a_partner.message_service.enum.ChatType
import com.finding_a_partner.message_service.enum.ParticipantType
import com.finding_a_partner.message_service.errors.ResourceNotFoundException
import com.finding_a_partner.message_service.mapper.ChatMapper
import com.finding_a_partner.message_service.mapper.ChatParticipantMapper
import com.finding_a_partner.message_service.model.request.ChatParticipantRequest
import com.finding_a_partner.message_service.model.request.ChatRequest
import com.finding_a_partner.message_service.model.response.ChatDetailResponse
import com.finding_a_partner.message_service.model.response.ChatResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.OffsetDateTime
import java.util.*

class ChatServiceImplTest {

    private lateinit var chatDao: ChatDao
    private lateinit var chatMapper: ChatMapper
    private lateinit var chatParticipantMapper: ChatParticipantMapper
    private lateinit var chatParticipantDao: ChatParticipantDao
    private lateinit var chatService: ChatServiceImpl

    @BeforeEach
    fun setUp() {
        chatDao = mockk()
        chatMapper = mockk()
        chatParticipantMapper = mockk()
        chatParticipantDao = mockk()
        chatService = ChatServiceImpl(chatDao, chatMapper, chatParticipantMapper, chatParticipantDao)
    }

    @Test
    fun `create should create private chat with two participants`() {
        // Given
        val userId = 1L
        val request = ChatRequest(
            type = ChatType.PRIVATE,
            name = null,
            ownerId = null,
            ownerType = null,
            participants = null,
            eventId = null
        )
        val savedChat = Chat(
            id = 1L,
            type = ChatType.PRIVATE,
            name = null,
            createdAt = OffsetDateTime.now(),
            participants = emptyList(),
            eventId = null
        )
        val chatResponse = ChatResponse(
            id = 1L,
            type = ChatType.PRIVATE,
            name = null,
            createdAt = OffsetDateTime.now(),
            eventId = null
        )

        every { chatDao.save(any()) } returns savedChat
        every { chatParticipantDao.save(any()) } returns mockk<ChatParticipant>()
        every { chatMapper.entityToResponse(savedChat) } returns chatResponse

        // When
        val result = chatService.create(userId, request)

        // Then
        assertNotNull(result)
        assertEquals(chatResponse.id, result.id)
        assertEquals(chatResponse.type, result.type)
        verify(exactly = 1) { chatDao.save(any()) }
        verify(exactly = 2) { chatParticipantDao.save(any()) } // Two participants for private chat
        verify(exactly = 1) { chatMapper.entityToResponse(savedChat) }
    }

    @Test
    fun `getOrCreatePrivateChat should return existing chat when found`() {
        // Given
        val userId1 = 1L
        val userId2 = 2L
        val existingChat = Chat(
            id = 1L,
            type = ChatType.PRIVATE,
            name = null,
            createdAt = OffsetDateTime.now(),
            participants = emptyList(),
            eventId = null
        )
        val chatResponse = ChatResponse(
            id = 1L,
            type = ChatType.PRIVATE,
            name = null,
            createdAt = OffsetDateTime.now(),
            eventId = null
        )

        every { chatParticipantDao.findPrivateChatBetweenUsers(userId1, userId2, ParticipantType.USER) } returns existingChat
        every { chatMapper.entityToResponse(existingChat) } returns chatResponse

        // When
        val result = chatService.getOrCreatePrivateChat(userId1, userId2)

        // Then
        assertNotNull(result)
        assertEquals(chatResponse.id, result.id)
        verify(exactly = 1) { chatParticipantDao.findPrivateChatBetweenUsers(userId1, userId2, ParticipantType.USER) }
        verify(exactly = 1) { chatMapper.entityToResponse(existingChat) }
        verify(exactly = 0) { chatDao.save(any()) }
    }

    @Test
    fun `getOrCreatePrivateChat should throw exception when trying to create chat with yourself`() {
        // Given
        val userId = 1L

        // When & Then
        assertThrows<IllegalArgumentException> {
            chatService.getOrCreatePrivateChat(userId, userId)
        }
        verify(exactly = 0) { chatParticipantDao.findPrivateChatBetweenUsers(any(), any(), any()) }
        verify(exactly = 0) { chatDao.save(any()) }
    }

    @Test
    fun `getById should return chat when found`() {
        // Given
        val chatId = 1L
        val chat = Chat(
            id = chatId,
            type = ChatType.PRIVATE,
            name = null,
            createdAt = OffsetDateTime.now(),
            participants = emptyList(),
            eventId = null
        )
        val chatResponse = ChatResponse(
            id = chatId,
            type = ChatType.PRIVATE,
            name = null,
            createdAt = OffsetDateTime.now(),
            eventId = null
        )

        every { chatDao.findById(chatId) } returns Optional.of(chat)
        every { chatMapper.entityToResponse(chat) } returns chatResponse

        // When
        val result = chatService.getById(chatId)

        // Then
        assertNotNull(result)
        assertEquals(chatResponse.id, result.id)
        verify(exactly = 1) { chatDao.findById(chatId) }
        verify(exactly = 1) { chatMapper.entityToResponse(chat) }
    }

    @Test
    fun `getById should throw ResourceNotFoundException when chat not found`() {
        // Given
        val chatId = 999L

        every { chatDao.findById(chatId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            chatService.getById(chatId)
        }
        verify(exactly = 1) { chatDao.findById(chatId) }
        verify(exactly = 0) { chatMapper.entityToResponse(any()) }
    }

    @Test
    fun `delete should delete chat successfully`() {
        // Given
        val chatId = 1L
        val chat = Chat(
            id = chatId,
            type = ChatType.PRIVATE,
            name = null,
            createdAt = OffsetDateTime.now(),
            participants = emptyList(),
            eventId = null
        )

        every { chatDao.findById(chatId) } returns Optional.of(chat)
        every { chatDao.delete(chat) } returns Unit

        // When
        chatService.delete(chatId)

        // Then
        verify(exactly = 1) { chatDao.findById(chatId) }
        verify(exactly = 1) { chatDao.delete(chat) }
    }

    @Test
    fun `getChatsByUserId should return all chats for user`() {
        // Given
        val userId = 1L
        val chat1 = Chat(
            id = 1L,
            type = ChatType.PRIVATE,
            name = null,
            createdAt = OffsetDateTime.now(),
            participants = emptyList(),
            eventId = null
        )
        val chat2 = Chat(
            id = 2L,
            type = ChatType.GROUP,
            name = "Group Chat",
            createdAt = OffsetDateTime.now(),
            participants = emptyList(),
            eventId = null
        )
        val participants = listOf(
            ChatParticipant(chat = chat1, participantId = userId, participantType = ParticipantType.USER, role = ChatRole.MEMBER),
            ChatParticipant(chat = chat2, participantId = userId, participantType = ParticipantType.USER, role = ChatRole.MEMBER)
        )
        val chatResponses = listOf(
            ChatResponse(1L, ChatType.PRIVATE, null, OffsetDateTime.now(), null),
            ChatResponse(2L, ChatType.GROUP, "Group Chat", OffsetDateTime.now(), null)
        )

        every { chatParticipantDao.findAllByParticipantIdAndParticipantTypeWithChat(userId, ParticipantType.USER) } returns participants
        every { chatMapper.entityToResponse(chat1) } returns chatResponses[0]
        every { chatMapper.entityToResponse(chat2) } returns chatResponses[1]

        // When
        val result = chatService.getChatsByUserId(userId)

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { chatParticipantDao.findAllByParticipantIdAndParticipantTypeWithChat(userId, ParticipantType.USER) }
        verify(exactly = 2) { chatMapper.entityToResponse(any()) }
    }
}

