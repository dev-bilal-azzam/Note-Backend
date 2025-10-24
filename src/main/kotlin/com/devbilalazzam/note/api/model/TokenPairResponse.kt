package com.devbilalazzam.note.api.model

import com.devbilalazzam.note.service.model.TokenPair

data class TokenPairResponse(
    val accessToken: String,
    val refreshToken: String,
) {
    companion object {
        fun fromTokenPair(tokenPair: TokenPair): TokenPairResponse {
            return TokenPairResponse(
                accessToken = tokenPair.accessToken,
                refreshToken = tokenPair.refreshToken,
            )
        }
    }
}