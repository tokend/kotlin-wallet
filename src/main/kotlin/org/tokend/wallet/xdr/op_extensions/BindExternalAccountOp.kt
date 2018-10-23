package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.xdr.BindExternalSystemAccountIdOp

class BindExternalAccountOp(
        type: Int
) : BindExternalSystemAccountIdOp(
        type,
        BindExternalSystemAccountIdOpExt.EmptyVersion()
)