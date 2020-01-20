package org.tokend.wallet

import org.tokend.wallet.utils.Hashing
import java.io.Serializable
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

/**
 * Holds network-specific parameters.
 */
class NetworkParams: Serializable {
    /**
     * Passphrase of the network
     */
    val passphrase: String

    /**
     * Decimal places in amounts. For example, 0.000001 in 6 precision is 1
     */
    val precision: Int

    /**
     * Multiplier for precised amount conversions
     *
     * @see precision
     */
    val precisionMultiplier: Long

    /**
     * Identifier of the network
     */
    val networkId: ByteArray

    /**
     * Offset between device and server time in seconds
     */
    val timeOffsetSeconds: Int

    /**
     * Calculated current time on server as a UNIX timestamp
     *
     * @see timeOffsetSeconds
     */
    val nowTimestamp: Long
        get() = (Date().time / 1000L) + timeOffsetSeconds

    /**
     * @param passphrase network passphrase
     * @param precision decimal places in amounts, [DEFAULT_PRECISION] by default
     * @param timeOffsetSeconds offset between device and server time in seconds, 0 by default
     */
    @JvmOverloads
    constructor(passphrase: String,
                precision: Int = DEFAULT_PRECISION,
                timeOffsetSeconds: Int = 0) {
        this.passphrase = passphrase
        this.precision = precision
        this.precisionMultiplier = BigDecimal.TEN.pow(precision).longValueExact()
        this.networkId = Hashing.sha256(passphrase.toByteArray())
        this.timeOffsetSeconds = timeOffsetSeconds
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
        const val serialVersionUID = 5677019745177892600L

        /**
         * Default amount precision in TokenD.
         */
        const val DEFAULT_PRECISION = 6
    }
}