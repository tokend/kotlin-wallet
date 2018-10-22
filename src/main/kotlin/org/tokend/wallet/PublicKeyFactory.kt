package org.tokend.wallet

import org.tokend.wallet.xdr.AccountID
import org.tokend.wallet.xdr.BalanceID
import org.tokend.wallet.xdr.PublicKey
import org.tokend.wallet.xdr.Uint256

/**
 * Holds method to create XDR public keys from
 * [Base32Check] encoded string representations.
 */
object PublicKeyFactory {
    /**
     * Creates [PublicKey] from [Base32Check] encoded balance ID.
     */
    @JvmStatic
    fun fromBalanceId(balanceId: String): BalanceID {
        return PublicKey.KeyTypeEd25519(Uint256(Base32Check.decodeBalanceId(balanceId)))
    }

    /**
     * Creates [PublicKey] from [Base32Check] encoded account ID.
     */
    @JvmStatic
    fun fromAccountId(accountId: String): AccountID {
        return PublicKey.KeyTypeEd25519(Uint256(Base32Check.decodeAccountId(accountId)))
    }
}