package org.tokend.wallet_test

import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.xdr.AccountEntry
import org.tokend.wallet.xdr.PublicKey
import org.tokend.wallet.xdr.SaleQuoteAsset
import org.tokend.wallet.xdr.utils.ReflectiveXdrDecoder
import org.tokend.wallet.xdr.utils.XdrDataInputStream
import org.tokend.wallet.xdr.utils.XdrDataOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DecodingTest {
    @Test
    fun aDecodeAllRequired() {
        val source = SaleQuoteAsset(
                quoteAsset = "OLE",
                price = 4020,
                quoteBalance = PublicKeyFactory.fromBalanceId(
                        "BCHZ2AUTIDCAU7VQM4357MMH3ZT5IHCMZL5DK2GGMUGTJVNISPR56AJF"
                ),
                currentCap = 5495,
                ext = SaleQuoteAsset.SaleQuoteAssetExt.EmptyVersion()
        )
        val sourceOutputStream = ByteArrayOutputStream()
        source.toXdr(XdrDataOutputStream(sourceOutputStream))

        val sourceInputStream = XdrDataInputStream(ByteArrayInputStream(sourceOutputStream.toByteArray()))

        val decoded = ReflectiveXdrDecoder.read(SaleQuoteAsset::class.java, sourceInputStream)

        Assert.assertEquals(source.quoteAsset, decoded.quoteAsset)
        Assert.assertEquals(source.price, decoded.price)
        Assert.assertEquals(source.currentCap, decoded.currentCap)

        Assert.assertEquals(source.ext.discriminant, decoded.ext.discriminant)

        Assert.assertEquals(source.quoteBalance.discriminant, decoded.quoteBalance.discriminant)
        Assert.assertArrayEquals(
                (source.quoteBalance as PublicKey.KeyTypeEd25519).ed25519.wrapped,
                (decoded.quoteBalance as PublicKey.KeyTypeEd25519).ed25519.wrapped
        )
    }

    @Test
    fun bDecodeWithOptionals() {
        val source = AccountEntry(
                accountID = PublicKeyFactory.fromAccountId(
                        "GDLWLDE33BN7SG6V4P63V2HFA56JYRMODESBLR2JJ5F3ITNQDUVKS2JE"
                ),
                roleID = 333,
                referrer = null,
                sequentialID = 404,
                ext = AccountEntry.AccountEntryExt.EmptyVersion()
        )

        val sourceOutputStream = ByteArrayOutputStream()
        source.toXdr(XdrDataOutputStream(sourceOutputStream))

        val sourceInputStream = XdrDataInputStream(ByteArrayInputStream(sourceOutputStream.toByteArray()))

        val decoded = ReflectiveXdrDecoder.read(AccountEntry::class.java, sourceInputStream)

        Assert.assertNull(decoded.referrer)
        Assert.assertEquals(source.sequentialID, decoded.sequentialID)
    }
}