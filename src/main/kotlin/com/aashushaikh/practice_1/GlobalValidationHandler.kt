package com.aashushaikh.practice_1

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalValidationHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleInvalidField(exception: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>>{
        val error = exception.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid Value")
        }
        return ResponseEntity.status(400).body(mapOf("errors" to error))
    }

}