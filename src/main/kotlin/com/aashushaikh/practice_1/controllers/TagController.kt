package com.aashushaikh.practice_1.controllers

import com.aashushaikh.practice_1.database.models.Tag
import com.aashushaikh.practice_1.services.TagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tags")
class TagController (
    private val tagService: TagService
){

    data class TagRequest(
        val name: String
    )

    data class TagResponse(
        val id: String,
        val name: String,
        val productIds: List<String>
    )

    @GetMapping
    fun getAllTags(): ResponseEntity<List<TagResponse>>{
        val tags = tagService.getAllTags()
        return ResponseEntity.ok(tags.map { it.toTagResponse() })
    }

    @GetMapping("/{id}")
    fun getTag(
        @PathVariable id: String
    ): ResponseEntity<TagResponse>{
        val tag = tagService.getTag(id)
        return ResponseEntity.ok(tag.toTagResponse())
    }

    @PostMapping
    fun saveTag(
        @RequestBody body: TagRequest
    ): ResponseEntity<TagResponse> {
        val tag = Tag(
            name = body.name,
        )
        tagService.saveTag(tag)
        return ResponseEntity.ok(tag.toTagResponse())
    }

}

fun Tag.toTagResponse(): TagController.TagResponse{
    return TagController.TagResponse(
        id = id?: "INVALID",
        name = name,
        productIds = product.mapNotNull { it.id }
    )
}