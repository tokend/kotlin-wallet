package org.tokend.wallet

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.xdr.*

class TransactionBuilderTest {
    val SOURCE_ACCOUNT_ID = "GDVJSBSBSERR3YP3LKLHTODWEFGCSLDWDIODER3CKLZXUMVPZOPT4MHY"
    val SOURCE_ACCOUNT_PUBKEY = PublicKeyFactory.fromAccountId(SOURCE_ACCOUNT_ID)
    val NETWORK = Network("Example Test Network")
    val SIMPLE_OP = ManageBalanceOp(
            ManageBalanceAction.CREATE,
            SOURCE_ACCOUNT_PUBKEY,
            "OLG",
            ManageBalanceOp.ManageBalanceOpExt.EmptyVersion()
    )

    @Test
    fun singleOperation() {
        val operationBody = Operation.OperationBody.ManageBalance(SIMPLE_OP)
        val transaction = TransactionBuilder(NETWORK, SOURCE_ACCOUNT_PUBKEY)
                .addOperation(operationBody)
                .build()

        Assert.assertEquals(SOURCE_ACCOUNT_PUBKEY, transaction.sourceAccountId)
        Assert.assertEquals(NETWORK.passphrase, transaction.network.passphrase)
        Assert.assertEquals(operationBody.toBase64(),
                transaction.operations[0].body.toBase64())
    }

    @Test
    fun setMemo() {
        val memoText = "TokenD is awesome"
        val transaction = TransactionBuilder(NETWORK, SOURCE_ACCOUNT_PUBKEY)
                .addOperation(Operation.OperationBody.ManageBalance(SIMPLE_OP))
                .setMemo(Memo.MemoText(memoText))
                .build()

        Assert.assertTrue(transaction.memo is Memo.MemoText)
        Assert.assertEquals(memoText, (transaction.memo as Memo.MemoText).text)
    }

    @Test
    fun setTimeBounds() {
        val timeBounds = TimeBounds(1, 5)
        val transaction = TransactionBuilder(NETWORK, SOURCE_ACCOUNT_PUBKEY)
                .addOperation(Operation.OperationBody.ManageBalance(SIMPLE_OP))
                .setTimeBounds(timeBounds)
                .build()

        Assert.assertEquals(timeBounds.maxTime, transaction.timeBounds.maxTime)
        Assert.assertEquals(timeBounds.minTime, transaction.timeBounds.minTime)
    }

    @Test
    fun setSalt() {
        val salt = 42L
        val transaction = TransactionBuilder(NETWORK, SOURCE_ACCOUNT_PUBKEY)
                .addOperation(Operation.OperationBody.ManageBalance(SIMPLE_OP))
                .setSalt(salt)
                .build()

        Assert.assertEquals(salt, transaction.salt)
    }
}