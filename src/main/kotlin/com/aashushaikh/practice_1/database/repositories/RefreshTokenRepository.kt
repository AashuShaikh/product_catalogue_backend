package com.aashushaikh.practice_1.database.repositories

import com.aashushaikh.practice_1.database.models.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository: JpaRepository<RefreshToken, String> {
    fun findByUserIdAndHashedToken(userId: String, hashedToken: String): RefreshToken?
    fun deleteByUserIdAndHashedToken(userId: String, hashedToken: String)
}