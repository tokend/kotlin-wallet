package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.xdr.ManageOfferOp
import org.tokend.wallet.xdr.PublicKey
import org.tokend.wallet.xdr.Uint64
import org.tokend.wallet.xdr.XdrByteArrayFixed32

class CancelOfferOp : ManageOfferOp {
    @JvmOverloads
    constructor(offerId: Uint64, isBuy: Boolean,
                orderBookId: Uint64 = 0
    ) : super(PublicKey.KeyTypeEd25519(XdrByteArrayFixed32(ByteArray(32))),
            PublicKey.KeyTypeEd25519(XdrByteArrayFixed32(ByteArray(32))),
            isBuy, 0, 0, 0,
            offerId, orderBookId, ManageOfferOpExt.EmptyVersion())
}