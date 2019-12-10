package org.tokend.wallet_test

import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.Account
import org.tokend.wallet.NetworkParams
import org.tokend.wallet.PublicKeyFactory
import org.tokend.wallet.Transaction
import org.tokend.wallet.utils.Base64
import org.tokend.wallet.xdr.*

class TransactionTest {
    val SOURCE_ACCOUNT_ID = "GDVJSBSBSERR3YP3LKLHTODWEFGCSLDWDIODER3CKLZXUMVPZOPT4MHY"
    val NETWORK = NetworkParams("Example Test Network")

    @Test
    fun encoding() {
        val transaction = getSampleTransaction()

        val expectedEnvelope = "AAAAAOqZBkGRIx3h+1qWebh2IUwpLHYaHDJHYlLzejKvy58+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAqAAAAAQAAAAtTYW1wbGUgdGV4dAAAAAABAAAAAAAAAAEAAAAAwzAYkmWPPTkSYZOvnD60A6LVpCIyvUohHoV268vyeNUAAAAAAAAAAwAAAAAAAAABAAAAAAAAAAIAAAAAAAAAAwAAAAEAAAAAwzAYkmWPPTkSYZOvnD60A6LVpCIyvUohHoV268vyeNUAAAADAAAAAAAAAAMAAAAAAAAAAgAAAAAAAAABAAAA/wAAAAEAAAACe30AAAAAAAAAAAAAAAAAAAAAAAHjQTipAAAAQFluUSfuxnOljkY7TfeDqArpGTnVEUPUaItGCi96SqYWQ7gKWPslikbVHVnNP/0L8CVS1SNRE9Ws/+TXeFruyAg="
        val envelope = transaction.getEnvelope().toBase64()
        Assert.assertEquals(expectedEnvelope, envelope)
    }

    @Test
    fun noOperations() {
        try {
            Transaction(
                    NETWORK,
                    PublicKeyFactory.fromAccountId(SOURCE_ACCOUNT_ID),
                    emptyList())
            Assert.fail("Transactions with no operations can't be allowed")
        } catch (e: Exception) {
        }
    }

    @Test
    fun hash() {
        val transaction = getSampleTransaction()

        val expectedHash = "KsYX2kY78KNbtoWjf/p9Tzlg3flR8b1+2C55+YFZ3Ps="
        val hash = Base64.encode(transaction.getHash()).toString(Charsets.UTF_8)
        Assert.assertEquals(expectedHash, hash)
    }

    private fun getSampleTransaction(): Transaction {
        val sourceAccountSeed = "SBEBZQIXHAZ3BZXOJEN6R57KMEDISGBIIP6LAVRCNDM4WZIQPHNYZICC".toCharArray()
        val destAccount = "GDBTAGESMWHT2OISMGJ27HB6WQB2FVNEEIZL2SRBD2CXN26L6J4NKDP2"
        val account = Account.fromSecretSeed(sourceAccountSeed)

        val createAccountOp = CreateAccountOp(
                destination = PublicKeyFactory.fromAccountId(destAccount),
                referrer = null,
                roleIDs = arrayOf(1,2,3),
                signersData = arrayOf(SignerData(
                        publicKey = PublicKeyFactory.fromAccountId(destAccount),
                        roleIDs = arrayOf(3,2,1),
                        weight = 255,
                        identity = 1,
                        details = "{}",
                        ext = EmptyExt.EmptyVersion()
                )),
                ext = CreateAccountOp.CreateAccountOpExt.EmptyVersion()
        )

        val transaction = Transaction(
                NETWORK,
                PublicKeyFactory.fromAccountId(SOURCE_ACCOUNT_ID),
                listOf(Operation(null, Operation.OperationBody.CreateAccount(createAccountOp))),
                Memo.MemoText("Sample text"),
                TimeBounds(0L, 42L),
                0L
        )
        transaction.addSignature(account)

        return transaction
    }
}