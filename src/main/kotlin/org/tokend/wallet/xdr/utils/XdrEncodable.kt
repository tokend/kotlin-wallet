package org.tokend.wallet.xdr.utils

import java.io.ByteArrayOutputStream

interface XdrEncodable {
    /**
     * Encodes object to xdr and writes it to specified XdrDataOutputStream
     */
    fun toXdr(stream: XdrDataOutputStream)

    /**
     * Returns base64 xdr representation of this object
     */
    fun toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        val xdrOutputStream = XdrDataOutputStream(outputStream)
        this.toXdr(xdrOutputStream)
        return outputStream.toByteArray().encodeBase64ToString()
    }
}