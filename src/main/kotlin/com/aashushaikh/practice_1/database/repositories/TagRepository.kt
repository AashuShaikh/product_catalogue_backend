package com.aashushaikh.practice_1.database.repositories

import com.aashushaikh.practice_1.database.models.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository: JpaRepository<Tag, String>