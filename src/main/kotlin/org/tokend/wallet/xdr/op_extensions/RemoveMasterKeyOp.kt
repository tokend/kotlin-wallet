package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.xdr.SetOptionsOp

class RemoveMasterKeyOp : SetOptionsOp(
        masterWeight = 0,
        medThreshold = null,
        lowThreshold = null,
        highThreshold = null,
        trustData = null,
        limitsUpdateRequestData = null,
        signer = null,
        ext = SetOptionsOp.SetOptionsOpExt.EmptyVersion())