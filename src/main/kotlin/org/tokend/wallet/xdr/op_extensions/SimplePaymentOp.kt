package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.xdr.*

class SimplePaymentOp : PaymentOp {
    @JvmOverloads
    constructor(
            sourceBalanceID: BalanceID,
            destinationBalanceID: BalanceID,
            amount: Int64,
            feeData: PaymentFeeData,
            subject: String256 = "") : super(sourceBalanceID,
            destinationBalanceID, amount, feeData, subject, "", null,
            PaymentOp.PaymentOpExt.EmptyVersion())

    @JvmOverloads
    constructor(sourceBalanceID: String,
                destinationBalanceID: String,
                amount: Long,
                feeData: PaymentFeeData,
                subject: String = "") : this(PublicKeyFactory.fromBalanceId(sourceBalanceID),
            PublicKeyFactory.fromBalanceId(destinationBalanceID), amount, feeData, subject)
}