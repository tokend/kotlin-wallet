package org.tokend.wallet.xdr.utils

// Int32 and UInt32
fun Int.toXdr(stream: XdrDataOutputStream) {
    stream.writeInt(this)
}

fun Int.Companion.fromXdr(stream: XdrDataInputStream): Int {
    return stream.readInt()
}

// Int64 and UInt64
fun Long.toXdr(stream: XdrDataOutputStream) {
    stream.writeLong(this)
}

fun Long.Companion.fromXdr(stream: XdrDataInputStream): Long {
    return stream.readLong()
}

// String
fun String.toXdr(stream: XdrDataOutputStream) {
    stream.writeString(this)
}

fun String.Companion.fromXdr(stream: XdrDataInputStream): String {
    return stream.readString()
}

// Bool
fun Boolean.toXdr(stream: XdrDataOutputStream) {
    stream.writeInt(if (this) 1 else 0)
}

fun Boolean.Companion.fromXdr(stream: XdrDataInputStream): Boolean {
    return stream.readInt() == 1
}

// Opaque
fun ByteArray.toXdr(stream: XdrDataOutputStream) {
    this.size.toXdr(stream)
    stream.write(this)
}

object XdrOpaque {
    @JvmStatic
    fun fromXdr(stream: XdrDataInputStream): ByteArray {
        val size = stream.readInt()
        val array = ByteArray(size)
        stream.read(array)
        return array
    }
}

/**
 * Fixed size opaque data
 */
abstract class XdrFixedByteArray : XdrEncodable {
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