package com.devbilalazzam.note.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class JWTService(
    @Value("\${jwt.secret}") private val jwtSecret: String
) {
    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    val accessTokenValidityMilliseconds = 15L * 60L * 1000L
    val refreshTokenValidityMilliseconds = 30L * 24L *60L * 60L * 1000L

    private fun generateToken(
        userId: String,
        type: String,
        expiry: Long
    ): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId)
            .claim("type", type)
            .issuedAt(Date())
            .expiration(Date(now.time + expiry))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(userId: String) = generateToken(userId, "access", accessTokenValidityMilliseconds)

    fun generateRefreshToken(userId: String) = generateToken(userId, "refresh", refreshTokenValidityMilliseconds)

    fun validateAccessToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val type =  claims["type"] as? String ?: return false
        return type == "access"
    }

    fun validateRefreshToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val type =  claims["type"] as? String ?: return false
        return type == "refresh"
    }

    fun getUserIdFromToken(token: String): String {
        val claims = parseAllClaims(token) ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid Token!")
        return claims.subject
    }

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = if (token.startsWith("Bearer ")) {
            token.removePrefix("Bearer ")
        } else token
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (_: Exception) { null }
    }
}