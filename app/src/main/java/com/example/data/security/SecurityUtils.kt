package com.example.data.security

import java.security.MessageDigest
import java.util.UUID

object SecurityUtils {
    private const val SALT = "ETFC_ADWA_FIGHT_NIGHT_SALT_2026"

    /**
     * Computes a SHA-256 password hash with cryptographic salt.
     */
    fun hashPassword(password: String): String {
        val input = "$SALT:$password"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Generates a signed session token.
     */
    fun generateAuthToken(userId: String, role: String): String {
        val raw = "$userId:$role:${System.currentTimeMillis()}:${UUID.randomUUID().toString().take(8)}"
        val signature = MessageDigest.getInstance("SHA-256").digest("$SALT:$raw".toByteArray(Charsets.UTF_8))
            .take(6)
            .toByteArray()
            .joinToString("") { "%02X".format(it) }
        return "ETFC.$role.${UUID.randomUUID().toString().take(12)}.$signature"
    }

    /**
     * Verifies if password matches stored hash.
     */
    fun verifyPassword(password: String, storedHash: String): Boolean {
        return hashPassword(password) == storedHash
    }
}
