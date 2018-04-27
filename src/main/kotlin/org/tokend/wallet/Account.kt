package org.tokend.wallet

import org.tokend.crypto.ecdsa.Curves
import org.tokend.crypto.ecdsa.EcDSAKeyPair
import org.tokend.wallet.xdr.DecoratedSignature
import org.tokend.wallet.xdr.PublicKey
import org.tokend.wallet.xdr.SignatureHint
import org.tokend.wallet.xdr.Uint256
import org.tokend.wallet.xdr.utils.XdrDataOutputStream
import java.io.ByteArrayOutputStream
import java.util.*

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

    /**
     * Returns public key bytes.
     */
    val publicKey: ByteArray = ecDSAKeyPair.publicKeyBytes

    /**
     * Returns public key wrapped into XDR.
     */
    val xdrPublicKey: PublicKey
        get() = PublicKey.KeyTypeEd25519(Uint256(publicKey))

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

    /**
     * Sign the provided data with the account's private key
     * and wraps the signature into XDR.
     */
    fun signDecorated(data: ByteArray): DecoratedSignature {
        return DecoratedSignature(getSignatureHint(), sign(data))
    }

    private fun getSignatureHint(): SignatureHint {
        val publicKeyBytesStream = ByteArrayOutputStream()
        val xdrPublicKeyStream = XdrDataOutputStream(publicKeyBytesStream)
        xdrPublicKey.toXdr(xdrPublicKeyStream)

        val publicKeyBytes = publicKeyBytesStream.toByteArray()
        val signatureHintBytes = Arrays.copyOfRange(publicKeyBytes,
                publicKeyBytes.size - 4, publicKeyBytes.size)

        return SignatureHint(signatureHintBytes)
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
         * Creates a new Account from a raw 32 byte secret seed.
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
         * Creates a new Account from a 32 byte public key.
         * @param publicKey The 32 byte public key.
         * @return [Account]
         */
        @JvmStatic
        fun fromPublicKey(publicKey: ByteArray): Account {
            return Account(EcDSAKeyPair.fromPublicKeyBytes(CURVE, publicKey))
        }

        /**
         * Creates a new Account from an XDR-wrapped public key.
         * @param publicKey XDR-wrapped public key.
         * @return [Account]
         */
        @JvmStatic
        fun fromXdrPublicKey(publicKey: PublicKey.KeyTypeEd25519): Account {
            return Account.fromPublicKey(publicKey.ed25519.wrapped)
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