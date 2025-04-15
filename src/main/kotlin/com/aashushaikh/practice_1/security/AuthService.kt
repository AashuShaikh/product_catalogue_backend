package com.aashushaikh.practice_1.security

import com.aashushaikh.practice_1.database.models.RefreshToken
import com.aashushaikh.practice_1.database.models.User
import com.aashushaikh.practice_1.database.repositories.RefreshTokenRepository
import com.aashushaikh.practice_1.database.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.*

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    fun register(email: String, password: String, roles: Set<String>): User {
        val user = userRepository.findByEmail(email)
        if(user != null){
            throw ResponseStatusException(HttpStatus.CONFLICT)
        }
        return userRepository.save(
            User(
                email = email,
                hashedPassword = hashEncoder.encode(password),
                roles = roles
            )
        )
    }

    fun login(email: String, password: String): TokenPair{
        val user = userRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid Credentials")
        if(!hashEncoder.matches(password, user.hashedPassword)){ //doesn't match
            throw BadCredentialsException("Invalid Credentials")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id!!, user.roles)
        val newRefreshToken = jwtService.generateRefreshToken(user.id!!, user.roles)

        storeRefreshToken(user.id!!, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    fun refresh(refreshToken: String): TokenPair{
        if(!jwtService.validateRefreshToken(refreshToken)){
            throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid Refresh Token")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid Refresh Token")
        }

        val hashedToken = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id!!, hashedToken)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "Refresh token not recognized, maybe used or expired")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashedToken)

        val newAccessToken = jwtService.generateAccessToken(user.id, user.roles)
        val newRefreshToken = jwtService.generateRefreshToken(user.id, user.roles)

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    private fun storeRefreshToken(userId: String, rawRefreshToken: String) {
        val hashedToken = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashedToken
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}