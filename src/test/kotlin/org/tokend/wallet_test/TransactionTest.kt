package org.tokend.wallet_test

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.Account
import org.tokend.wallet.NetworkParams
import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.Transaction
import org.tokend.wallet.xdr.*
import org.tokend.wallet.xdr.op_extensions.SimplePaymentOpV2

class TransactionTest {
    val SOURCE_ACCOUNT_ID = "GDVJSBSBSERR3YP3LKLHTODWEFGCSLDWDIODER3CKLZXUMVPZOPT4MHY"
    val NETWORK = NetworkParams("Example Test Network")

    @Test
    fun encoding() {
        val sourceBalance = "BBVRUASMC2OMFGWHQPD4TTXTZZ7ACOFWWFTB5Y3K6757FSUSAEPEPXAS"
        val sourceAccountSeed = "SBEBZQIXHAZ3BZXOJEN6R57KMEDISGBIIP6LAVRCNDM4WZIQPHNYZICC".toCharArray()
        val destAccount = "GDBTAGESMWHT2OISMGJ27HB6WQB2FVNEEIZL2SRBD2CXN26L6J4NKDP2"
        val account = Account.fromSecretSeed(sourceAccountSeed)

        val paymentOp = SimplePaymentOpV2(
                sourceBalanceId = sourceBalance,
                destAccountId =destAccount,
                amount = 1 * 1000000L,
                feeData = PaymentFeeDataV2(
                        Fee(0L, 0L, Fee.FeeExt.EmptyVersion()),
                        Fee(0L, 0L, Fee.FeeExt.EmptyVersion()),
                        false,
                        PaymentFeeDataV2.PaymentFeeDataV2Ext.EmptyVersion()
                ),
                subject = "Test"
        )

        val transaction = Transaction(
                NETWORK,
                PublicKeyFactory.fromAccountId(SOURCE_ACCOUNT_ID),
                listOf(Operation(null, Operation.OperationBody.PaymentV2(paymentOp))),
                Memo.MemoText("Sample text"),
                TimeBounds(0L, 42L),
                0L
        )
        transaction.addSignature(account)

        val expectedEnvelope = "AAAAAOqZBkGRIx3h+1qWebh2IUwpLHYaHDJHYlLzejKvy58+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAqAAAAAQAAAAtTYW1wbGUgdGV4dAAAAAABAAAAAAAAABcAAAAAaxoCTBacwprHg8fJzvPOfgE4trFmHuNq9/vyypIBHkcAAAAAAAAAAMMwGJJljz05EmGTr5w+tAOi1aQiMr1KIR6FduvL8njVAAAAAAAPQkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEVGVzdAAAAAAAAAAAAAAAAAAAAAHjQTipAAAAQF34oMJ+LK2Zu5FQIxbCsETYs5ELbzp4QsjS/5iu5rwxSiNtKBOGPwN43O57bMOEetTYdPWC+J2BKASM7eXVKQ0="
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
}