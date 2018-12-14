package org.tokend.wallet_test

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.NetworkParams
import java.math.BigDecimal
import java.util.*

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

    @Test
    fun timeCorrection() {
        val correction = 60
        val networkParams = NetworkParams("Test phrase",
                NetworkParams.DEFAULT_PRECISION, correction)

        val actual = Date().time / 1000
        val calculated = networkParams.nowTimestamp

        Assert.assertTrue((calculated - actual) in (correction - 1..correction + 1))
    }
}