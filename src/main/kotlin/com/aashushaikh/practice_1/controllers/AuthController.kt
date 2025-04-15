package com.aashushaikh.practice_1.controllers

import com.aashushaikh.practice_1.security.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    data class AuthRequest(
        val email: String,
        val password: String
    )

    data class RefreshRequest(
        val refreshToken: String
    )

    @PostMapping("/register")
    fun register(
        @RequestBody body: AuthRequest
    ){
        authService.register(email = body.email, password = body.password)
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