package com.devbilalazzam.note.validation

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ValidationHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(error: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = error.bindingResult.allErrors.map { it.defaultMessage ?: "Invalid request!" }

        return ResponseEntity
            .status(400)
            .body(mapOf("errors" to errors))
    }
}