package com.finding_a_partner.message_service.mapper

import com.finding_a_partner.message_service.database.entity.ChatParticipant
import com.finding_a_partner.message_service.enum.ParticipantType
import com.finding_a_partner.message_service.feign.GroupClient
import com.finding_a_partner.message_service.feign.UserClient
import com.finding_a_partner.message_service.model.response.ChatParticipantDetailResponse
import com.finding_a_partner.message_service.model.response.ChatParticipantResponse
import org.springframework.stereotype.Component

@Component
class ChatParticipantMapper(
    private val userClient: UserClient,
    private val groupClient: GroupClient
) {
    fun entityToResponse(entity: ChatParticipant): ChatParticipantResponse =
        ChatParticipantResponse(
            id = entity.id!!,
            chatId = entity.chat.id!!,
            participantId = entity.participantId,
            type = entity.participantType,
            role = entity.role,
            createdAt = entity.createdAt,
        )

    fun entityToDetailResponse(entity: ChatParticipant): ChatParticipantDetailResponse {
        return when (entity.participantType) {
            ParticipantType.USER -> {
                val user = userClient.getById(entity.participantId)
                ChatParticipantDetailResponse(
                    id = entity.id!!,
                    participantId = user.id,
                    participantType = ParticipantType.USER,
                    role = entity.role,
                    name = user.name,
                    surname = user.surname,
                    login = user.login,
                    avatarUrl = user.avatarUrl,
                    description = user.description,
                    createdAt = entity.createdAt,
                )
            }

            ParticipantType.GROUP -> {
                val group = groupClient.getGroupById(entity.participantId)
                ChatParticipantDetailResponse(
                    id = entity.id!!,
                    participantId = group.id,
                    participantType = ParticipantType.GROUP,
                    role = entity.role,
                    name = group.name,
                    avatarUrl = group.avatarUrl,
                    description = group.description,
                    createdAt = group.createdAt,
                )
            }
        }
    }
}
