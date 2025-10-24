package com.devbilalazzam.note.service

import com.devbilalazzam.note.database.model.RefreshToken
import com.devbilalazzam.note.database.model.User
import com.devbilalazzam.note.database.repository.RefreshTokenRepository
import com.devbilalazzam.note.database.repository.UserRepository
import com.devbilalazzam.note.security.HashEncoder
import com.devbilalazzam.note.security.JWTService
import com.devbilalazzam.note.service.model.TokenPair
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthenticationService(
    private val jwtService: JWTService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun register(email: String, password: String): User {
        val user = userRepository.findByEmail(email.trim())
        if (user != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User with email: $email already exists!")
        }
        return userRepository.save(
            User(
                email = email,
                hashedPassword = hashEncoder.encode(password),
            )
        )
    }

    fun login(email: String, password: String): TokenPair {
        val user = userRepository.findByEmail(email)
            ?: throw BadCredentialsException("This email is not attached to any user!")

        if (!hashEncoder.matches(password, user.hashedPassword)) {
            throw BadCredentialsException("Invalid password!")
        }

        val accessToken = jwtService.generateAccessToken(user.id.toString())
        val refreshToken = jwtService.generateRefreshToken(user.id.toString())

        storeRefreshToken(user.id, refreshToken)
        return TokenPair(accessToken, refreshToken)
    }

    @Transactional
    fun refresh(refreshToken: String): TokenPair {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid Refresh Token!")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            ResponseStatusException(HttpStatusCode.valueOf(404), "User Not Found, Invalid Refresh Token!") // User not found
        }

        val hashedToken = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashedToken)
            ?: ResponseStatusException(HttpStatusCode.valueOf(401), "Refresh Token Not Recognized Or Expired!")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashedToken)

        val accessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)

        storeRefreshToken(user.id, newRefreshToken)
        return TokenPair(accessToken, newRefreshToken)
    }

    fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String) {
        val hashedToken = hashToken(rawRefreshToken)
        val expiryMilliSeconds = jwtService.refreshTokenValidityMilliseconds
        val expiresAt = Instant.now().plusSeconds(expiryMilliSeconds)
        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                hashedToken = hashedToken,
                expiresAt = expiresAt
            )
        )
    }

    private fun hashToken(token: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashedBytes)
    }
}