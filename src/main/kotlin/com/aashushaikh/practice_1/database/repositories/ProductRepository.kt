package com.aashushaikh.practice_1.database.repositories

import com.aashushaikh.practice_1.database.models.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository: JpaRepository<Product, String>{

//    fun findByPriceBetween(min: Double, max: Double): List<Product>?
    fun findByPriceBetween(min: Double, max: Double, pageable: Pageable): Page<Product>

//@Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
//fun findInRange(
//    @Param("min") min: Double,
//    @Param("max") max: Double,
//    pageable: Pageable
//): Page<Product>

    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName")
    fun findByCategoryName(@Param("categoryName") name: String): List<Product>?

    @Query(value = "SELECT * FROM product WHERE name ILIKE %:keyword%", nativeQuery = true)
    fun searchByNameNative(@Param("keyword") keyword: String): List<Product>?

}