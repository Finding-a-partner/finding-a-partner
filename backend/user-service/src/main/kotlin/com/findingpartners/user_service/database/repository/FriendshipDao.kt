package com.findingpartners.user_service.database.repository

import com.findingpartners.user_service.database.entity.Friendship
import com.findingpartners.user_service.database.entity.User
import com.findingpartners.user_service.enum.FriendshipStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FriendshipDao : JpaRepository<Friendship, Long> {

    @Query(
        """
        SELECT f FROM Friendship f
        WHERE (f.user1.id = :userId1 AND f.user2.id = :userId2)
           OR (f.user1.id = :userId2 AND f.user2.id = :userId1)
    """,
    )
    fun findBetweenUsers(
        @Param("userId1") userId1: Long,
        @Param("userId2") userId2: Long,
    ): Friendship?

    @Query(
        """
        SELECT f FROM Friendship f
        WHERE (f.user1.id = :userId AND f.statusForUser1 = :status)
           OR (f.user2.id = :userId AND f.statusForUser2 = :status)
    """,
    )
    fun findByUserAndStatus(
        @Param("userId") userId: Long,
        @Param("status") status: FriendshipStatus,
    ): List<Friendship>
    
    // входящие заявки (пользователь является получателем)
    @Query(
        """
        SELECT f FROM Friendship f
        WHERE (f.user1.id = :userId AND f.statusForUser1 = :noConnectionStatus AND f.statusForUser2 = :pendingStatus)
           OR (f.user2.id = :userId AND f.statusForUser2 = :noConnectionStatus AND f.statusForUser1 = :pendingStatus)
    """,
    )
    fun findIncomingRequests(
        @Param("userId") userId: Long,
        @Param("pendingStatus") pendingStatus: FriendshipStatus,
        @Param("noConnectionStatus") noConnectionStatus: FriendshipStatus,
    ): List<Friendship>
    
    // исходящие заявки (пользователь является отправителем)
    @Query(
        """
        SELECT f FROM Friendship f
        WHERE (f.user1.id = :userId AND f.statusForUser1 = :pendingStatus AND f.statusForUser2 = :noConnectionStatus)
           OR (f.user2.id = :userId AND f.statusForUser2 = :pendingStatus AND f.statusForUser1 = :noConnectionStatus)
    """,
    )
    fun findOutgoingRequests(
        @Param("userId") userId: Long,
        @Param("pendingStatus") pendingStatus: FriendshipStatus,
        @Param("noConnectionStatus") noConnectionStatus: FriendshipStatus,
    ): List<Friendship>
}
