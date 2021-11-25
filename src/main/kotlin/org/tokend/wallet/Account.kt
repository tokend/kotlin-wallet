package org.tokend.wallet

import org.tokend.crypto.ecdsa.Curves
import org.tokend.crypto.ecdsa.EcDSAKeyPair
import org.tokend.crypto.ecdsa.erase
import org.tokend.wallet.xdr.DecoratedSignature
import org.tokend.wallet.xdr.PublicKey
import org.tokend.wallet.xdr.SignatureHint
import org.tokend.wallet.xdr.Uint256
import javax.security.auth.Destroyable

/**
 * Represents TokenD account defined by EcDSA private and/or public keys.
 * In this case account is a synonym of keypair.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Elliptic_Curve_Digital_Signature_Algorithm/">ECDSA</a>
 * @see <a href="https://ed25519.cr.yp.to/">Ed25519</a>
 */
class Account(private val ecDSAKeyPair: EcDSAKeyPair) : Destroyable {
    /**
     * @return private key seed encoded by [Base32Check].
     *
     * @see <a href="https://tokend.gitbook.io/knowledge-base/technical-details/key-entities/accounts#account-secret-seed">Secret seed in the Knowledge base</a>
     */
    val secretSeed: CharArray = ecDSAKeyPair.privateKeySeed.let(Base32Check::encodeSecretSeed)

    /**
     * @return public key bytes.
     */
    val publicKey: ByteArray = ecDSAKeyPair.publicKeyBytes

    /**
     * @return public key encoded by [Base32Check].
     *
     * @see <a href="https://tokend.gitbook.io/knowledge-base/technical-details/key-entities/accounts#account-id">AccountID in the Knowledge base</a>
     */
    val accountId: String = Base32Check.encodeAccountId(publicKey)

    /**
     * @return public key wrapped into XDR.
     */
    val xdrPublicKey: PublicKey
        get() = PublicKey.KeyTypeEd25519(Uint256(publicKey))

    /**
     * Signs provided data with the account's private key.
     */
    fun sign(data: ByteArray): ByteArray {
        return ecDSAKeyPair.sign(data)
    }

    /**
     * Verifies provided data and signature with the account's public key.
     */
    fun verifySignature(data: ByteArray, signature: ByteArray): Boolean {
        return ecDSAKeyPair.verify(data, signature)
    }

    /**
     * Signs provided data with the account's private key
     * and wraps the signature into XDR.
     *
     * @see sign
     */
    fun signDecorated(data: ByteArray): DecoratedSignature {
        return DecoratedSignature(getSignatureHint(), sign(data))
    }

    private fun getSignatureHint(): SignatureHint {
        val signatureHintBytes = publicKey.copyOfRange(publicKey.size - 4, publicKey.size)

        return SignatureHint(signatureHintBytes)
    }

    override fun destroy() {
        secretSeed.erase()
        ecDSAKeyPair.destroy()
    }

    override fun isDestroyed(): Boolean {
        return ecDSAKeyPair.isDestroyed
    }

    companion object {
        private const val CURVE = Curves.ED25519

        /**
         * Creates an account from a secret seed.
         *
         * @param seed [Base32Check] encoded private key seed. Will be decoded and duplicated
         * so can be erased after account creation.
         *
         * @see Base32Check
         * @see Account.secretSeed
         */
        @JvmStatic
        fun fromSecretSeed(seed: CharArray): Account {
            val decoded = Base32Check.decodeSecretSeed(seed)
            val keypair = fromSecretSeed(decoded)
            decoded.erase()
            return keypair
        }

        /**
         * Creates an account from a raw 32 byte secret seed.
         *
         * @param seed 32 bytes of the private key seed. Will be duplicated.
         */
        @JvmStatic
        fun fromSecretSeed(seed: ByteArray): Account {
            return Account(EcDSAKeyPair.fromPrivateKeySeed(CURVE, seed))
        }

        /**
         * Creates an account from a random private key.
         */
        @JvmStatic
        fun random(): Account {
            return Account(EcDSAKeyPair.random(CURVE))
        }

        /**
         * Verifies [signature] for provided [data] with public key decoded from [accountId]
         */
        @JvmStatic
        fun verifySignature(data: ByteArray,
                            signature: ByteArray,
                            accountId: String): Boolean =
                verifySignature(data, signature, PublicKeyFactory.fromAccountId(accountId))

        /**
         * Verifies [signature] for provided [data] with [publicKey]
         */
        @JvmStatic
        fun verifySignature(data: ByteArray,
                            signature: ByteArray,
                            publicKey: ByteArray): Boolean =
                verifySignature(data, signature, PublicKey.KeyTypeEd25519(Uint256(publicKey)))

        /**
         * Verifies [signature] for provided [data] with [xdrPublicKey]
         */
        @JvmStatic
        fun verifySignature(data: ByteArray,
                            signature: ByteArray,
                            xdrPublicKey: PublicKey): Boolean {
            require(xdrPublicKey is PublicKey.KeyTypeEd25519) {
                "Only Ed25519 keys are supported"
            }

            return EcDSAKeyPair.verify(CURVE, data, signature, xdrPublicKey.ed25519.wrapped)
        }
    }
}