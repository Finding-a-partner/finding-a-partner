package com.findingpartners.user_service.database.entity

import com.findingpartners.user_service.enum.FriendshipStatus
import jakarta.persistence.*

@Entity
@Table(
    name = "friendship",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id_1", "user_id_2"])
    ]
)
@org.hibernate.annotations.Check(
    name = "check_user_order",
    constraints = "(user_id_1) < (user_id_2)"
)
class Friendship(
    @ManyToOne
    @JoinColumn(name = "user_id_1", nullable = false)
    val user1: User,
    
    @ManyToOne
    @JoinColumn(name = "user_id_2", nullable = false)
    val user2: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "status_for_user_1", nullable = false)
    var statusForUser1: FriendshipStatus = FriendshipStatus.PENDING,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status_for_user_2", nullable = false)
    var statusForUser2: FriendshipStatus = FriendshipStatus.PENDING,

) : AbstractEntity() {
    fun getStatusForUser(userId: Long): FriendshipStatus {
        return when (userId) {
            user1.id -> statusForUser1
            user2.id -> statusForUser2
            else -> throw IllegalArgumentException("User $userId is not part of this friendship")
        }
    }

    fun getOtherUser(userId: Long): User {
        return when (userId) {
            user1.id -> user2
            user2.id -> user1
            else -> throw IllegalArgumentException("User $userId is not part of this friendship")
        }
    }

    fun involvesUser(userId: Long): Boolean {
        return user1.id == userId || user2.id == userId
    }
}
