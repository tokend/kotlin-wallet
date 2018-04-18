package org.tokend.wallet

import org.tokend.crypto.ecdsa.Curves
import org.tokend.crypto.ecdsa.EcDSAKeyPair

/**
 * Represents TokenD account defined by private and/or public keys.
 */
class Account(private val ecDSAKeyPair: EcDSAKeyPair) {
    /**
     * Returns the human readable account ID encoded in strkey.
     */
    val accountId: String = StrKey.encodeAccountId(ecDSAKeyPair.publicKeyBytes)

    /**
     * Returns the human readable secret seed encoded in strkey.
     */
    val secretSeed: String?
        get() = ecDSAKeyPair.privateKeySeed?.let { StrKey.encodeSecretSeed(it) }

    val publicKey: ByteArray = ecDSAKeyPair.publicKeyBytes

    /**
     * Returns [true] if this Account is capable of signing, [false] otherwise.
     */
    fun canSign(): Boolean = ecDSAKeyPair.canSign

    /**
     * Sign the provided data with the account's private key.
     */
    fun sign(data: ByteArray): ByteArray {
        return ecDSAKeyPair.sign(data)
    }

    /**
     * Verify the provided data and signature match this account's public key.
     */
    fun verifySignature(data: ByteArray, signature: ByteArray): Boolean {
        return ecDSAKeyPair.verify(data, signature)
    }

    companion object {
        private const val CURVE = Curves.ED25519_SHA512

        /**
         * Creates a new Account from a strkey encoded secret seed.
         * @param seed The strkey encoded secret seed.
         * @return [Account]
         */
        @JvmStatic
        fun fromSecretSeed(seed: String): Account {
            val decoded = StrKey.decodeSecretSeed(seed)
            val keypair = fromSecretSeed(decoded)
            return keypair
        }

        /**
         * Creates a new keypair from a raw 32 byte secret seed.
         * @param seed The 32 byte secret seed.
         * @return [Account]
         */
        @JvmStatic
        fun fromSecretSeed(seed: ByteArray): Account {
            return Account(EcDSAKeyPair.fromPrivateKeySeed(CURVE, seed))
        }

        /**
         * Creates a new Account from a strkey encoded account ID.
         * @param accountId The strkey encoded account ID.
         * @return [Account]
         */
        @JvmStatic
        fun fromAccountId(accountId: String): Account {
            val decoded = StrKey.decodeAccountId(accountId)
            return fromPublicKey(decoded)
        }

        /**
         * Creates a new Account from a 32 byte address.
         * @param publicKey The 32 byte public key.
         * @return [Account]
         */
        @JvmStatic
        fun fromPublicKey(publicKey: ByteArray): Account {
            return Account(EcDSAKeyPair.fromPublicKeyBytes(CURVE, publicKey))
        }

        /**
         * Generates a random Account.
         * @return a random Account.
         */
        @JvmStatic
        fun random(): Account {
            return Account(EcDSAKeyPair.random(CURVE))
        }
    }
}