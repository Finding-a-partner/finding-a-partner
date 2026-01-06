package com.findingpartners.user_service.service.impl

import com.findingpartners.user_service.database.entity.User
import com.findingpartners.user_service.database.repository.UserDao
import com.findingpartners.user_service.errors.ResourceNotFoundException
import com.findingpartners.user_service.model.request.UserRequest
import com.findingpartners.user_service.model.response.UserResponse
import com.findingpartners.user_service.service.UserService
import com.findingpartners.user_service.util.UserMapper
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    val dao: UserDao,
    val mapper: UserMapper,
) : UserService {
    override fun getAll(): List<UserResponse> =
        dao.findAll().map {
            mapper.entityToResponse(it)
        }

    override fun getByLogin(login: String): UserResponse {
        return mapper.entityToResponse(
            dao.getByLogin(login).orElseThrow {
                RuntimeException("User with login $login not found")
            },
        )
    }

    override fun getById(id: Long): UserResponse {
        return mapper.entityToResponse(dao.findById(id).orElseThrow() { throw ResourceNotFoundException(id) })
    }

    override fun searchUsers(query: String): List<UserResponse> {
        return dao.findByLoginContainingOrNameContainingOrSurnameContaining(query, query, query)
            .map { mapper.entityToResponse(it) }
    }

    override fun getByIds(ids: List<Long>): List<UserResponse> {
        return dao.findAllById(ids).map { mapper.entityToResponse(it) }
    }

    @Transactional
    override fun update(id: Long, request: UserRequest): UserResponse {
        val entity = dao.findById(id).orElseThrow { throw ResourceNotFoundException(id) }
            .apply {
                login = request.login
                email = request.email
                description = request.description
            }
        val updatedEntity = dao.save(entity) // Явно сохраняем изменения
        return mapper.entityToResponse(entity)
    }

    override fun delete(id: Long) {
        val entity = dao.findById(id).orElseThrow { throw ResourceNotFoundException(id) }
        dao.delete(entity)
    }

    override fun create(request: UserRequest): UserResponse {
        try {
            val entity = User(
                login = request.login,
                email = request.email,
                description = request.description,
                name = request.name,
                surname = request.surname,
            )
            return mapper.entityToResponse(dao.save(entity))
        } catch (e: Exception) {
            println("exeption in create: ${e.message}")
            throw e
        }
    }
}
