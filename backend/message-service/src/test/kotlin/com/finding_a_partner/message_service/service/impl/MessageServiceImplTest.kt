package com.finding_a_partner.message_service.service.impl

import com.finding_a_partner.message_service.database.entity.Chat
import com.finding_a_partner.message_service.database.entity.Message
import com.finding_a_partner.message_service.database.repository.MessageDao
import com.finding_a_partner.message_service.enum.ChatType
import com.finding_a_partner.message_service.enum.MessageStatus
import com.finding_a_partner.message_service.errors.ResourceNotFoundException
import com.finding_a_partner.message_service.mapper.MessageMapper
import com.finding_a_partner.message_service.model.request.MessageRequest
import com.finding_a_partner.message_service.model.response.MessageResponse
import com.finding_a_partner.message_service.service.ChatService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.OffsetDateTime

class MessageServiceImplTest {

    private lateinit var messageDao: MessageDao
    private lateinit var chatService: ChatService
    private lateinit var messageMapper: MessageMapper
    private lateinit var messageService: MessageServiceImpl

    @BeforeEach
    fun setUp() {
        messageDao = mockk()
        chatService = mockk()
        messageMapper = mockk()
        messageService = MessageServiceImpl(messageDao, chatService, messageMapper)
    }

    @Test
    fun `sendMessage should create and return message successfully`() {
        // Given
        val chatId = 1L
        val senderId = 2L
        val content = "Test message"
        val request = MessageRequest(
            chatId = chatId,
            senderId = senderId,
            content = content
        )
        val chat = Chat(
            id = chatId,
            type = ChatType.PRIVATE,
            name = null,
            createdAt = OffsetDateTime.now(),
            participants = emptyList(),
            eventId = null
        )
        val savedMessage = Message(
            id = 1L,
            chat = chat,
            senderId = senderId,
            content = content,
            createdAt = OffsetDateTime.now(),
            status = MessageStatus.SENT
        )
        val messageResponse = MessageResponse(
            id = 1L,
            chatId = chatId,
            senderId = senderId,
            content = content,
            createdAt = OffsetDateTime.now(),
            status = MessageStatus.SENT
        )

        every { chatService.getEntityById(chatId) } returns chat
        every { messageDao.save(any()) } returns savedMessage
        every { messageMapper.entityToResponse(savedMessage) } returns messageResponse

        // When
        val result = messageService.sendMessage(request)

        // Then
        assertNotNull(result)
        assertEquals(messageResponse.id, result.id)
        assertEquals(messageResponse.content, result.content)
        assertEquals(messageResponse.chatId, result.chatId)
        verify(exactly = 1) { chatService.getEntityById(chatId) }
        verify(exactly = 1) { messageDao.save(any()) }
        verify(exactly = 1) { messageMapper.entityToResponse(savedMessage) }
    }

    @Test
    fun `sendMessage should throw exception when chat not found`() {
        // Given
        val chatId = 999L
        val request = MessageRequest(
            chatId = chatId,
            senderId = 1L,
            content = "Test"
        )

        every { chatService.getEntityById(chatId) } throws ResourceNotFoundException(chatId)

        // When & Then
        assertThrows<ResourceNotFoundException> {
            messageService.sendMessage(request)
        }
        verify(exactly = 1) { chatService.getEntityById(chatId) }
        verify(exactly = 0) { messageDao.save(any()) }
    }

    @Test
    fun `getMessagesByChatId should return all messages for chat`() {
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
        val messages = listOf(
            Message(
                id = 1L,
                chat = chat,
                senderId = 1L,
                content = "Message 1",
                createdAt = OffsetDateTime.now().minusHours(2),
                status = MessageStatus.SENT
            ),
            Message(
                id = 2L,
                chat = chat,
                senderId = 2L,
                content = "Message 2",
                createdAt = OffsetDateTime.now().minusHours(1),
                status = MessageStatus.SENT
            )
        )
        val messageResponses = listOf(
            MessageResponse(
                id = 1L,
                chatId = chatId,
                senderId = 1L,
                content = "Message 1",
                createdAt = messages[0].createdAt,
                status = MessageStatus.SENT
            ),
            MessageResponse(
                id = 2L,
                chatId = chatId,
                senderId = 2L,
                content = "Message 2",
                createdAt = messages[1].createdAt,
                status = MessageStatus.SENT
            )
        )

        every { messageDao.findAllByChatIdOrderByCreatedAtAscWithChat(chatId) } returns messages
        every { messageMapper.entityToResponse(messages[0]) } returns messageResponses[0]
        every { messageMapper.entityToResponse(messages[1]) } returns messageResponses[1]

        // When
        val result = messageService.getMessagesByChatId(chatId)

        // Then
        assertEquals(2, result.size)
        assertEquals(messageResponses[0].id, result[0].id)
        assertEquals(messageResponses[1].id, result[1].id)
        verify(exactly = 1) { messageDao.findAllByChatIdOrderByCreatedAtAscWithChat(chatId) }
        verify(exactly = 2) { messageMapper.entityToResponse(any()) }
    }

    @Test
    fun `getMessagesByChatId should return empty list when no messages`() {
        // Given
        val chatId = 1L

        every { messageDao.findAllByChatIdOrderByCreatedAtAscWithChat(chatId) } returns emptyList()

        // When
        val result = messageService.getMessagesByChatId(chatId)

        // Then
        assertTrue(result.isEmpty())
        verify(exactly = 1) { messageDao.findAllByChatIdOrderByCreatedAtAscWithChat(chatId) }
        verify(exactly = 0) { messageMapper.entityToResponse(any()) }
    }
}

