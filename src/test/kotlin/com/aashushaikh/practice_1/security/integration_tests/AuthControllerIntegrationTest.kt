package com.aashushaikh.practice_1.security.integration_tests

import com.aashushaikh.practice_1.database.models.User
import com.aashushaikh.practice_1.database.repositories.UserRepository
import com.aashushaikh.practice_1.security.HashEncoder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType

@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AuthControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // Test for registering a user successfully
    @Test
    fun `should register a user successfully`() {
        val registerRequest = mapOf(
            "email" to "testuser@example.com",
            "password" to "password123",
            "roles" to listOf("ROLE_USER")
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // Expect HTTP 201 status
    }

    // Test for registering an already existing user (Conflict)
    @Test
    fun `should return conflict when user already exists`() {
        val registerRequest = mapOf(
            "email" to "existinguser@example.com",
            "password" to "password123",
            "roles" to listOf("ROLE_USER")
        )

        // First, create a user with the same email
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        // Now, try to create the same user again
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isConflict) // Expect HTTP 409 CONFLICT
    }

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var hashEncoder: HashEncoder

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()

        val user = User(
            email = "testloginuser@example.com",
            hashedPassword = hashEncoder.encode("password123"),
            roles = setOf("ROLE_USER")
        )
        userRepository.save(user)
    }

    @Test
    fun `should login successfully and return token`() {
        val request = mapOf(
            "email" to "testloginuser@example.com",
            "password" to "password123"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("accessToken").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("refreshToken").exists())
    }

    @Test
    fun `register should return UNAUTHORIZED if non-admin tries to register with custom roles`() {
        val requestBody = """{
        "email": "testregisteruser@example.com",
        "password": "password123",
        "roles": ["ROLE_ADMIN"]
    }"""
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)  // Check for 401 Unauthorized
            .andExpect { result ->
                result.response.errorMessage?.contains("Only Admins can register with roles")?.let { assertTrue(it) }
            }// Check the error message
    }

    @Test
    fun `login should return BAD_REQUEST if credentials are incorrect`() {
        val loginRequest = """{
        "email": "testloginuser@example.com",
        "password": "wrongpassword"
    }"""
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginRequest))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect { result ->
                result.response.errorMessage?.contains("Invalid credentials")?.let {
                    assertTrue(it)
                }
            }
    }

    @Test
    fun `refreshing should return UNAUTHORIZED if refresh token is invalid`() {
        val expiredRefreshToken = "invalid.refresh.token"
        val request = """{
        "refreshToken": "$expiredRefreshToken"
    }"""
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect{result ->
                result.response.errorMessage?.contains("Invalid Refresh Token")?.let {
                    assertTrue(it)
                }
            }
    }

}
