package org.tokend.wallet.xdr.utils

import org.tokend.wallet.utils.Base64
import java.io.ByteArrayInputStream

interface XdrDecodable<T> {
    /**
     * Decodes object of type [T] from XDR content of the [stream]
     */
    fun fromXdr(stream: XdrDataInputStream): T

    /**
     * Decodes object of type [T] from Base64-encoded XDR content
     */
    fun fromBase64(xdrBase64: String): T {
        return fromXdr(XdrDataInputStream(ByteArrayInputStream(Base64.decode(xdrBase64.toByteArray()))))
    }
}