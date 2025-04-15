package com.aashushaikh.practice_1.controllers

import com.aashushaikh.practice_1.security.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    data class AuthRequest(
        val email: String,
        val password: String,
        val roles: Set<String>?
    )

    data class RefreshRequest(
        val refreshToken: String
    )

    private fun hasAdminRole(): Boolean {
        val roles = SecurityContextHolder.getContext().authentication.authorities
        return roles.any { it.authority == "ROLE_ADMIN" }
    }

    @PostMapping("/register")
    fun register(@RequestBody body: AuthRequest) {
        // Default roles to USER if not provided
        val roles = body.roles ?: setOf("ROLE_USER")

        // If roles are provided, but not by an ADMIN, throw an error (optional logic)
        if (roles != setOf("ROLE_USER") && !hasAdminRole()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only Admins can register with roles")
        }

        authService.register(email = body.email, password = body.password, roles = roles)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    fun adminRegister(@RequestBody body: AuthRequest) {
        val roles = body.roles ?: setOf("ROLE_USER") // Default to USER if no roles provided
        authService.register(email = body.email, password = body.password, roles = roles)
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/login")
    fun login(
        @RequestBody body: AuthRequest
    ): AuthService.TokenPair{
        return authService.login(email = body.email, password = body.password)
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body: RefreshRequest
    ): AuthService.TokenPair{
        return authService.refresh(refreshToken = body.refreshToken)
    }

}