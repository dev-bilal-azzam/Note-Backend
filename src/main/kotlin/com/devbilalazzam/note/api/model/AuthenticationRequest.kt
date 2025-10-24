package com.devbilalazzam.note.api.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern


data class AuthenticationRequest(
    @field:Email(message = "Invalid email format!")
    val email:String,
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$",
        message = "Invalid Password!")
    val password: String
)

data class RefreshRequest(
    val refreshToken: String
)
