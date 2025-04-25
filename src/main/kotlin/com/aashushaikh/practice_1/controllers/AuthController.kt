package com.aashushaikh.practice_1.controllers

import com.aashushaikh.practice_1.security.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Auth operations")
class AuthController(
    private val authService: AuthService
) {

    @Schema(description = "Request object for authentication and registration")
    data class AuthRequest(
        @Schema(description = "User email", example = "user@example.com")
        val email: String,

        @Schema(description = "User password", example = "password123")
        val password: String,

        @Schema(description = "Set of roles", example = "[\"ROLE_USER\"]", required = false)
        val roles: Set<String>?
    )

    @Schema(description = "Request object for refreshing tokens")
    data class RefreshRequest(
        @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzI1...")
        val refreshToken: String
    )

    private fun hasAdminRole(): Boolean {
        val roles = SecurityContextHolder.getContext().authentication.authorities
        return roles.any { it.authority == "ROLE_ADMIN" }
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register User",
        description = "Registers a user with 'USER' role using given credentials",
        responses = [
            ApiResponse(responseCode = "200", description = "User registered successfully"),
            ApiResponse(responseCode = "401", description = "Unauthorized if non-admin tries to assign special roles")
        ]
    )
    fun register(@RequestBody body: AuthRequest) {
        val roles = body.roles ?: setOf("ROLE_USER")
        if (roles != setOf("ROLE_USER") && !hasAdminRole()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only Admins can register with special roles")
        }
        authService.register(email = body.email, password = body.password, roles = roles)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    @Operation(
        summary = "Admin Registration",
        description = "Admins can register a user with special roles (e.g., ADMIN, MODERATOR)",
        responses = [
            ApiResponse(responseCode = "200", description = "User registered with custom roles"),
            ApiResponse(responseCode = "403", description = "Forbidden if non-admin accesses")
        ]
    )
    fun adminRegister(@RequestBody body: AuthRequest) {
        val roles = body.roles ?: setOf("ROLE_USER")
        authService.register(email = body.email, password = body.password, roles = roles)
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/login")
    @Operation(
        summary = "Login with credentials",
        description = "Returns access and refresh tokens",
        responses = [
            ApiResponse(responseCode = "200", description = "Login successful, tokens returned"),
            ApiResponse(responseCode = "401", description = "Invalid credentials")
        ]
    )
    suspend fun login(@RequestBody body: AuthRequest): AuthService.TokenPair {
        println("🔵 Controller Thread: ${Thread.currentThread().name}")
        return authService.login(email = body.email, password = body.password)
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh access token",
        description = "Takes a valid refresh token and returns a new access token pair",
        responses = [
            ApiResponse(responseCode = "200", description = "Token refreshed"),
            ApiResponse(responseCode = "401", description = "Invalid refresh token")
        ]
    )
    fun refresh(@RequestBody body: RefreshRequest): AuthService.TokenPair {
        return authService.refresh(refreshToken = body.refreshToken)
    }
}