package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.xdr.*

class SimplePaymentOp : PaymentOp {
    @JvmOverloads
    constructor(
            sourceBalanceId: BalanceID,
            destAccountId: AccountID,
            amount: Int64,
            feeData: PaymentFeeData,
            subject: String256 = "",
            reference: Longstring = ""
    ) : super(
            sourceBalanceId,
            PaymentOpDestination.Account(destAccountId),
            amount,
            feeData,
            subject,
            reference,
            PaymentOpExt.EmptyVersion())

    @JvmOverloads
    constructor(
            sourceBalanceId: String,
            destAccountId: String,
            amount: Long,
            feeData: PaymentFeeData,
            subject: String = "",
            reference: Longstring = ""
    ) : this(
            PublicKeyFactory.fromBalanceId(sourceBalanceId),
            PublicKeyFactory.fromAccountId(destAccountId),
            amount,
            feeData,
            subject,
            reference
    )
}