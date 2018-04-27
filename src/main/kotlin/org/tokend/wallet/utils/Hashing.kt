package org.tokend.wallet.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Hashing {
    fun sha256(data: ByteArray): ByteArray {
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update(data)
            return digest.digest()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("SHA-256 not implemented")
        }
    }
}