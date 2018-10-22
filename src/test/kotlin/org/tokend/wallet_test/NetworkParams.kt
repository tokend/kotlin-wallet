package org.tokend.wallet_test

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.NetworkParams
import java.math.BigDecimal

class NetworkParams {
    @Test
    fun amountConversion() {
        val precision = 6
        val amount = BigDecimal("1.234567")
        val networkParams = NetworkParams("Test phrase", precision)

        val precisedAmount = networkParams.amountToPrecised(amount)
        val dePrecisedAmount = networkParams.amountFromPrecised(precisedAmount)

        Assert.assertEquals(amount, dePrecisedAmount)
    }
}