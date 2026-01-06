package com.finding_a_partner.group_service.database.repository

import com.finding_a_partner.group_service.database.entity.GroupMembership
import com.finding_a_partner.group_service.database.entity.GroupMembershipId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GroupMembershipDao : JpaRepository<GroupMembership, GroupMembershipId> {
    fun findAllByIdGroupId(groupId: Long): List<GroupMembership>

    @Query(
        """
    SELECT gm FROM GroupMembership gm 
    JOIN FETCH gm.group g
    WHERE gm.id.userId = :userId
""",
    )
    fun findAllWithGroupByUserId(userId: Long): List<GroupMembership>
}
