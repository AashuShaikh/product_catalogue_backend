package com.aashushaikh.practice_1.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component //instance managed by Spring. automatically injected by SpringBoot
class HashEncoder {

    private val bCrypt = BCryptPasswordEncoder()

    fun encode(raw: String): String = bCrypt.encode(raw)

    fun matches(raw: String, hashed: String): Boolean = bCrypt.matches(raw, hashed)

}