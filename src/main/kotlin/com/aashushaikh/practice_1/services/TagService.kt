package com.aashushaikh.practice_1.services

import com.aashushaikh.practice_1.database.models.Tag
import com.aashushaikh.practice_1.database.repositories.TagRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TagService(
    private val tagRepository: TagRepository
) {

    fun getAllTags(): List<Tag>{
        val tags = tagRepository.findAll()
        if(tags == null){
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No Tags found")
        }
        return tags
    }

    fun saveTag(tag: Tag){
        tagRepository.save(tag)
    }

    fun getTag(id: String): Tag{
        val tag = tagRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Tag Not Found")
        }
        return tag
    }
}