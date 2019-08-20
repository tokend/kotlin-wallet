package org.tokend.wallet_test

import org.apache.commons.codec.binary.Base64
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.tokend.wallet.Base32Check
import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.xdr.*
import org.tokend.wallet.xdr.utils.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DecodingTest {
    @Test
    fun aDecodeAllRequired() {
        val sourceRequest = UpdateMaxIssuance(
                assetCode = "OLE",
                maxIssuanceAmount = 5495,
                ext = UpdateMaxIssuance.UpdateMaxIssuanceExt.EmptyVersion()
        )

        val source = Operation.OperationBody.ManageAsset(
                ManageAssetOp(
                        requestID = 4020,
                        request = ManageAssetOp.ManageAssetOpRequest.UpdateMaxIssuance(
                                sourceRequest
                        ),
                        ext = ManageAssetOp.ManageAssetOpExt.EmptyVersion()
                )
        )

        val sourceOutputStream = ByteArrayOutputStream()
        source.toXdr(XdrDataOutputStream(sourceOutputStream))

        val sourceInputStream = XdrDataInputStream(ByteArrayInputStream(sourceOutputStream.toByteArray()))

        val decoded = ReflectiveXdrDecoder.read(Operation.OperationBody::class.java, sourceInputStream)

        Assert.assertEquals(source.discriminant, decoded.discriminant)

        val decodedOp = (decoded as Operation.OperationBody.ManageAsset).manageAssetOp

        Assert.assertEquals(source.manageAssetOp.requestID, decodedOp.requestID)
        Assert.assertEquals(source.manageAssetOp.request.discriminant, decodedOp.request.discriminant)

        val decodedRequest = (decodedOp.request as ManageAssetOp.ManageAssetOpRequest.UpdateMaxIssuance)
                .updateMaxIssuance

        Assert.assertEquals(sourceRequest.assetCode, decodedRequest.assetCode)
        Assert.assertEquals(sourceRequest.maxIssuanceAmount, decodedRequest.maxIssuanceAmount)
        Assert.assertEquals(sourceRequest.ext.discriminant, decodedRequest.ext.discriminant)
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

    @Test
    fun cPrimitives() {
        44.also { source ->
            Assert.assertEquals(source, ByteArrayOutputStream().let {
                source.toXdr(XdrDataOutputStream(it))
                Int.fromXdr(XdrDataInputStream(ByteArrayInputStream(it.toByteArray())))
            })
        }
        (-44).also { source ->
            Assert.assertEquals(source, ByteArrayOutputStream().let {
                source.toXdr(XdrDataOutputStream(it))
                Int.fromXdr(XdrDataInputStream(ByteArrayInputStream(it.toByteArray())))
            })
        }

        55L.also { source ->
            Assert.assertEquals(source, ByteArrayOutputStream().let {
                source.toXdr(XdrDataOutputStream(it))
                Long.fromXdr(XdrDataInputStream(ByteArrayInputStream(it.toByteArray())))
            })
        }
        (-55L).also { source ->
            Assert.assertEquals(source, ByteArrayOutputStream().let {
                source.toXdr(XdrDataOutputStream(it))
                Long.fromXdr(XdrDataInputStream(ByteArrayInputStream(it.toByteArray())))
            })
        }

        true.also { source ->
            Assert.assertEquals(source, ByteArrayOutputStream().let {
                source.toXdr(XdrDataOutputStream(it))
                Boolean.fromXdr(XdrDataInputStream(ByteArrayInputStream(it.toByteArray())))
            })
        }
        false.also { source ->
            Assert.assertEquals(source, ByteArrayOutputStream().let {
                source.toXdr(XdrDataOutputStream(it))
                Boolean.fromXdr(XdrDataInputStream(ByteArrayInputStream(it.toByteArray())))
            })
        }

        "TokenD is awesome!".also { source ->
            Assert.assertEquals(source, ByteArrayOutputStream().let {
                source.toXdr(XdrDataOutputStream(it))
                String.fromXdr(XdrDataInputStream(ByteArrayInputStream(it.toByteArray())))
            })
        }
        "".also { source ->
            Assert.assertEquals(source, ByteArrayOutputStream().let {
                source.toXdr(XdrDataOutputStream(it))
                String.fromXdr(XdrDataInputStream(ByteArrayInputStream(it.toByteArray())))
            })
        }

        byteArrayOf(1, 2, 3, 4).also { source ->
            Assert.assertArrayEquals(source, ByteArrayOutputStream().let {
                source.toXdr(XdrDataOutputStream(it))
                XdrOpaque.fromXdr(XdrDataInputStream(ByteArrayInputStream(it.toByteArray())))
            })
        }
        byteArrayOf().also { source ->
            Assert.assertArrayEquals(source, ByteArrayOutputStream().let {
                source.toXdr(XdrDataOutputStream(it))
                XdrOpaque.fromXdr(XdrDataInputStream(ByteArrayInputStream(it.toByteArray())))
            })
        }
    }

    @Test
    fun dTxResult() {
        val createdBalanceId = "BDGDRIG2WFR7HJESFI35WFUKS5XXEMIZIU44MBXVD3GXNRDXVLFDBGJW"
        val result = "AAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAJAAAAAAAAAADMOKDasWPzpJIqN9sWipdvcjEZRTnGBvUezXbEd6rKMAAAAAAAAAAA"
        val decoded = ReflectiveXdrDecoder.read(
                TransactionResult::class.java,
                XdrDataInputStream(ByteArrayInputStream(Base64().decode(result)))
        )
        Assert.assertEquals(
                createdBalanceId,
                decoded.result
                        .let { it as TransactionResult.TransactionResultResult.Txsuccess }
                        .results
                        .first()
                        .let { it as OperationResult.Opinner }
                        .tr
                        .let { it as OperationResult.OperationResultTr.ManageBalance }
                        .manageBalanceResult
                        .let { it as ManageBalanceResult.Success }
                        .success
                        .balanceID
                        .let { it as PublicKey.KeyTypeEd25519 }
                        .ed25519
                        .wrapped
                        .let(Base32Check::encodeBalanceId)
        )
    }
}