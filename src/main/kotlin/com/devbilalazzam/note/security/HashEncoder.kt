package com.devbilalazzam.note.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class HashEncoder {
    private val bCryptEncoder = BCryptPasswordEncoder()
    fun encode(rawString: String): String = bCryptEncoder.encode(rawString)

    fun matches(rawString: String, hashedString: String) = bCryptEncoder.matches(rawString, hashedString)
}