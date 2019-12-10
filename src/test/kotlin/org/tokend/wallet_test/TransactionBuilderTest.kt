package org.tokend.wallet_test

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.Account
import org.tokend.wallet.NetworkParams
import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.TransactionBuilder
import org.tokend.wallet.xdr.*

class TransactionBuilderTest {
    val SOURCE_ACCOUNT_ID = "GDVJSBSBSERR3YP3LKLHTODWEFGCSLDWDIODER3CKLZXUMVPZOPT4MHY"
    val SOURCE_ACCOUNT_PUBKEY = PublicKeyFactory.fromAccountId(SOURCE_ACCOUNT_ID)
    val NETWORK = NetworkParams("Example Test Network")
    val CREATE_ACCOUNT_OP = CreateAccountOp(
            destination = SOURCE_ACCOUNT_PUBKEY,
            referrer = null,
            roleIDs = arrayOf(1,2,3),
            signersData = arrayOf(SignerData(
                    publicKey = SOURCE_ACCOUNT_PUBKEY,
                    roleIDs = arrayOf(3,2,1),
                    weight = 255,
                    identity = 1,
                    details = "{}",
                    ext = EmptyExt.EmptyVersion()
            )),
            ext = CreateAccountOp.CreateAccountOpExt.EmptyVersion()
    )

    val CREATE_ASSET_OP = CreateAssetOp(
            code = "OLE",
            securityType = 0,
            state = 0,
            maxIssuanceAmount = 1000000,
            trailingDigitsCount = 6,
            details = "{}",
            ext = CreateAssetOp.CreateAssetOpExt.EmptyVersion()
    )

    @Test
    fun singleOperation() {
        val operationBody = Operation.OperationBody.CreateAccount(CREATE_ACCOUNT_OP)
        val transaction = TransactionBuilder(NETWORK, SOURCE_ACCOUNT_PUBKEY)
                .addOperation(operationBody)
                .build()

        Assert.assertEquals(SOURCE_ACCOUNT_PUBKEY, transaction.sourceAccountId)
        Assert.assertEquals(NETWORK.passphrase, transaction.networkParams.passphrase)
        Assert.assertEquals(operationBody.toBase64(),
                transaction.operations[0].body.toBase64())
    }

    @Test
    fun multipleOperations() {
        val operationBodies = listOf(
                Operation.OperationBody.CreateAccount(CREATE_ACCOUNT_OP),
                Operation.OperationBody.CreateAsset(CREATE_ASSET_OP)
        )

        val transaction = TransactionBuilder(NETWORK, SOURCE_ACCOUNT_PUBKEY)
                .addOperations(operationBodies)
                .build()

        Assert.assertEquals(SOURCE_ACCOUNT_PUBKEY, transaction.sourceAccountId)
        Assert.assertEquals(NETWORK.passphrase, transaction.networkParams.passphrase)
        Assert.assertEquals(operationBodies[0].toBase64(),
                transaction.operations[0].body.toBase64())
        Assert.assertEquals(operationBodies[1].toBase64(),
                transaction.operations[1].body.toBase64())
    }

    @Test
    fun setMemo() {
        val memoText = "TokenD is awesome"
        val transaction = TransactionBuilder(NETWORK, SOURCE_ACCOUNT_PUBKEY)
                .addOperation(Operation.OperationBody.CreateAccount(CREATE_ACCOUNT_OP))
                .setMemo(Memo.MemoText(memoText))
                .build()

        Assert.assertTrue(transaction.memo is Memo.MemoText)
        Assert.assertEquals(memoText, (transaction.memo as Memo.MemoText).text)
    }

    @Test
    fun setTimeBounds() {
        val timeBounds = TimeBounds(1, 5)
        val transaction = TransactionBuilder(NETWORK, SOURCE_ACCOUNT_PUBKEY)
                .addOperation(Operation.OperationBody.CreateAccount(CREATE_ACCOUNT_OP))
                .setTimeBounds(timeBounds)
                .build()

        Assert.assertEquals(timeBounds.maxTime, transaction.timeBounds.maxTime)
        Assert.assertEquals(timeBounds.minTime, transaction.timeBounds.minTime)
    }

    @Test
    fun setSalt() {
        val salt = 42L
        val transaction = TransactionBuilder(NETWORK, SOURCE_ACCOUNT_PUBKEY)
                .addOperation(Operation.OperationBody.CreateAccount(CREATE_ACCOUNT_OP))
                .setSalt(salt)
                .build()

        Assert.assertEquals(salt, transaction.salt)
    }

    @Test
    fun addSigner() {
        val signer = Account.random()
        val signatureHint = signer.signDecorated(byteArrayOf()).hint

        val transaction = TransactionBuilder(NETWORK, SOURCE_ACCOUNT_PUBKEY)
                .addOperation(Operation.OperationBody.CreateAccount(CREATE_ACCOUNT_OP))
                .addSigner(signer)
                .build()

        Assert.assertEquals(1, transaction.signatures.size)
        Assert.assertArrayEquals(signatureHint.wrapped, transaction.signatures[0].hint.wrapped)
    }
}