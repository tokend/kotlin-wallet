package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.xdr.AccountType
import org.tokend.wallet.xdr.FeeType
import org.tokend.wallet.xdr.Int64

class DeleteFeeOp
@JvmOverloads
constructor(
        type: FeeType,
        asset: String,
        fixed: Int64,
        percent: Int64,
        upperBound: Int64,
        lowerBound: Int64,
        subtype: Int64 = 0L,
        accountId: String? = null,
        accountType: AccountType? = null
) : SimpleSetFeesOp(true, type, asset, fixed, percent,
        upperBound, lowerBound, subtype, accountId, accountType)