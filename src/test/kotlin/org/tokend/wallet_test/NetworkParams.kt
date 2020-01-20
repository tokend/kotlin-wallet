package org.tokend.wallet_test

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.NetworkParams
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
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

    @Test
    fun serialization() {
        val networkParams = NetworkParams("Test phrase", 10, 42)
        val byteArrayOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteArrayOutputStream).writeObject(networkParams)
        val inputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
        val pn = ObjectInputStream(inputStream).readObject() as NetworkParams

        Assert.assertEquals(networkParams.passphrase, pn.passphrase)
        Assert.assertEquals(networkParams.precision, pn.precision)
        Assert.assertEquals(networkParams.timeOffsetSeconds, pn.timeOffsetSeconds)
        Assert.assertArrayEquals(networkParams.networkId, pn.networkId)
    }
}