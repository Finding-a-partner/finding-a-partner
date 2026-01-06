package com.findingpartners.user_service.service.impl

import com.findingpartners.user_service.database.entity.Friendship
import com.findingpartners.user_service.database.repository.FriendshipDao
import com.findingpartners.user_service.database.repository.UserDao
import com.findingpartners.user_service.enum.FriendshipStatus
import com.findingpartners.user_service.model.request.FriendshipRequest
import com.findingpartners.user_service.model.response.FriendResponse
import com.findingpartners.user_service.model.response.FriendshipResponse
import com.findingpartners.user_service.service.FriendshipService
import com.findingpartners.user_service.util.FriendshipMapper
import com.findingpartners.user_service.util.UserMapper
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import org.springframework.stereotype.Service

@Service
@Transactional
class FriendshipServiceImpl(
    private val friendshipDao: FriendshipDao,
    private val userDao: UserDao,
    val userMapper: UserMapper,
    val friendMapper: FriendshipMapper,
) : FriendshipService {
    override fun sendFriendRequest(request: FriendshipRequest): FriendshipResponse {
        if (request.userId == request.friendId) {
            throw IllegalArgumentException("Нельзя отправить запрос самому себе")
        }

        val sender = userDao.findById(request.userId)
            .orElseThrow { NoSuchElementException("Отправитель не найден") }

        val receiver = userDao.findById(request.friendId)
            .orElseThrow { NoSuchElementException("Получатель не найден") }

        val existing = friendshipDao.findBetweenUsers(request.userId, request.friendId)
        existing?.let {
            val senderIsUser1 = it.user1.id == request.userId
            val senderIsUser2 = it.user2.id == request.userId
            
            if (senderIsUser1 && it.statusForUser1 == FriendshipStatus.PENDING 
                && it.statusForUser2 == FriendshipStatus.NO_CONNECTION) {
                throw IllegalStateException("Запрос уже отправлен")
            }
            if (senderIsUser2 && it.statusForUser2 == FriendshipStatus.PENDING 
                && it.statusForUser1 == FriendshipStatus.NO_CONNECTION) {
                throw IllegalStateException("Запрос уже отправлен")
            }
        }

        val (user1, user2) = if (sender.id < receiver.id) {
            Pair(sender, receiver)
        } else {
            Pair(receiver, sender)
        }

        val senderIsUser1 = user1.id == request.userId

        val entity = if (senderIsUser1) {
            Friendship(
                user1 = user1,
                user2 = user2,
                statusForUser1 = FriendshipStatus.PENDING,
                statusForUser2 = FriendshipStatus.NO_CONNECTION,
            )
        } else {
            Friendship(
                user1 = user1,
                user2 = user2,
                statusForUser1 = FriendshipStatus.NO_CONNECTION,
                statusForUser2 = FriendshipStatus.PENDING,
            )
        }
        
        return friendMapper.entityToResponse(friendshipDao.save(entity))
    }

    override fun respondToFriendRequest(requestId: Long, request: FriendshipRequest): FriendshipResponse {
        val entity = friendshipDao.findById(requestId).orElseThrow {
            NoSuchElementException("Запрос с ID $requestId не найден")
        }

        if (!entity.involvesUser(request.friendId)) {
            throw SecurityException("Пользователь не является участником этой дружбы")
        }

        val isUser1Sender = entity.statusForUser1 == FriendshipStatus.PENDING &&
                entity.statusForUser2 == FriendshipStatus.NO_CONNECTION
        val isUser2Sender = entity.statusForUser2 == FriendshipStatus.PENDING &&
                entity.statusForUser1 == FriendshipStatus.NO_CONNECTION

        if (!isUser1Sender && !isUser2Sender) {
            throw IllegalStateException("Запрос уже обработан")
        }

        val isUser1Receiver = entity.user1.id == request.friendId && isUser2Sender
        val isUser2Receiver = entity.user2.id == request.friendId && isUser1Sender

        if (!isUser1Receiver && !isUser2Receiver) {
            throw SecurityException("Только получатель заявки может на неё ответить")
        }

        val newStatus = if (request.status == FriendshipStatus.ACCEPTED) {
            FriendshipStatus.ACCEPTED
        } else {
            FriendshipStatus.REJECTED
        }

        if (isUser1Receiver) {
            entity.statusForUser1 = newStatus
        } else if (isUser2Receiver) {
            entity.statusForUser2 = newStatus
        }

        if (newStatus == FriendshipStatus.ACCEPTED) {
            entity.statusForUser1 = FriendshipStatus.ACCEPTED
            entity.statusForUser2 = FriendshipStatus.ACCEPTED
        }

        return friendMapper.entityToResponse(friendshipDao.save(entity))
    }

    override fun getUserRequests(userId: Long, status: FriendshipStatus): List<FriendResponse> {
        userDao.findById(userId).orElseThrow {
            NoSuchElementException("Пользователь с ID $userId не найден")
        }
        
        val friendships = when (status) {
            FriendshipStatus.PENDING -> {
                friendshipDao.findOutgoingRequests(
                    userId, 
                    FriendshipStatus.PENDING, 
                    FriendshipStatus.NO_CONNECTION
                )
            }
            else -> {
                friendshipDao.findByUserAndStatus(userId, status)
            }
        }
        
        return friendships.map { friendship ->
            val otherUser = if (status == FriendshipStatus.PENDING) {
                if (friendship.statusForUser1 == FriendshipStatus.PENDING) {
                    friendship.user2
                } else {
                    friendship.user1
                }
            } else {
                friendship.getOtherUser(userId)
            }
            
            val userStatus = friendship.getStatusForUser(userId)
            
            FriendResponse(
                friend = userMapper.entityToResponse(otherUser),
                status = userStatus,
                id = friendship.id,
            )
        }
    }

    override fun getIncomingRequests(userId: Long): List<FriendResponse> {
        userDao.findById(userId).orElseThrow {
            NoSuchElementException("Пользователь с ID $userId не найден")
        }

        val friendships = friendshipDao.findIncomingRequests(
            userId, 
            FriendshipStatus.PENDING, 
            FriendshipStatus.NO_CONNECTION
        )
        return friendships.map { friendship ->
            val sender = if (friendship.statusForUser1 == FriendshipStatus.PENDING) {
                friendship.user1
            } else {
                friendship.user2
            }

            val receiverStatus = friendship.getStatusForUser(userId)
            
            FriendResponse(
                friend = userMapper.entityToResponse(sender),
                status = receiverStatus,
                id = friendship.id,
            )
        }
    }

    override fun deleteFriend(currentUserId: Long, friendId: Long) {
        userDao.findById(currentUserId)
            .orElseThrow { NotFoundException("Current user not found") }

        userDao.findById(friendId)
            .orElseThrow { NotFoundException("Friend not found") }

        val friendship = friendshipDao.findBetweenUsers(currentUserId, friendId)
        if (friendship == null) {
            throw NotFoundException("Friendship not found")
        }

        friendshipDao.delete(friendship)
    }
}
