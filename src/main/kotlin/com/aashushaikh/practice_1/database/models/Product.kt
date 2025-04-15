package com.aashushaikh.practice_1.database.models

import jakarta.persistence.*
import java.time.Instant

@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val createdAt: Instant = Instant.now(),
    @ManyToOne
    @JoinColumn(name = "category_id")
    val category: Category? = null,

    @ManyToMany
    @JoinTable(
        name = "product_tags",
        joinColumns = [JoinColumn(name = "product_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    val tags: MutableList<Tag> = mutableListOf()
)
