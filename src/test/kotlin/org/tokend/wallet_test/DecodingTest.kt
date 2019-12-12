package org.tokend.wallet_test

import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.xdr.*
import org.tokend.wallet.xdr.utils.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DecodingTest {
    @Test
    fun aDecodeAllRequired() {
        val sourceRequest = CreateAssetOp(
                code = "OLE",
                securityType = 0,
                state = 0,
                maxIssuanceAmount = 1000000,
                trailingDigitsCount = 6,
                details = "{}",
                ext = CreateAssetOp.CreateAssetOpExt.EmptyVersion()
        )

        val source = Operation.OperationBody.CreateReviewableRequest(
                CreateReviewableRequestOp(
                        securityType = 0,
                        operations = arrayOf(
                                ReviewableRequestOperation.CreateAsset(sourceRequest)
                        ),
                        ext = EmptyExt.EmptyVersion()
                )
        )

        val decoded = Operation.OperationBody.fromBase64(source.toBase64())

        Assert.assertEquals(source.discriminant, decoded.discriminant)

        val decodedOp = (decoded as Operation.OperationBody.CreateReviewableRequest).createReviewableRequestOp

        Assert.assertEquals(source.createReviewableRequestOp.securityType, decodedOp.securityType)
        Assert.assertEquals(source.createReviewableRequestOp.operations.size, decodedOp.operations.size)

        val decodedRequest = (decodedOp.operations.first() as ReviewableRequestOperation.CreateAsset)
                .createAssetOp

        Assert.assertEquals(sourceRequest.code, decodedRequest.code)
        Assert.assertEquals(sourceRequest.maxIssuanceAmount, decodedRequest.maxIssuanceAmount)
        Assert.assertEquals(sourceRequest.ext.discriminant, decodedRequest.ext.discriminant)
    }

    @Test
    fun bDecodeWithOptionals() {
        val source = AccountEntry(
                accountID = PublicKeyFactory.fromAccountId(
                        "GDLWLDE33BN7SG6V4P63V2HFA56JYRMODESBLR2JJ5F3ITNQDUVKS2JE"
                ),
                roleIDs = arrayOf(1, 2, 3),
                referrer = null,
                sequentialID = 404,
                ext = AccountEntry.AccountEntryExt.EmptyVersion()
        )

        val decoded = AccountEntry.fromBase64(source.toBase64())

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
        val createdRuleId = 19L
        val result = "AAAAAAAAAAEAAAABAAAAAAAAAAAAAAAeAAAAAAAAABMAAAAAAAAABwAAAANPTEUAAAAAAAAAAAAAAAAAAAAAFAAAAAAAAAAAAAAAAAAAAAJ7fQAAAAAAAAAAAAA="
        val decoded = TransactionMeta.fromBase64(result)
        Assert.assertEquals(
                createdRuleId,
                decoded
                        .let { it as TransactionMeta.EmptyVersion }
                        .operations
                        .first()
                        .changes
                        .first()
                        .let { it as LedgerEntryChange.Created }
                        .created
                        .data
                        .let { it as LedgerEntry.LedgerEntryData.Rule }
                        .rule
                        .id
        )
    }
}