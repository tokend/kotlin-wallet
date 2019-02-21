package org.tokend.wallet.xdr.op_extensions

import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.utils.Hashing
import org.tokend.wallet.xdr.*

open class SimpleSetFeesOp : SetFeesOp {
    @JvmOverloads
    constructor(
            isDelete: Boolean,
            type: FeeType,
            asset: String,
            fixed: Int64,
            percent: Int64,
            upperBound: Int64,
            lowerBound: Int64,
            subtype: Int64 = 0L,
            accountId: String? = null,
            accountRole: Uint64? = null

    ) : super(
            FeeEntry(
                    type,
                    asset,
                    fixed,
                    percent,
                    if (accountId != null)
                        PublicKeyFactory.fromAccountId(accountId)
                    else
                        null,
                    accountRole,
                    subtype,
                    lowerBound,
                    upperBound,
                    getHash(type, asset, subtype, accountId, accountRole),
                    FeeEntry.FeeEntryExt.EmptyVersion()
            ),
            isDelete,
            SetFeesOpExt.EmptyVersion()
    )

    companion object {
        @JvmStatic
        fun getHash(type: FeeType, asset: String, subtype: Int64,
                    accountId: String?, accountRole: Uint64?): Hash {
            var data =
                    "type:${type.value}asset:${asset}subtype:$subtype"

            if (accountId != null) {
                data += "accountID:$accountId"
            }

            if (accountRole != null) {
                data += "accountRole:$accountRole"
            }

            return Hash(
                    Hashing.sha256(data.toByteArray())
            )
        }
    }
}