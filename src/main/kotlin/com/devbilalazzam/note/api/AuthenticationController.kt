package com.devbilalazzam.note.api

import com.devbilalazzam.note.api.model.AuthenticationRequest
import com.devbilalazzam.note.api.model.RefreshRequest
import com.devbilalazzam.note.api.model.TokenPairResponse
import com.devbilalazzam.note.service.AuthenticationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody body: AuthenticationRequest
    ) {
        authenticationService.register(body.email, body.password)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody body: AuthenticationRequest
    ): TokenPairResponse {
        return TokenPairResponse.fromTokenPair(
            authenticationService.login(body.email, body.password)
        )
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body: RefreshRequest
    ): TokenPairResponse {
        return TokenPairResponse.fromTokenPair(
            authenticationService.refresh(body.refreshToken)
        )
    }
}