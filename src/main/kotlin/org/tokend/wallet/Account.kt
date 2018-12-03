package org.tokend.wallet

import org.tokend.crypto.ecdsa.Curves
import org.tokend.crypto.ecdsa.EcDSAKeyPair
import org.tokend.crypto.ecdsa.SignUnavailableException
import org.tokend.crypto.ecdsa.erase
import org.tokend.wallet.xdr.DecoratedSignature
import org.tokend.wallet.xdr.PublicKey
import org.tokend.wallet.xdr.SignatureHint
import org.tokend.wallet.xdr.Uint256
import java.util.*
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
     * @return public key encoded by [Base32Check].
     *
     * @see <a href="https://tokend.gitbook.io/knowledge-base/technical-details/key-entities/accounts#account-id">AccountID in the Knowledge base</a>
     */
    val accountId: String = Base32Check.encodeAccountId(ecDSAKeyPair.publicKeyBytes)

    /**
     * @return private key seed encoded by [Base32Check].
     *
     * @see <a href="https://tokend.gitbook.io/knowledge-base/technical-details/key-entities/accounts#account-secret-seed">Secret seed in the Knowledge base</a>
     */
    val secretSeed: CharArray?
        get() = ecDSAKeyPair.privateKeySeed?.let { Base32Check.encodeSecretSeed(it) }

    /**
     * @return public key bytes.
     */
    val publicKey: ByteArray = ecDSAKeyPair.publicKeyBytes

    /**
     * @return public key wrapped into XDR.
     */
    val xdrPublicKey: PublicKey
        get() = PublicKey.KeyTypeEd25519(Uint256(publicKey))

    /**
     * @return [true] if this account is capable of signing, [false] otherwise.
     */
    fun canSign(): Boolean = ecDSAKeyPair.canSign

    /**
     * Signs provided data with the account's private key.
     *
     * @throws SignUnavailableException if account is not capable of signing.
     *
     * @see canSign
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
        val signatureHintBytes = Arrays.copyOfRange(publicKey,
                publicKey.size - 4, publicKey.size)

        return SignatureHint(signatureHintBytes)
    }

    override fun destroy() {
        ecDSAKeyPair.destroy()
    }

    override fun isDestroyed(): Boolean {
        return ecDSAKeyPair.isDestroyed
    }

    companion object {
        private const val CURVE = Curves.ED25519_SHA512

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
         * Creates an account from an account ID.
         * Created account can be used only for signature verification
         * as soon as it has no private key.
         *
         * @param accountId [Base32Check] encoded public key.
         *
         * @see Base32Check
         * @see Account.accountId
         * @see verifySignature
         */
        @JvmStatic
        fun fromAccountId(accountId: String): Account {
            val decoded = Base32Check.decodeAccountId(accountId)
            return fromPublicKey(decoded)
        }

        /**
         * Creates an account from a raw 32 byte public key.
         *
         * @param publicKey 32 bytes of the public key.
         *
         * @see Account.publicKey
         */
        @JvmStatic
        fun fromPublicKey(publicKey: ByteArray): Account {
            return Account(EcDSAKeyPair.fromPublicKeyBytes(CURVE, publicKey))
        }

        /**
         * Creates an account from an XDR-wrapped public key.
         *
         * @param publicKey XDR-wrapped public key.
         */
        @JvmStatic
        fun fromXdrPublicKey(publicKey: PublicKey.KeyTypeEd25519): Account {
            return Account.fromPublicKey(publicKey.ed25519.wrapped)
        }

        /**
         * Creates an account from a random private key.
         */
        @JvmStatic
        fun random(): Account {
            return Account(EcDSAKeyPair.random(CURVE))
        }
    }
}