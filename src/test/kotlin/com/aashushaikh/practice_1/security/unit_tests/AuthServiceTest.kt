package com.aashushaikh.practice_1.security.unit_tests

import com.aashushaikh.practice_1.database.models.User
import com.aashushaikh.practice_1.database.repositories.RefreshTokenRepository
import com.aashushaikh.practice_1.database.repositories.UserRepository
import com.aashushaikh.practice_1.security.AuthService
import com.aashushaikh.practice_1.security.HashEncoder
import com.aashushaikh.practice_1.security.JwtService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.test.Test

class AuthServiceTest {

    private val jwtService = mock(JwtService::class.java)
    private val userRepository = mock(UserRepository::class.java)
    private val refreshTokenRepository = mock(RefreshTokenRepository::class.java)
    private val hashEncoder = HashEncoder() // can be real unless it's slow or external

    private val authService = AuthService(jwtService, userRepository, hashEncoder, refreshTokenRepository)

    @Test
    fun `register should save new user if not already exists`() {
        val email = "test@example.com"
        val password = "pass123"
        val roles = setOf("ROLE_USER")

        `when`(userRepository.findByEmail(email)).thenReturn(null)

        val userToSave = User(UUID.randomUUID().toString(), email, "hashed", roles)
        `when`(userRepository.save(any(User::class.java))).thenReturn(userToSave)

        authService.register(email, password, roles)

        verify(userRepository, times(1)).save(any(User::class.java))
    }

    @Test
    fun `register should throw exception if user already exists`() {
        val email = "existing@example.com"
        val password = "pass123"
        val roles = setOf("ROLE_USER")

        val existingUser = User(UUID.randomUUID().toString(), email, "hashed", roles)
        `when`(userRepository.findByEmail(email)).thenReturn(existingUser)

        val exception = assertThrows<ResponseStatusException> {
            authService.register(email, password, roles)
        }

        assertEquals(HttpStatus.CONFLICT, exception.statusCode)
    }

}