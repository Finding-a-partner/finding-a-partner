package com.finding_a_partner.message_service.controller

import com.finding_a_partner.message_service.model.request.ChatRequest
import com.finding_a_partner.message_service.model.response.ChatDetailResponse
import com.finding_a_partner.message_service.model.response.ChatResponse
import com.finding_a_partner.message_service.service.ChatService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chats")
class ChatController(
    val service: ChatService,
) {
    @GetMapping()
    fun getAll(): List<ChatResponse> = service.getAll()

    @PostMapping
    fun create(
        @RequestBody request: ChatRequest,
        @RequestHeader("X-User-Id") userId: Long,
    ): ChatResponse =
        service.create(userId, request)

    @GetMapping("/{chatId}")
    fun getById(@PathVariable("chatId") chatId: Long): ChatDetailResponse =
        service.getChatWithParticipantsById(chatId)

    @PutMapping("/{chatId}")
    fun update(
        @PathVariable("chatId") chatId: Long,
        @RequestBody request: ChatRequest,
    ) = service.update(chatId, request)

    @DeleteMapping("/{chatId}")
    fun delete(@PathVariable("chatId") chatId: Long) = service.delete(chatId)

    @GetMapping("/private/{otherUserId}")
    fun getOrCreatePrivateChat(
        @PathVariable("otherUserId") otherUserId: Long,
        @RequestHeader("X-User-Id") userId: Long,
    ): ChatResponse =
        service.getOrCreatePrivateChat(userId, otherUserId)

    @GetMapping("/my")
    fun getMyChats(
        @RequestHeader("X-User-Id") userId: Long,
    ): List<ChatResponse> =
        service.getChatsByUserId(userId)
}
