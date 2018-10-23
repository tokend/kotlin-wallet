package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.xdr.AccountID
import org.tokend.wallet.xdr.AssetCode
import org.tokend.wallet.xdr.ManageBalanceAction
import org.tokend.wallet.xdr.ManageBalanceOp

class DeleteBalanceOp(accountId: AccountID,
                      assetCode: AssetCode) : ManageBalanceOp(ManageBalanceAction.DELETE_BALANCE,
        accountId, assetCode, ManageBalanceOp.ManageBalanceOpExt.EmptyVersion()) {
    constructor(accountId: String,
                assetCode: String) : this(PublicKeyFactory.fromAccountId(accountId), assetCode)
}