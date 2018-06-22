package org.tokend.wallet.xdr.utils

import org.apache.commons.codec.binary.Base64
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
        val xdrBytes = outputStream.toByteArray()
        return String(Base64().encode(xdrBytes))
    }
}