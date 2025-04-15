package com.aashushaikh.practice_1.controllers

import com.aashushaikh.practice_1.database.models.Category
import com.aashushaikh.practice_1.database.models.Product
import com.aashushaikh.practice_1.database.repositories.CategoryRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/categories")
class CategoryController(private val categoryRepo: CategoryRepository) {

    data class CategoryRequest(
        val id: String?,
        val name: String,
    )

    data class CategoryResponse(
        val id: String,
        val name: String,
        val productIds: List<String>
    )

    @PostMapping
    fun create(@RequestBody body: CategoryRequest): ResponseEntity<Category> {
        val category = Category(
            name = body.name
        )
        return ResponseEntity.ok(categoryRepo.save(category))
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<CategoryResponse>> {
        val categories = categoryRepo.findAll()
        return ResponseEntity.ok(categories.map {
            it.toCategoryResponse()
        })
    }

    @GetMapping(params = ["categoryId"])
    fun getCategoryById(
        @RequestParam categoryId: String
    ): ResponseEntity<CategoryResponse> {
        val category = categoryRepo.findById(categoryId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Category with given Id not found")
        }
        return ResponseEntity.ok(category.toCategoryResponse())
    }

    @GetMapping("/{id}/products")
    fun getProductsForCategory(@PathVariable id: String): ResponseEntity<List<ProductController.ProductResponse>> {
        val category = categoryRepo.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found")
        }

        val products = category.products.map { it.toProductResponse() }

        return ResponseEntity.ok(products)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: String
    ): ResponseEntity.BodyBuilder{
        categoryRepo.deleteById(id)
        return ResponseEntity.ok()
    }
}

fun Category.toCategoryResponse(): CategoryController.CategoryResponse{
    return CategoryController.CategoryResponse(
        id = id ?: "INVALID",
        name = name,
        productIds = products.map {
            it.id?.let { productId ->
                productId
            }?: "Invalid Product ID"
        }
    )
}
