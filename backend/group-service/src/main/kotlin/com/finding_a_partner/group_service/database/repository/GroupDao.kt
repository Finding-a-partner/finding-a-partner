package com.finding_a_partner.group_service.database.repository

import com.finding_a_partner.group_service.database.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupDao : JpaRepository<Group, Long> {
    fun findByNameContainingOrDescriptionContaining(query: String, query1: String): List<Group>
}
