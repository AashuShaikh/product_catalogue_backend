package com.aashushaikh.practice_1.database.models

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null, // Auto-generated primary key
    @Column(nullable = false)
    val userId: String? = null, // User ID to associate the refresh token with

    @Column(nullable = false)
    val expiresAt: Instant = Instant.now(), // Expiration time of the refresh token

    @Column(nullable = false)
    val hashedToken: String? = null, // The hashed refresh token

    @Column(nullable = false)
    val createdAt: Instant = Instant.now() // Time when the refresh token was created
)

