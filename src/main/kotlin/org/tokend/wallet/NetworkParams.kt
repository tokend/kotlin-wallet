package org.tokend.wallet

import org.tokend.wallet.utils.Hashing
import java.math.BigDecimal
import java.math.MathContext
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
     *
     * @see NetworkParams.precision
     */
    fun amountToPrecised(amount: BigDecimal): Long {
        return amount
                .multiply(BigDecimal(precisionMultiplier))
                .setScale(0, RoundingMode.DOWN)
                .longValueExact()
    }

    /**
     * Converts given amount from network format to human-readable.
     */
    fun amountFromPrecised(amount: Long): BigDecimal {
        return BigDecimal(amount)
                .divide(BigDecimal(precisionMultiplier), MathContext.DECIMAL128)
    }

    companion object {
        /**
         * Default amount precision in TokenD.
         */
        const val DEFAULT_PRECISION = 6
    }
}