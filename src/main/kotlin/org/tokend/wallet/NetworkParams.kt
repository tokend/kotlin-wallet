package org.tokend.wallet

import org.tokend.wallet.utils.Hashing
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

/**
 * Holds network-specific parameters.
 */
class NetworkParams : Serializable {
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
     * @param precision decimal places in amounts, [DEFAULT_PRECISION] by default, [MAX_PRECISION] max
     * @param timeOffsetSeconds offset between device and server time in seconds, 0 by default
     */
    @JvmOverloads
    constructor(passphrase: String,
                precision: Int = DEFAULT_PRECISION,
                timeOffsetSeconds: Int = 0) {
        require(precision <= MAX_PRECISION) { "Precision can't be bigger than $MAX_PRECISION" }

        this.passphrase = passphrase
        this.precision = precision
        this.precisionMultiplier = BigDecimal.TEN.pow(precision).longValueExact()
        this.networkId = Hashing.sha256(passphrase.toByteArray())
        this.timeOffsetSeconds = timeOffsetSeconds
    }

    /**
     * Converts given amount to network format.
     *
     * @return UInt64 value in [Long], may result in negative Java value
     * if the precised amount is bigger than 9223372036854775807
     *
     * @see NetworkParams.precision
     */
    fun amountToPrecised(amount: BigDecimal): Long {
        require(amount.signum() >= 0) { "Amount can't be negative" }

        return amount
                .multiply(BigDecimal(precisionMultiplier))
                .setScale(0, RoundingMode.DOWN)
                .also { check(it <= MAX_PRECISED_AMOUNT) { "$it overflows UInt64" } }
                .toLong()
    }

    /**
     * Converts given amount from network format to human-readable.
     *
     * @param amount UInt64 value in [Long],
     * negative Java values are treated as bigger than 9223372036854775807
     */
    fun amountFromPrecised(amount: Long): BigDecimal {
        val amountBytes = ByteArray(Long.SIZE_BYTES).apply {
            for (byteI in 0 until size) {
                set(byteI, amount.ushr((size - 1 - byteI) * 8).toByte())
            }
        }

        return BigDecimal(BigInteger(1, amountBytes))
                .divide(BigDecimal(precisionMultiplier), MathContext.DECIMAL128)
    }

    companion object {
        private val MAX_PRECISED_AMOUNT = BigDecimal("18446744073709551615")

        const val serialVersionUID = 5677019745177892600L

        const val MAX_PRECISION = 6

        /**
         * Default amount precision in TokenD.
         */
        const val DEFAULT_PRECISION = MAX_PRECISION
    }
}