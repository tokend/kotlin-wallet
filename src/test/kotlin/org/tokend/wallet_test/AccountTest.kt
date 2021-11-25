package org.tokend.wallet_test

import com.google.common.io.BaseEncoding
import org.junit.Assert
import org.junit.Test
import org.tokend.wallet.Account
import org.tokend.wallet.Base32Check
import org.tokend.wallet.xdr.PublicKey

class AccountTest {
    private val SEED = "SBUFJEEK7FMWXPE4HGOWQZPHZ4V5TFKGSF664RAGT24NS662MKTQ7J6S"
    private val ACCOUNT_ID = "GB6ZRRKDAHUFQAGSJWMLCXL4W7OIEQNJL4NOISQUA6G23WK3OR3MGC4L"
    private val XDR_PUBLIC_KEY = "AAAAAH2YxUMB6FgA0k2YsV18t9yCQalfGuRKFAeNrdlbdHbD"
    private val DATA = "TokenD is awesome".toByteArray()

    @Test
    fun sign() {
        val expectedSig = "1B0EBBAE618B267668A8122ECCCD2A20480BC81951EB401E0F92B613483B798763D36AEB4B0404BC2A31FA1EAD47522BBA08705AB51BA205020E67D09AE87D0E"
        val account = Account.fromSecretSeed(SEED.toCharArray())
        val sig = account.sign(DATA)
        Assert.assertArrayEquals(BaseEncoding.base16().decode(expectedSig), sig)
    }

    @Test
    fun verifyValid() {
        val sig = "1B0EBBAE618B267668A8122ECCCD2A20480BC81951EB401E0F92B613483B798763D36AEB4B0404BC2A31FA1EAD47522BBA08705AB51BA205020E67D09AE87D0E"
        Assert.assertTrue(Account.verifySignature(DATA, BaseEncoding.base16().decode(sig), ACCOUNT_ID))
    }

    @Test
    fun verifyValidPublicKey() {
        val sig = "1B0EBBAE618B267668A8122ECCCD2A20480BC81951EB401E0F92B613483B798763D36AEB4B0404BC2A31FA1EAD47522BBA08705AB51BA205020E67D09AE87D0E"
        val publicKey = PublicKey.Decoder.fromBase64(XDR_PUBLIC_KEY)
        Assert.assertTrue(Account.verifySignature(DATA, BaseEncoding.base16().decode(sig), publicKey))
    }

    @Test
    fun verifyInvalid() {
        val account = Account.fromSecretSeed(SEED.toCharArray())
        Assert.assertFalse(account.verifySignature(ByteArray(0), ByteArray(0)))
    }

    @Test
    fun fromSeedString() {
        val account = Account.fromSecretSeed(SEED.toCharArray())
        Assert.assertEquals(SEED, String(account.secretSeed))
    }

    @Test
    fun fromSeedBytes() {
        val seed = (0 until 32).map { it.toByte() }.toByteArray()
        val account = Account.fromSecretSeed(seed)
        Assert.assertEquals(String(Base32Check.encodeSecretSeed(seed)), String(account.secretSeed))
    }

    @Test()
    fun random() {
        val first = Account.random()
        val second = Account.random()
        Assert.assertNotEquals(first.secretSeed, second.secretSeed)
    }

    @Test
    fun accountId() {
        val account = Account.fromSecretSeed(SEED.toCharArray())
        Assert.assertEquals(ACCOUNT_ID, account.accountId)
    }

    @Test
    fun xdrPublicKey() {
        val account = Account.fromSecretSeed(SEED.toCharArray())
        Assert.assertEquals(XDR_PUBLIC_KEY, account.xdrPublicKey.toBase64())
        Assert.assertEquals(ACCOUNT_ID, account.accountId)
        Assert.assertArrayEquals(Base32Check.decodeAccountId(ACCOUNT_ID), (account.xdrPublicKey as PublicKey.KeyTypeEd25519).ed25519.wrapped)
    }

    @Test
    fun signDecorated() {
        val account = Account.fromSecretSeed(SEED.toCharArray())
        val expectedSig = "W3R2wwAAAEAbDruuYYsmdmioEi7MzSogSAvIGVHrQB4PkrYTSDt5h2PTautLBAS8KjH6Hq1HUiu6CHBatRuiBQIOZ9Ca6H0O"
        val decoratedSignature = account.signDecorated(DATA)
        Assert.assertEquals(expectedSig, decoratedSignature.toBase64())
    }

    @Test
    fun destroy() {
        val account = Account.fromSecretSeed(SEED.toCharArray())
        account.destroy()
        Assert.assertTrue(account.isDestroyed)
        Assert.assertFalse(account.secretSeed.any { it != '0' })
    }

    @Test
    fun equals() {
        val accountA = Account.fromSecretSeed(SEED.toCharArray())
        val accountB = Account.fromSecretSeed(SEED.toCharArray())

        Assert.assertEquals(accountA, accountB)
        Assert.assertEquals(accountB, accountA)

        val accountC = Account.random()

        Assert.assertNotEquals(accountA, accountC)
        Assert.assertNotEquals(accountB, accountC)

        accountB.destroy()

        Assert.assertNotEquals(accountA, accountB)
        Assert.assertNotEquals(accountB, accountA)
    }

    @Test
    fun hashCodee() {
        val accountA = Account.fromSecretSeed(SEED.toCharArray())
        val accountB = Account.fromSecretSeed(SEED.toCharArray())

        Assert.assertEquals(accountA.hashCode(), accountB.hashCode())

        accountB.destroy()

        Assert.assertNotEquals(accountA.hashCode(), accountB.hashCode())
    }
}