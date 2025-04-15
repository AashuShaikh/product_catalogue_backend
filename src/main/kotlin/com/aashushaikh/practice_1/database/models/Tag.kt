package com.aashushaikh.practice_1.database.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany

@Entity
data class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    val name: String = "",

    @ManyToMany(mappedBy = "tags")
    val product: MutableList<Product> = mutableListOf()
)
