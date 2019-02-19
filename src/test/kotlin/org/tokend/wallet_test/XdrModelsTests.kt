package org.tokend.wallet_test

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.Base32Check
import org.tokend.wallet.xdr.*
import org.tokend.wallet.xdr.op_extensions.SimplePaymentOpV2

class XdrModelsTests {
    private val ACCOUNT_ID = "GB5V4R2P6EPS7VDDTWATJKSS3F4FWRIPNRKCWFL6WSYIWBN3L6YH3A3J"
    private val BALANCE_ID = "BA7UXH23ZELVU6XZFEXMAE3J4QJTGG3F5ZPOV2BJ335CMGO6BHWRODQG"
    private val ASSET_CODE = "OLG"
    private val AMOUNT = 12345600L
    private val BYTES_ACCOUNT_ID: PublicKey = PublicKey.KeyTypeEd25519(Uint256(Base32Check.decodeAccountId(ACCOUNT_ID)))
    private val BYTES_BALANCE_ID: PublicKey = PublicKey.KeyTypeEd25519(Uint256(Base32Check.decodeBalanceId(BALANCE_ID)))

    @Test
    fun testCreateBalanceOp() {
        val manageBalanceOp = ManageBalanceOp(ManageBalanceAction.CREATE, BYTES_ACCOUNT_ID, ASSET_CODE,
                ManageBalanceOp.ManageBalanceOpExt.EmptyVersion())

        val op = Operation(null, Operation.OperationBody.ManageBalance(manageBalanceOp))

        Assert.assertEquals("AAAAAAAAAAkAAAAAAAAAAHteR0/xHy/UY52BNKpS2XhbRQ9sVCsVfrSwiwW7X7B9AAAAA09MRwAAAAAA",
                op.toBase64())
    }

    @Test
    fun testDeleteBalanceOp() {
        val manageBalanceOp = ManageBalanceOp(ManageBalanceAction.DELETE_BALANCE, BYTES_ACCOUNT_ID, ASSET_CODE,
                ManageBalanceOp.ManageBalanceOpExt.EmptyVersion())

        val op = Operation(null, Operation.OperationBody.ManageBalance(manageBalanceOp))

        Assert.assertEquals("AAAAAAAAAAkAAAABAAAAAHteR0/xHy/UY52BNKpS2XhbRQ9sVCsVfrSwiwW7X7B9AAAAA09MRwAAAAAA",
                op.toBase64())
    }

    @Test
    fun testPaymentOp() {
        val paymentOp = SimplePaymentOpV2(
                sourceBalanceId = BYTES_BALANCE_ID,
                destAccountId = BYTES_ACCOUNT_ID,
                amount = AMOUNT,
                feeData = PaymentFeeDataV2(
                        Fee(0L, 0L, Fee.FeeExt.EmptyVersion()),
                        Fee(0L, 0L, Fee.FeeExt.EmptyVersion()),
                        false,
                        PaymentFeeDataV2.PaymentFeeDataV2Ext.EmptyVersion()
                ),
                subject = "Test"
        )

        val op = Operation(null, Operation.OperationBody.PaymentV2(paymentOp))

        Assert.assertEquals("AAAAAAAAABcAAAAAP0ufW8kXWnr5KS7AE2nkEzMbZe5e6ugp3vomGd4J7RcAAAAAAAAAAHteR0/xHy/UY52BNKpS2XhbRQ9sVCsVfrSwiwW7X7B9AAAAAAC8YQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEVGVzdAAAAAAAAAAA",
                op.toBase64())
    }
}