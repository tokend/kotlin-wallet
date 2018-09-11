package org.tokend.wallet

import org.tokend.wallet.xdr.AccountID
import org.tokend.wallet.xdr.Memo
import org.tokend.wallet.xdr.Operation
import org.tokend.wallet.xdr.TimeBounds

/**
 * Builds a [Transaction] object.
 */
class TransactionBuilder(private val networkParams: NetworkParams,
                         private val sourceAccountId: AccountID) {
    private val operations = mutableListOf<Operation>()
    private var memo: Memo? = null
    private var timeBounds: TimeBounds? = null
    private var salt: Long? = null
    private var maxTotalFee: Long? = null

    /**
     * Adds operation with given body to the result transaction.
     */
    @JvmOverloads
    fun addOperation(operationBody: Operation.OperationBody,
                     operationSourceAccount: AccountID? = null): TransactionBuilder {
        operations.add(Operation(operationSourceAccount, operationBody))
        return this
    }

    /**
     * Sets memo of the result transaction.
     */
    fun setMemo(memo: Memo): TransactionBuilder {
        this.memo = memo
        return this
    }

    /**
     * Sets range of time during which the
     * result transaction will be valid.
     * Default transaction lifetime is [Transaction.DEFAULT_LIFETIME_SECONDS]
     * @param timeBounds time range in unixtime
     */
    fun setTimeBounds(timeBounds: TimeBounds): TransactionBuilder {
        this.timeBounds = timeBounds
        return this
    }

    /**
     * Sets salt of the result transaction.
     * By default transaction salt is a random [Long].
     */
    fun setSalt(salt: Long): TransactionBuilder {
        this.salt = salt
        return this
    }

    /**
     * Sets maximum fee of the result transaction.
     * By default transaction fee is not used.
     */
    fun setMaxTotalFee(fee: Long): TransactionBuilder {
        this.maxTotalFee = fee
        return this
    }

    /**
     * Builds the result transaction.
     * @throws IllegalStateException if no operations were added.
     */
    fun build(): Transaction {
        return Transaction(
                networkParams,
                sourceAccountId,
                operations,
                memo,
                timeBounds,
                salt,
                maxTotalFee
        )
    }
}