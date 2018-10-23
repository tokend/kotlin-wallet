package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.xdr.*

class RejectInvoiceOp : PaymentOp {
    constructor(
            sourceBalanceID: BalanceID,
            destinationBalanceID: BalanceID,
            feeData: PaymentFeeData,
            invoiceId: Uint64) : super(sourceBalanceID,
            destinationBalanceID, 0, feeData, "", "",
            InvoiceReference(invoiceId, false, InvoiceReference.InvoiceReferenceExt.EmptyVersion()),
            PaymentOp.PaymentOpExt.EmptyVersion())

    constructor(
            sourceBalanceID: String,
            destinationBalanceID: String,
            feeData: PaymentFeeData,
            invoiceId: Uint64
    ) : this(PublicKeyFactory.fromBalanceId(sourceBalanceID),
            PublicKeyFactory.fromBalanceId(destinationBalanceID), feeData, invoiceId)
}