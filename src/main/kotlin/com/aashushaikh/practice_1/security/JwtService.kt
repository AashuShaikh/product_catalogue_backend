package com.aashushaikh.practice_1.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.lang.Exception
import java.util.*

@Component
class JwtService(
    @Value("\${jwt.secret}") private val jwtSecret: String
) {
    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    private val accessTokenValidityMs = 15L * 60L * 1000L // 15 minutes
    val refreshTokenValidityMs = 30L * 24L * 60L * 60L * 1000L // 30 days

    private fun generateToken(userId: String, roles: Set<String>, type: String, validityMs: Long): String {
        val now = Date()
        val expiry = Date(now.time + validityMs)

        return Jwts.builder()
            .subject(userId)
            .claim("type", type)
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(userId: String, roles: Set<String>): String =
        generateToken(userId, roles, "access", accessTokenValidityMs)

    fun generateRefreshToken(userId: String, roles: Set<String>): String =
        generateToken(userId, roles, "refresh", refreshTokenValidityMs)

    fun validateAccessToken(token: String): Boolean =
        getTokenType(token) == "access"

    fun validateRefreshToken(token: String): Boolean =
        getTokenType(token) == "refresh"

    fun getUserIdFromToken(token: String): String {
        val claims = parseAllClaims(token)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token")
        return claims.subject
    }

    fun getRolesFromToken(token: String): List<String>? {
        val claims = parseAllClaims(token) ?: return null
        val rawRoles = claims["roles"]
        println("Raw roles from token: $rawRoles")
        return when (rawRoles) {
            is List<*> -> rawRoles.filterIsInstance<String>()
            else -> null
        }
    }

    private fun getTokenType(token: String): String? {
        val claims = parseAllClaims(token) ?: return null
        return claims["type"] as? String
    }

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = token.removePrefix("Bearer ").trim()
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (e: Exception) {
            null
        }
    }
}

