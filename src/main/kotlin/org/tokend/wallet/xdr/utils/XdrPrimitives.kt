package org.tokend.wallet.xdr.utils

// Int32 and UInt32
fun Int.toXdr(stream: XdrDataOutputStream) {
    stream.writeInt(this)
}

// Int64 and UInt64
fun Long.toXdr(stream: XdrDataOutputStream) {
    stream.writeLong(this)
}

// String
fun String.toXdr(stream: XdrDataOutputStream) {
    stream.writeString(this)
}

// Bool
fun Boolean.toXdr(stream: XdrDataOutputStream) {
    stream.writeInt( if (this) 1 else 0)
}

// Opaque
fun ByteArray.toXdr(stream: XdrDataOutputStream) {
    this.size.toXdr(stream)
    stream.write(this)
}

/**
 * Fixed size opaque data
 */
abstract class XdrFixedByteArray: XdrEncodable {
    var wrapped: ByteArray
        set(value) {
            when {
                value.size == this.size ->
                    field = value
                value.size > this.size ->
                    // TODO: Throw exception
                    field = value.sliceArray(0..(this.size - 1))
                value.size < this.size -> {
                    field = ByteArray(this.size)
                    value.forEachIndexed { index, el ->
                        field[index] = el
                    }
                }
            }
        }
    /**
     * Size of specific fixed opaque type. Should be overridden in child classes
     */
    abstract val size: Int

    constructor(wrapped: ByteArray) {
        this.wrapped = wrapped
    }

    override fun toXdr(stream: XdrDataOutputStream) {
        stream.write(this.wrapped)
    }
}