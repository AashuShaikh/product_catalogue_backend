package com.aashushaikh.practice_1

import org.apache.coyote.BadRequestException
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalErrorHandler {

    @ExceptionHandler(BadRequestException::class)
    fun productNotFoundException(){

    }

}