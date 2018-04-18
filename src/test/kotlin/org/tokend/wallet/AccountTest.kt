package org.tokend.wallet

import com.google.common.io.BaseEncoding
import org.junit.Assert
import org.junit.Test

class AccountTest {
    private val SEED = "SBUFJEEK7FMWXPE4HGOWQZPHZ4V5TFKGSF664RAGT24NS662MKTQ7J6S"
    private val ACCOUNT_ID = "GB6ZRRKDAHUFQAGSJWMLCXL4W7OIEQNJL4NOISQUA6G23WK3OR3MGC4L"

    @Test
    fun sign() {
        val expectedSig = "1B0EBBAE618B267668A8122ECCCD2A20480BC81951EB401E0F92B613483B798763D36AEB4B0404BC2A31FA1EAD47522BBA08705AB51BA205020E67D09AE87D0E"
        val account = Account.fromSecretSeed(SEED)
        val data = "TokenD is awesome"
        val sig = account.sign(data.toByteArray())
        Assert.assertArrayEquals(BaseEncoding.base16().decode(expectedSig), sig)
    }

    @Test
    fun verifyValid() {
        val sig = "1B0EBBAE618B267668A8122ECCCD2A20480BC81951EB401E0F92B613483B798763D36AEB4B0404BC2A31FA1EAD47522BBA08705AB51BA205020E67D09AE87D0E"
        val account = Account.fromSecretSeed(SEED)
        val data = "TokenD is awesome"
        Assert.assertTrue(account.verifySignature(data.toByteArray(), BaseEncoding.base16().decode(sig)))
    }

    @Test
    fun verifyInvalid() {
        val account = Account.fromSecretSeed(SEED)
        Assert.assertFalse(account.verifySignature(ByteArray(0), ByteArray(0)))
    }

    @Test
    fun fromSeedString() {
        val account = Account.fromSecretSeed(SEED)
        Assert.assertEquals(SEED, account.secretSeed)
    }

    @Test
    fun fromSeedBytes() {
        val seed = (0 until 32).map { it.toByte() }.toByteArray()
        val account = Account.fromSecretSeed(seed)
        Assert.assertEquals(StrKey.encodeSecretSeed(seed), account.secretSeed)
    }

    @Test()
    fun random() {
        val first = Account.random()
        val second = Account.random()
        Assert.assertNotEquals(first.secretSeed, second.secretSeed)
    }

    @Test
    fun accountId() {
        val account = Account.fromSecretSeed(SEED)
        Assert.assertEquals(ACCOUNT_ID, account.accountId)
    }
}