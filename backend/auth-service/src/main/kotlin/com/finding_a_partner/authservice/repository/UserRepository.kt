package com.finding_a_partner.authservice.repository

import com.finding_a_partner.authservice.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findByLogin(login: String): User?
}
