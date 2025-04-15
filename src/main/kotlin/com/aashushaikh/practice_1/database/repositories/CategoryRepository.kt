package com.aashushaikh.practice_1.database.repositories

import com.aashushaikh.practice_1.database.models.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository: JpaRepository<Category, String>