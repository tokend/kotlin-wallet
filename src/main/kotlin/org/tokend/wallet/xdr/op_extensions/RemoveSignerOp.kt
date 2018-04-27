package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.xdr.AccountID
import org.tokend.wallet.xdr.Signer
import org.tokend.wallet.xdr.Uint32

class RemoveSignerOp : UpdateSignerOp {
    @JvmOverloads
    constructor(accountID: AccountID,
                identity: Uint32,
                type: Uint32 = 1) : super(Signer(accountID, 0, type, identity,
            "", Signer.SignerExt.EmptyVersion()))

    @JvmOverloads
    constructor(accountID: String,
                identity: Int,
                type: Int = 1) : super(accountID, 0, type, identity, "")
}