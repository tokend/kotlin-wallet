package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.xdr.*

class SimplePaymentOpV2 : PaymentOpV2 {
    @JvmOverloads
    constructor(
            sourceBalanceId: BalanceID,
            destAccountId: AccountID,
            amount: Int64,
            feeData: PaymentFeeDataV2,
            subject: String256 = ""
    ) : super(
            sourceBalanceId,
            PaymentOpV2Destination.Account(destAccountId),
            amount,
            feeData,
            subject,
            "",
            PaymentOpV2Ext.EmptyVersion())

    @JvmOverloads
    constructor(
            sourceBalanceId: String,
            destAccountId: String,
            amount: Long,
            feeData: PaymentFeeDataV2,
            subject: String = ""
    ) : this(
            PublicKeyFactory.fromBalanceId(sourceBalanceId),
            PublicKeyFactory.fromAccountId(destAccountId),
            amount,
            feeData,
            subject
    )
}