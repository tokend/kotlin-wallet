package org.tokend.wallet_test

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.Account
import org.tokend.wallet.NetworkParams
import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.Transaction
import org.tokend.wallet.utils.Base64
import org.tokend.wallet.xdr.*
import org.tokend.wallet.xdr.op_extensions.SimplePaymentOp

class TransactionTest {
    private val sourceAccountId = "GDVJSBSBSERR3YP3LKLHTODWEFGCSLDWDIODER3CKLZXUMVPZOPT4MHY"
    private val network = NetworkParams("Example Test Network")

    private val sourceBalance = "BBVRUASMC2OMFGWHQPD4TTXTZZ7ACOFWWFTB5Y3K6757FSUSAEPEPXAS"
    private val destAccount = "GDBTAGESMWHT2OISMGJ27HB6WQB2FVNEEIZL2SRBD2CXN26L6J4NKDP2"
    private val sourceAccountSeed = "SBEBZQIXHAZ3BZXOJEN6R57KMEDISGBIIP6LAVRCNDM4WZIQPHNYZICC".toCharArray()
    private val account = Account.fromSecretSeed(sourceAccountSeed)

    private val paymentOp = SimplePaymentOp(
            sourceBalanceId = sourceBalance,
            destAccountId = destAccount,
            amount = 1 * 1000000L,
            feeData = PaymentFeeData(
                    Fee(0L, 0L, Fee.FeeExt.EmptyVersion()),
                    Fee(0L, 0L, Fee.FeeExt.EmptyVersion()),
                    false,
                    PaymentFeeData.PaymentFeeDataExt.EmptyVersion()
            ),
            subject = "Test"
    )

    private val sampleTransaction = Transaction(
            network,
            PublicKeyFactory.fromAccountId(sourceAccountId),
            listOf(Operation(null, Operation.OperationBody.Payment(paymentOp))),
            Memo.MemoText("Sample text"),
            TimeBounds(0L, 42L),
            0L
    ).apply { addSignature(account) }

    @Test
    fun encoding() {
        val transaction = sampleTransaction

        val expectedEnvelope = "AAAAAOqZBkGRIx3h+1qWebh2IUwpLHYaHDJHYlLzejKvy58+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAqAAAAAQAAAAtTYW1wbGUgdGV4dAAAAAABAAAAAAAAABcAAAAAaxoCTBacwprHg8fJzvPOfgE4trFmHuNq9/vyypIBHkcAAAAAAAAAAMMwGJJljz05EmGTr5w+tAOi1aQiMr1KIR6FduvL8njVAAAAAAAPQkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEVGVzdAAAAAAAAAAAAAAAAAAAAAHjQTipAAAAQF34oMJ+LK2Zu5FQIxbCsETYs5ELbzp4QsjS/5iu5rwxSiNtKBOGPwN43O57bMOEetTYdPWC+J2BKASM7eXVKQ0="
        val envelope = transaction.getEnvelope().toBase64()
        Assert.assertEquals(expectedEnvelope, envelope)
    }

    @Test
    fun noOperations() {
        try {
            Transaction(
                    network,
                    PublicKeyFactory.fromAccountId(sourceAccountId),
                    emptyList())
            Assert.fail("Transactions with no operations can't be allowed")
        } catch (e: Exception) {
        }
    }

    @Test
    fun hash() {
        val expectedHash = "TcNNk7QSlWHviChZnnuwUp5tE6BxXL2BhFpWD6/4k3M="
        val hash = Base64.encode(sampleTransaction.getHash()).toString(Charsets.UTF_8)
        Assert.assertEquals(expectedHash, hash)
    }

    @Test
    fun salt() {
        val salt = Long.MIN_VALUE + 1
        val transaction = Transaction(
                network,
                PublicKeyFactory.fromAccountId(sourceAccountId),
                listOf(Operation(null, Operation.OperationBody.Payment(paymentOp))),
                salt = salt
        )
        Assert.assertEquals("Absolute salt value must be used", Long.MAX_VALUE, transaction.salt)
    }

    @Test
    fun saltLongMinValue() {
        try {
            Transaction(
                    network,
                    PublicKeyFactory.fromAccountId(sourceAccountId),
                    listOf(Operation(null, Operation.OperationBody.Payment(paymentOp))),
                    salt = Long.MIN_VALUE
            )
        } catch (e: IllegalArgumentException) {
            // OK
            return
        }

        Assert.fail("Long.MIN_VALUE can't be used as a salt")
    }
}