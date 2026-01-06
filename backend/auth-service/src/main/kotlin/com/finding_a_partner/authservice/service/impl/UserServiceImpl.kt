package com.finding_a_partner.authservice.service.impl

import com.finding_a_partner.authservice.entity.User
import com.finding_a_partner.authservice.feign.UserClient
import com.finding_a_partner.authservice.feign.UserRequest
import com.finding_a_partner.authservice.feign.UserResponse
import com.finding_a_partner.authservice.model.RegisterRequest
import com.finding_a_partner.authservice.repository.UserRepository
import com.finding_a_partner.authservice.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userClient: UserClient,
) : UserService {

    override fun registerUser(registerRequest: RegisterRequest): UserResponse {
        userRepository.findByLogin(registerRequest.login)?.let {
            throw IllegalArgumentException("Пользователь с таким login уже существует.")
        }

        val saved = userClient.create(
            UserRequest(
                name = registerRequest.name,
                surname = registerRequest.surname,
                email = registerRequest.email,
                login = registerRequest.login,
                description = registerRequest.description,
            ),
        )

        val user = User(
            login = registerRequest.login,
            email = registerRequest.email,
            password = passwordEncoder.encode(registerRequest.password),
        )

        userRepository.save(user)

        return saved
    }

    override fun authenticate(login: String, password: String): UserResponse? {
        val user = userRepository.findByLogin(login)
        return if (user != null && passwordEncoder.matches(password, user.password)) {
            userClient.getByLogin(login)
        } else {
            null
        }
    }
}
