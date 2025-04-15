package com.aashushaikh.practice_1.services

import com.aashushaikh.practice_1.database.models.Product
import com.aashushaikh.practice_1.database.repositories.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ProductService(
    private val repository: ProductRepository
) {

    fun getAll(): List<Product> = repository.findAll()

    fun getById(id: String): Product =
        repository.findById(id).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not found")
        }

    fun add(product: Product): Product = repository.save(product)

    fun delete(id: String): Product {
        val product = getById(id)
        repository.delete(product)
        return product
    }

//    fun findByPriceRange(min: Double, max: Double): List<Product>{
//        val products = repository.findByPriceBetween(min, max)
//            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No products in this range")
//        return products
//    }
    fun findByPriceRange(min: Double, max: Double, pageable: Pageable): Page<Product> {
        return repository.findByPriceBetween(min, max, pageable)
    }


    fun findByCategoryName(name: String): List<Product>{
        val products = repository.findByCategoryName(name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No products in this range")
        return products
    }

    fun searchByNameNative(keyword: String): List<Product>{
        val products = repository.searchByNameNative(keyword)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No products in this range")
        return products
    }

}