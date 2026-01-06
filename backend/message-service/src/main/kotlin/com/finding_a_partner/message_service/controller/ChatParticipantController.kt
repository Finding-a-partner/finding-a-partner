package com.finding_a_partner.message_service.controller

import com.finding_a_partner.message_service.model.request.ChatParticipantRequest
import com.finding_a_partner.message_service.model.response.ChatParticipantResponse
import com.finding_a_partner.message_service.model.response.ChatResponse
import com.finding_a_partner.message_service.service.ChatParticipantService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chats")
class ChatParticipantController(
    val participantService: ChatParticipantService,
) {
    @GetMapping("/participants")
    fun getAll() = participantService.getAll()

//    вступить в чат
    @PostMapping("/{chatId}/join")
    fun join(
        @PathVariable("chatId") chatId: Long,
        @RequestHeader("X-User-Id") userId: Long,
        @RequestBody request: ChatParticipantRequest,
    ): ChatParticipantResponse =
        participantService.join(chatId, userId, request)

//    добавить пользователя(ей)
    @PostMapping("/{chatId}/participants")
    fun create(
        @PathVariable("chatId") chatId: Long,
        @RequestBody request: ChatParticipantRequest,
    ): ChatParticipantResponse =
        participantService.create(chatId, request)

//    чаты пользователя
    @GetMapping("/users/{userId}")
    fun getChatsByUserId(@PathVariable("userId") userId: Long): List<ChatResponse> =
        participantService.getChatsByUserId(userId)

//    чаты группы
    @GetMapping("/groups/{groupId}")
    fun getChatsByGroupId(@PathVariable("groupId") groupId: Long): List<ChatResponse> =
        participantService.getChatsByGroupId(groupId)

    @PutMapping("/participants/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody request: ChatParticipantRequest,
    ) = participantService.update(id, request)

    @DeleteMapping("/participants/{id}")
    fun delete(@PathVariable("id") id: Long) = participantService.delete(id)
}
