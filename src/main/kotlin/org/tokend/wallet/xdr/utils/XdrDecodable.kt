package org.tokend.wallet.xdr.utils

interface XdrDecodable<T> {
    /**
     * Decodes object of type [T] from XDR content of the [stream]
     */
    fun fromXdr(stream: XdrDataInputStream): T

    /**
     * Decodes object of type [T] from Base64-encoded XDR content
     */
    fun fromBase64(xdrBase64: String): T {
        return fromXdr(XdrDataInputStream.fromBase64(xdrBase64))
    }
}