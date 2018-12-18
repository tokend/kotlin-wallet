package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.xdr.AccountType
import org.tokend.wallet.xdr.FeeType
import org.tokend.wallet.xdr.Int64

class CreateFeeOp
@JvmOverloads
constructor(
        type: FeeType,
        asset: String,
        fixed: Int64,
        percent: Int64,
        upperBound: Int64,
        lowerBound: Int64,
        subtype: Int64 = 0L,
        feeAsset: String? = null,
        accountId: String? = null,
        accountType: AccountType? = null
) : SimpleSetFeesOp(false, type, asset, fixed, percent,
        upperBound, lowerBound, subtype, feeAsset, accountId, accountType)