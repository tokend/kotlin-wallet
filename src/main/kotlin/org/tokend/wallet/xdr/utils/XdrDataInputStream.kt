package org.tokend.wallet.xdr.utils

import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream

class XdrDataInputStream
/**
 * Creates a XdrDataInputStream that uses the specified
 * underlying InputStream.
 *
 * @param in the specified input stream
 */
(`in`: InputStream) : DataInputStream(XdrInputStream(`in`)) {

    // The underlying input stream
    private val mIn: XdrInputStream

    // The total bytes read so far.
    private val mCount: Int

    init {
        mIn = super.`in` as XdrInputStream
        mCount = 0
    }

    @Throws(IOException::class)
    fun readString(): String {
        val l = readInt()
        val bytes = ByteArray(l)
        readFully(bytes)
        pad()
        return String(bytes, Charsets.UTF_8)
    }

    @Throws(IOException::class)
    fun readIntArray(): IntArray {
        val l = readInt()
        return readIntArray(l)
    }

    @Throws(IOException::class)
    fun readIntArray(l: Int): IntArray {
        val arr = IntArray(l)
        for (i in 0 until l) {
            arr[i] = readInt()
        }
        return arr
    }

    @Throws(IOException::class)
    fun readFloatArray(): FloatArray {
        val l = readInt()
        return readFloatArray(l)
    }

    @Throws(IOException::class)
    fun readFloatArray(l: Int): FloatArray {
        val arr = FloatArray(l)
        for (i in 0 until l) {
            arr[i] = readFloat()
        }
        return arr
    }

    @Throws(IOException::class)
    fun readDoubleArray(): DoubleArray {
        val l = readInt()
        return readDoubleArray(l)
    }

    @Throws(IOException::class)
    fun readDoubleArray(l: Int): DoubleArray {
        val arr = DoubleArray(l)
        for (i in 0 until l) {
            arr[i] = readDouble()
        }
        return arr
    }

    /**
     * Skips ahead to bring the stream to 4 byte alignment.
     */
    @Throws(IOException::class)
    fun pad() {
        mIn.pad()
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return super.read()
    }

    /**
     * Need to provide a custom impl of InputStream as DataInputStream's read methods
     * are final and we need to keep track of the count for padding purposes.
     */
    private class XdrInputStream(// The underlying input stream
            private val mIn: InputStream) : InputStream() {

        // The amount of bytes read so far.
        private var mCount: Int = 0

        init {
            mCount = 0
        }

        @Throws(IOException::class)
        override fun read(): Int {
            val read = mIn.read()
            if (read >= 0) {
                mCount++
            }
            return read
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray): Int {
            return read(b, 0, b.size)
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray, off: Int, len: Int): Int {
            val read = mIn.read(b, off, len)
            mCount += read
            return read
        }

        @Throws(IOException::class)
        fun pad() {
            var pad = 0
            val mod = mCount % 4
            if (mod > 0) {
                pad = 4 - mod
            }
            skip(pad.toLong())
        }
    }
}
