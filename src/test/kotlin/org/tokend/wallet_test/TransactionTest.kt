package org.tokend.wallet_test

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.Account
import org.tokend.wallet.NetworkParams
import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.Transaction
import org.tokend.wallet.xdr.*
import org.tokend.wallet.xdr.op_extensions.BindExternalAccountOp

class TransactionTest {
    val SOURCE_ACCOUNT_ID = "GDVJSBSBSERR3YP3LKLHTODWEFGCSLDWDIODER3CKLZXUMVPZOPT4MHY"
    val NETWORK = NetworkParams("Example Test Network")

    @Test
    fun encoding() {
        val sourceBalance = "BBVRUASMC2OMFGWHQPD4TTXTZZ7ACOFWWFTB5Y3K6757FSUSAEPEPXAS"
        val sourceAccountSeed = "SBEBZQIXHAZ3BZXOJEN6R57KMEDISGBIIP6LAVRCNDM4WZIQPHNYZICC".toCharArray()
        val destBalance = "BCN65IW4JYFLLJADTA5PNP2N27KPGDWUBD27UAHQITRTO3ADVST4WI3O"
        val account = Account.fromSecretSeed(sourceAccountSeed)

        val paymentOp = PaymentOp(
                PublicKeyFactory.fromBalanceId(sourceBalance),
                PublicKeyFactory.fromBalanceId(destBalance),
                1 * 1000000L,
                PaymentFeeData(
                        FeeData(0L, 0L, FeeData.FeeDataExt.EmptyVersion()),
                        FeeData(0L, 0L, FeeData.FeeDataExt.EmptyVersion()),
                        false,
                        PaymentFeeData.PaymentFeeDataExt.EmptyVersion()
                ),
                "Test",
                "",
                null,
                PaymentOp.PaymentOpExt.EmptyVersion()
        )

        val transaction = Transaction(
                NETWORK,
                PublicKeyFactory.fromAccountId(SOURCE_ACCOUNT_ID),
                listOf(Operation(null, Operation.OperationBody.Payment(paymentOp))),
                Memo.MemoText("Sample text"),
                TimeBounds(0L, 42L),
                0L,
                12345L
        )
        transaction.addSignature(account)

        val expectedEnvelope = "AAAAAOqZBkGRIx3h+1qWebh2IUwpLHYaHDJHYlLzejKvy58+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAqAAAAAQAAAAtTYW1wbGUgdGV4dAAAAAABAAAAAAAAAAEAAAAAaxoCTBacwprHg8fJzvPOfgE4trFmHuNq9/vyypIBHkcAAAAAm+6i3E4KtaQDmDr2v03X1PMO1Aj1+gDwROM3bAOsp8sAAAAAAA9CQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAARUZXN0AAAAAAAAAAAAAAAAAAAAMQAAAAAAADA5AAAAAeNBOKkAAABAjLsz2UoLaZ0MWTatsoAufNJcOO0Yy8/Mug9ByC+kwjeYKwvxnuBuveqAzcpbJdMqa6fN0thoW1nYCIfxpSwsDg=="
        val envelope = transaction.getEnvelope().toBase64()
        Assert.assertEquals(expectedEnvelope, envelope)
    }

    @Test
    fun noOperations() {
        try {
            Transaction(
                    NETWORK,
                    PublicKeyFactory.fromAccountId(SOURCE_ACCOUNT_ID),
                    emptyList())
            Assert.fail("Transactions with no operations can't be allowed")
        } catch (e: Exception) {
        }
    }

    @Test
    fun emptyVersionExt() {
        val transaction = Transaction(
                NETWORK,
                PublicKeyFactory.fromAccountId(SOURCE_ACCOUNT_ID),
                listOf(
                        Operation(null,
                                Operation.OperationBody.BindExternalSystemAccountId(BindExternalAccountOp(42)))
                ))

        Assert.assertEquals(org.tokend.wallet.xdr.Transaction.TransactionExt.EmptyVersion::class.java,
                transaction.getEnvelope().tx.ext.javaClass)
    }

    @Test
    fun feeExt() {
        val transaction = Transaction(
                NETWORK,
                PublicKeyFactory.fromAccountId(SOURCE_ACCOUNT_ID),
                listOf(
                        Operation(null,
                                Operation.OperationBody.BindExternalSystemAccountId(BindExternalAccountOp(42)))
                ),
                maxTotalFee = 1783)

        Assert.assertEquals(org.tokend.wallet.xdr.Transaction.TransactionExt.AddTransactionFee::class.java,
                transaction.getEnvelope().tx.ext.javaClass)
    }
}