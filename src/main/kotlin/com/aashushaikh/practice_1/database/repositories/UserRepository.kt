package com.aashushaikh.practice_1.database.repositories

import com.aashushaikh.practice_1.database.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, String>{
    fun findByEmail(email: String): User?
}