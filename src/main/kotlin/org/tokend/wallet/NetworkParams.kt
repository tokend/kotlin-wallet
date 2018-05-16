package org.tokend.wallet

import org.tokend.wallet.utils.Hashing
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Holds network-specific parameters.
 */
class NetworkParams {
    val passphrase: String
    val precision: Int
    val precisionMultiplier: Long
    val networkId: ByteArray

    /**
     * @param passphrase network passphrase
     * @param precision decimal places in amounts, [DEFAULT_PRECISION] by default
     */
    @JvmOverloads
    constructor(passphrase: String, precision: Int = DEFAULT_PRECISION) {
        this.passphrase = passphrase
        this.precision = precision
        this.precisionMultiplier = BigDecimal.TEN.pow(precision).longValueExact()
        this.networkId = Hashing.sha256(passphrase.toByteArray())
    }

    /**
     * Converts given amount to network format.
     */
    fun amountToPrecised(amount: BigDecimal): Long {
        return amount
                .multiply(BigDecimal.TEN.pow(precision))
                .setScale(0, RoundingMode.DOWN)
                .longValueExact()
    }

    /**
     * Converts given amount from network format to human-readable.
     */
    fun amountFromPrecised(amount: Long): BigDecimal {
        return BigDecimal(amount).divide(BigDecimal.TEN.pow(precision))
    }

    companion object {
        val DEFAULT_PRECISION = 6
    }
}