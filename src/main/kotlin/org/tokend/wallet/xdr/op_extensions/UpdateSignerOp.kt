package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.xdr.SetOptionsOp
import org.tokend.wallet.xdr.Signer

open class UpdateSignerOp : SetOptionsOp {
    constructor(signer: Signer) : super(
            masterWeight = null,
            medThreshold = null,
            lowThreshold = null,
            highThreshold = null,
            trustData = null,
            limitsUpdateRequestData = null,
            signer = signer,
            ext = SetOptionsOp.SetOptionsOpExt.EmptyVersion())

    @JvmOverloads
    constructor(accountID: String,
                weight: Int,
                type: Int,
                identity: Int,
                name: String = "") : this(Signer(PublicKeyFactory.fromAccountId(accountID),
            weight, type, identity, name, Signer.SignerExt.EmptyVersion()))
}