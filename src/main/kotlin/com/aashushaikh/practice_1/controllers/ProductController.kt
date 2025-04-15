package com.aashushaikh.practice_1.controllers

import com.aashushaikh.practice_1.database.models.Product
import com.aashushaikh.practice_1.database.repositories.CategoryRepository
import com.aashushaikh.practice_1.services.ProductService
import com.aashushaikh.practice_1.services.TagService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
    private val categoryRepository: CategoryRepository,
    private val tagService: TagService
) {

    data class ProductRequest(
        val id: String?,
        @field:NotBlank(message = "This Field cannot be empty")
        val name: String,
        @field:Min(value = 0, message = "Price cannot be negative")
        val price: Double,
        @field:Min(value = 0, message = "Quantity cannot be negative")
        val quantity: Int,
        val categoryId: String
    )

    data class ProductResponse(
        val id: String,
        val name: String,
        val price: Double,
        val quantity: Int,
        val createdAt: Instant,
        val categoryId: String,
        val tagIds: List<String>
    )

    @GetMapping
    fun getProducts(): ResponseEntity<List<ProductResponse>>{
        val productsList = productService.getAll()
        val body = productsList.map {
            it.toProductResponse()
        }
        return ResponseEntity.ok(body)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun saveProduct(
        @Valid @RequestBody body: ProductRequest
    ): ResponseEntity<ProductResponse>{
        val category = categoryRepository.findById(body.categoryId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Category Not Found")
        }
        val product = Product(
            name = body.name,
            price = body.price,
            quantity = body.quantity,
            createdAt = Instant.now(),
            category = category
        )
        val answer = productService.add(product)
        return ResponseEntity.ok(product.toProductResponse())
    }

    @GetMapping("/{id}")
    fun getProduct(
        @PathVariable id: String
    ): ResponseEntity<ProductResponse>{
        val product = productService.getById(id)
        return ResponseEntity.ok(product.toProductResponse())
    }

    @GetMapping(params = ["min", "max", "page", "size", "sort", "direction"])
    fun getByPriceRangeAndPage(
        @RequestParam min: Double,
        @RequestParam max: Double,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 10,
        @RequestParam sort: String = "name",
        @RequestParam direction: String = "ASC"
    ): ResponseEntity<Page<ProductResponse>> {
        val pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sort)
        val pageResult = productService.findByPriceRange(min, max, pageable)
        return ResponseEntity.ok(pageResult.map { it.toProductResponse() })
    }


    @GetMapping(params = ["categoryName"])
    fun getByCategoryName(
        @RequestParam categoryName: String
    ): ResponseEntity<List<ProductResponse>>{
        val products = productService.findByCategoryName(categoryName).map {
            it.toProductResponse()
        }
        return ResponseEntity.ok(products)
    }

    @GetMapping(params = ["keyword"])
    fun getBySearchKeyword(
        @RequestParam keyword: String
    ): ResponseEntity<List<ProductResponse>>{
        val products = productService.searchByNameNative(keyword).map {
            it.toProductResponse()
        }
        return ResponseEntity.ok(products)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: String,
        @Valid @RequestBody body: ProductRequest
    ): ResponseEntity<ProductResponse>{
        val product = productService.getById(id)
        val updated = product.copy(
            id = body.id ?: product.id,
            name = body.name,
            price = body.price,
            quantity = body.quantity
        )
        val answer = productService.add(updated)
        return ResponseEntity.ok(updated.toProductResponse())
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: String,
    ): ResponseEntity<ProductResponse>{
        val product = productService.delete(id)
        return ResponseEntity.ok(product.toProductResponse())
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{product_id}/tags/{tag_id}")
    fun addTagToProduct(
        @PathVariable product_id: String,
        @PathVariable tag_id: String
    ): ResponseEntity<ProductResponse>{
        val tag = tagService.getTag(tag_id)
        val product = productService.getById(product_id)
        if (!product.tags.contains(tag)) {
            product.tags.add(tag)
        }
        if (!tag.product.contains(product)) {
            tag.product.add(product)
        }
        val updatedProduct = productService.add(product)
        return ResponseEntity.ok(updatedProduct.toProductResponse())
    }

}

fun Product.toProductResponse(): ProductController.ProductResponse{
    return ProductController.ProductResponse(
        id = id ?: "INVALID",
        name = name,
        price = price,
        quantity = quantity,
        createdAt = createdAt,
        categoryId = category?.id?.let {
            it
        } ?: "INVALID CATEGORY",
        tagIds = tags.mapNotNull { it.id }
    )
}