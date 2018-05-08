package org.tokend.wallet

import org.tokend.wallet.xdr.AccountID
import org.tokend.wallet.xdr.BalanceID
import org.tokend.wallet.xdr.PublicKey
import org.tokend.wallet.xdr.Uint256

object PublicKeyFactory {
    @JvmStatic
    fun fromBalanceId(balanceId: String): BalanceID {
        return PublicKey.KeyTypeEd25519(Uint256(Base32Check.decodeBalanceId(balanceId)))
    }

    @JvmStatic
    fun fromAccountId(accountId: String): AccountID {
        return PublicKey.KeyTypeEd25519(Uint256(Base32Check.decodeAccountId(accountId)))
    }
}