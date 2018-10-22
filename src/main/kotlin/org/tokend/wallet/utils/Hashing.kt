package org.tokend.wallet.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Wraps [MessageDigest] calls for hashing algorithms.
 */
object Hashing {
    /**
     * @return SHA-256 hash of the given data.
     */
    @JvmStatic
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