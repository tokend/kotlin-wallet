package org.tokend.wallet

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.xdr.*

class XdrModelsTests {
    private val ACCOUNT_ID = "GB5V4R2P6EPS7VDDTWATJKSS3F4FWRIPNRKCWFL6WSYIWBN3L6YH3A3J"
    private val BALANCE_ID = "BA7UXH23ZELVU6XZFEXMAE3J4QJTGG3F5ZPOV2BJ335CMGO6BHWRODQG"
    private val COUNTERPARTY_BALANCE_ID = "BCWR3EV2BW4DD32WYN7G2JNKDHP4KCTR2FCEHFA7ILZIU3MRLPDN7CPD"
    private val ASSET_CODE = "OLG"
    private val AMOUNT = 12345600L
    private val BYTES_ACCOUNT_ID: PublicKey
            = PublicKey.KeyTypeEd25519(Uint256(StrKey.decodeAccountId(ACCOUNT_ID)))
    private val BYTES_BALANCE_ID: PublicKey
            = PublicKey.KeyTypeEd25519(Uint256(StrKey.decodeBalanceId(BALANCE_ID)))
    private val BYTES_COUNTERPARTY_BALANCE_ID: PublicKey
            = PublicKey.KeyTypeEd25519(Uint256(StrKey.decodeBalanceId(COUNTERPARTY_BALANCE_ID)))

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
        val feeData = FeeData(AMOUNT, AMOUNT, FeeData.FeeDataExt.EmptyVersion())
        val paymentFeeData = PaymentFeeData(feeData, feeData, true,
                PaymentFeeData.PaymentFeeDataExt.EmptyVersion())
        val paymentOp = PaymentOp(BYTES_BALANCE_ID, BYTES_COUNTERPARTY_BALANCE_ID, AMOUNT,
                paymentFeeData, "", "", null,
                PaymentOp.PaymentOpExt.EmptyVersion())

        val op = Operation(null, Operation.OperationBody.Payment(paymentOp))

        Assert.assertEquals("AAAAAAAAAAEAAAAAP0ufW8kXWnr5KS7AE2nkEzMbZe5e6ugp3vomGd4J7RcAAAAArR2Sug24Me9Ww35tJaoZ38UKcdFEQ5QfQvKKbZFbxt8AAAAAALxhAAAAAAAAvGEAAAAAAAC8YQAAAAAAAAAAAAC8YQAAAAAAALxhAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
                op.toBase64())
    }
}