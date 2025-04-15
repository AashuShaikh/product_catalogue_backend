package com.aashushaikh.practice_1.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        println("Auth Header: $authHeader")
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            println("Auth Header: $authHeader")
            if(jwtService.validateAccessToken(authHeader)) {
                val userId = jwtService.getUserIdFromToken(authHeader)
                val roles = jwtService.getRolesFromToken(authHeader)
                val authorities = roles?.map { SimpleGrantedAuthority(it.toString()) }
                println("Auth Header: $authHeader, userId: $userId")
                println("Roles from token: $roles")
                val auth = UsernamePasswordAuthenticationToken(userId, null, authorities)
                SecurityContextHolder.getContext().authentication = auth
                println("Authenticated user: $userId, for request ${request.requestURI}")
            }
        }
        filterChain.doFilter(request, response)
    }
}
