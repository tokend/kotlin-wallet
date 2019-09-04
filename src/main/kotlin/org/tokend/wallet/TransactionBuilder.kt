package org.tokend.wallet

import org.tokend.wallet.xdr.AccountID
import org.tokend.wallet.xdr.Memo
import org.tokend.wallet.xdr.Operation
import org.tokend.wallet.xdr.TimeBounds

/**
 * Builds a [Transaction] object.
 *
 * @param networkParams params of the network into which the transaction will be sent
 * @param sourceAccountId original account ID of the transaction initiator
 */
class TransactionBuilder(private val networkParams: NetworkParams,
                         private val sourceAccountId: AccountID) {
    private val operations = mutableListOf<Operation>()
    private var memo: Memo? = null
    private var timeBounds: TimeBounds? = null
    private var salt: Long? = null
    private val signers = mutableListOf<Account>()

    /**
     * @param networkParams params of the network into which the transaction will be sent
     * @param sourceAccountId original account ID of the transaction initiator
     */
    constructor(networkParams: NetworkParams,
                sourceAccountId: String) : this(networkParams, PublicKeyFactory.fromAccountId(sourceAccountId))

    /**
     * Adds operation with given body to the result transaction.
     *
     * @see Transaction.operations
     */
    @JvmOverloads
    fun addOperation(operationBody: Operation.OperationBody,
                     operationSourceAccount: AccountID? = null): TransactionBuilder {
        operations.add(Operation(operationSourceAccount, operationBody))
        return this
    }

    /**
     * Adds operations with given bodies to the result transaction.
     *
     * @see Transaction.operations
     */
    @JvmOverloads
    fun addOperations(operationBodies: Collection<Operation.OperationBody>,
                      operationsSourceAccount: AccountID? = null): TransactionBuilder {
        operations.addAll(operationBodies.map {
            Operation(operationsSourceAccount, it)
        })
        return this
    }

    /**
     * Sets memo of the result transaction.
     *
     * @see Transaction.memo
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
     *
     * @see Transaction.timeBounds
     */
    fun setTimeBounds(timeBounds: TimeBounds): TransactionBuilder {
        this.timeBounds = timeBounds
        return this
    }

    /**
     * Sets salt of the result transaction.
     * By default transaction salt is a random [Long].
     *
     * @see Transaction.salt
     */
    fun setSalt(salt: Long): TransactionBuilder {
        this.salt = salt
        return this
    }

    /**
     * Adds given account as a signer of the result transaction
     *
     * @see Transaction.addSignature
     */
    fun addSigner(signer: Account): TransactionBuilder {
        this.signers.add(signer)
        return this
    }

    /**
     * Builds the result transaction.
     * @throws IllegalStateException if no operations were added.
     */
    fun build(): Transaction {
        val transaction =
                Transaction(
                        networkParams,
                        sourceAccountId,
                        operations,
                        memo,
                        timeBounds,
                        salt
                )

        signers.forEach(transaction::addSignature)

        return transaction
    }
}