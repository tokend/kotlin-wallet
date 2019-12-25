package org.tokend.wallet

import org.tokend.wallet.utils.Hashing
import org.tokend.wallet.xdr.*
import org.tokend.wallet.xdr.Transaction
import org.tokend.wallet.xdr.utils.XdrDataOutputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.absoluteValue

/**
 * Represents TokenD transaction - a set of operations that changes the state of the system.
 *
 * @see <a href="https://tokend.gitbook.io/knowledge-base/technical-details/key-entities/transaction">Transaction in the Knowledge base</a>
 */
class Transaction {
    /**
     * Params of the network into which the transaction will be sent.
     */
    val networkParams: NetworkParams

    /**
     * Original account ID of the transaction initiator.
     *
     * @see <a href="https://tokend.gitbook.io/knowledge-base/technical-details/key-entities/transaction#source-account">Source account ID in the Knowledge base</a>
     */
    val sourceAccountId: AccountID

    /**
     * Optional transaction payload.
     *
     * @see <a href="https://tokend.gitbook.io/knowledge-base/technical-details/key-entities/transaction#memo">Memo in the Knowledge base</a>
     */
    val memo: Memo

    /**
     * List of operations performed by this transaction.
     *
     * @see <a href="https://tokend.gitbook.io/knowledge-base/technical-details/operations">Operations overview in the Knowledge base</a>
     */
    val operations: List<Operation>

    /**
     * Time range during which the transaction will be valid.
     *
     * @see <a href="https://tokend.gitbook.io/knowledge-base/technical-details/key-entities/transaction#time-bounds">Time bounds in the Knowledge base</a>
     */
    val timeBounds: TimeBounds

    /**
     * Any number that ensures the uniqueness of the transaction.
     */
    val salt: Long

    private val mSignatures = mutableListOf<DecoratedSignature>()

    /**
     * List of signatures for the transaction.
     */
    val signatures: List<DecoratedSignature>
        get() = mSignatures.toList()

    /**
     * Creates unsigned TokenD transaction.
     *
     * @param networkParams network specification
     * @param sourceAccountId account ID of transaction initiator
     * @param memo optional transaction payload
     * @param timeBounds time range during which the
     * transaction will be valid. Default transaction lifetime is [DEFAULT_LIFETIME_SECONDS]
     * @param salt optional unique value, random by default, absolute value is taken
     *
     *  @throws IllegalStateException if no operations were added.
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
        this.timeBounds = timeBounds
                ?: TimeBounds(0, networkParams.nowTimestamp + DEFAULT_LIFETIME_SECONDS)
        this.salt = (salt?.also { require(it != Long.MIN_VALUE) { "Can't use $it as a salt" } }
                        ?: (Random().nextLong() and 0x7fffffffffffffff))
                .absoluteValue
    }

    /**
     * Creates a copy of given XDR-wrapped transaction.
     *
     * @param networkParams network specification
     * @param transactionEnvelope XDR-wrapped transaction with signatures
     */
    constructor(networkParams: NetworkParams,
                transactionEnvelope: TransactionEnvelope) {
        this.networkParams = networkParams
        this.sourceAccountId = transactionEnvelope.tx.sourceAccount
        this.operations = transactionEnvelope.tx.operations.toList()
        this.memo = transactionEnvelope.tx.memo
        this.timeBounds = transactionEnvelope.tx.timeBounds
        this.salt = transactionEnvelope.tx.salt
        this.mSignatures.addAll(transactionEnvelope.signatures)
    }

    /**
     * Adds signature from given signer to transaction signatures.
     */
    fun addSignature(signer: Account) {
        addSignature(getSignature(signer, networkParams.networkId, getXdrTransaction()))
    }

    /**
     * Adds given signature to transaction signatures.
     */
    fun addSignature(decoratedSignature: DecoratedSignature) {
        mSignatures.add(decoratedSignature)
    }

    /**
     * @return SHA-256 hash of the transaction.
     */
    fun getHash(): ByteArray {
        return Companion.getHash(getSignatureBase(networkParams.networkId, getXdrTransaction()))
    }

    /**
     * @return XDR-wrapped transaction with all signatures.
     */
    fun getEnvelope(): TransactionEnvelope {
        return TransactionEnvelope(getXdrTransaction(), mSignatures.toTypedArray())
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
        const val DEFAULT_LIFETIME_SECONDS = 7 * 24 * 3600 - 3600L

        /**
         * @return [DecoratedSignature] for given transaction by [signer]
         */
        @JvmStatic
        fun getSignature(signer: Account,
                         networkId: ByteArray,
                         xdrTransaction: Transaction): DecoratedSignature {
            return signer.signDecorated(getHash(getSignatureBase(networkId, xdrTransaction)))
        }

        /**
         * @return SHA-256 hash of given transaction signature base
         *
         * @see getSignatureBase
         */
        @JvmStatic
        fun getHash(signatureBase: ByteArray): ByteArray {
            return Hashing.sha256(signatureBase)
        }

        /**
         * @return base content for transaction signature
         *
         * @see NetworkParams.networkId
         */
        @JvmStatic
        fun getSignatureBase(networkId: ByteArray,
                             xdrTransaction: Transaction): ByteArray {
            val outputStream = ByteArrayOutputStream()

            outputStream.write(networkId)
            outputStream.write(ByteBuffer.allocate(4).putInt(EnvelopeType.TX.value).array())

            val txOutputStream = ByteArrayOutputStream()
            val txXdrOutputStream = XdrDataOutputStream(txOutputStream)
            xdrTransaction.toXdr(txXdrOutputStream)
            outputStream.write(txOutputStream.toByteArray())

            return outputStream.toByteArray()
        }
    }
}