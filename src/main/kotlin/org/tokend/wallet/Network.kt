package org.tokend.wallet

import org.tokend.wallet.utils.Hashing

class Network(val passphrase: String) {
    fun getId(): ByteArray = Hashing.sha256(passphrase.toByteArray())
}