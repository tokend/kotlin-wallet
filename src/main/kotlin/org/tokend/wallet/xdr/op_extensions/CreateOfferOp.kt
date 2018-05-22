package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.xdr.BalanceID
import org.tokend.wallet.xdr.Int64
import org.tokend.wallet.xdr.ManageOfferOp
import org.tokend.wallet.xdr.Uint64

class CreateOfferOp : ManageOfferOp {
    @JvmOverloads
    constructor(
            baseBalance: BalanceID,
            quoteBalance: BalanceID,
            baseAmount: Int64,
            price: Int64,
            fee: Int64,
            isBuy: Boolean,
            orderBookId: Uint64 = 0
    ) : super(baseBalance, quoteBalance, isBuy, baseAmount, price, fee,
            0L, orderBookId, ManageOfferOpExt.EmptyVersion())

    @JvmOverloads
    constructor(
            baseBalanceId: String,
            quoteBalanceId: String,
            baseAmount: Int64,
            price: Int64,
            fee: Int64,
            isBuy: Boolean,
            orderBookId: Uint64 = 0L
    ) : this(
            PublicKeyFactory.fromBalanceId(baseBalanceId),
            PublicKeyFactory.fromBalanceId(quoteBalanceId),
            baseAmount, price, fee, isBuy, orderBookId
    )
}