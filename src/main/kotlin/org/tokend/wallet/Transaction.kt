package org.tokend.wallet

import org.tokend.wallet.utils.Hashing
import org.tokend.wallet.xdr.*
import org.tokend.wallet.xdr.Transaction
import org.tokend.wallet.xdr.utils.XdrDataOutputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.*

/**
 * Represents TokenD transaction - a set of operations that changes the state of the system.
 */
class Transaction {
    val networkParams: NetworkParams
    val sourceAccountId: AccountID
    val memo: Memo
    val operations: List<Operation>
    val timeBounds: TimeBounds
    val salt: Long

    private val mSignatures = mutableListOf<DecoratedSignature>()
    val signatures: List<DecoratedSignature>
        get() = mSignatures.toList()

    /**
     * Creates Transaction instance.
     *
     * @param networkParams network specification
     * @param sourceAccountId account ID of transaction initiator
     * @param memo optional transaction payload
     * @param timeBounds unixtime range during which the
     * transaction will be valid. Default transaction lifetime is [DEFAULT_LIFETIME_SECONDS]
     * @param salt optional unique value, random by default
     *
     * @throws IllegalAccessException if no operations were added.
     */
    @JvmOverloads
    constructor(networkParams: NetworkParams,
                sourceAccountId: AccountID,
                operations: List<Operation>,
                memo: Memo? = null,
                timeBounds: TimeBounds? = null,
                salt: Long? = null) {
        if (operations.isEmpty()) {
            throw IllegalStateException("Transaction must contain at least one operation")
        }

        this.networkParams = networkParams
        this.sourceAccountId = sourceAccountId
        this.operations = operations

        this.memo = memo ?: Memo.MemoNone()
        this.timeBounds = timeBounds ?:
                TimeBounds(0, Date().time / 1000 + DEFAULT_LIFETIME_SECONDS)
        this.salt = (salt ?: Random().nextLong()) and 0xffffffffL
    }

    /**
     * Adds signature from given signer to transaction signatures.
     */
    fun addSignature(signer: Account) {
        mSignatures.add(signer.signDecorated(getHash()))
    }

    /**
     * Returns SHA-256 hash of the transaction.
     */
    fun getHash(): ByteArray {
        return Hashing.sha256(getSignatureBase())
    }

    /**
     * Returns XDR-wrapped transaction with all signatures.
     */
    fun getEnvelope(): TransactionEnvelope {
        return TransactionEnvelope(getXdrTransaction(), mSignatures.toTypedArray())
    }

    private fun getSignatureBase(): ByteArray {
        val outputStream = ByteArrayOutputStream()

        outputStream.write(networkParams.networkId)
        outputStream.write(ByteBuffer.allocate(4).putInt(EnvelopeType.TX.value).array())

        val txOutputStream = ByteArrayOutputStream()
        val txXdrOutputStream = XdrDataOutputStream(txOutputStream)
        getXdrTransaction().toXdr(txXdrOutputStream)
        outputStream.write(txOutputStream.toByteArray())

        return outputStream.toByteArray()
    }

    private fun getXdrTransaction(): Transaction {
        return Transaction(
                sourceAccountId,
                salt,
                timeBounds,
                memo,
                operations.toTypedArray(),
                Transaction.TransactionExt.EmptyVersion()
        )
    }

    companion object {
        val DEFAULT_LIFETIME_SECONDS = 7 * 24 * 3600 - 3600L
    }
}