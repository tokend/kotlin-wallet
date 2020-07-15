package org.tokend.wallet.xdr.utils

import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream

class XdrDataOutputStream(out: OutputStream) : DataOutputStream(XdrOutputStream(out)) {

    private val mOut: XdrOutputStream

    init {
        mOut = super.out as XdrOutputStream
    }

    @Throws(IOException::class)
    fun writeString(s: String) {
        val chars = s.toByteArray(Charsets.UTF_8)
        writeInt(chars.size)
        write(chars)
        pad()
    }

    @Throws(IOException::class)
    fun writeIntArray(a: IntArray) {
        writeInt(a.size)
        writeIntArray(a, a.size)
    }

    @Throws(IOException::class)
    fun writeIntArray(a: IntArray, l: Int) {
        for (i in 0 until l) {
            writeInt(a[i])
        }
    }

    @Throws(IOException::class)
    fun writeFloatArray(a: FloatArray) {
        writeInt(a.size)
        writeFloatArray(a, a.size)
    }

    @Throws(IOException::class)
    fun writeFloatArray(a: FloatArray, l: Int) {
        for (i in 0 until l) {
            writeFloat(a[i])
        }
    }

    @Throws(IOException::class)
    fun writeDoubleArray(a: DoubleArray) {
        writeInt(a.size)
        writeDoubleArray(a, a.size)
    }

    @Throws(IOException::class)
    fun writeDoubleArray(a: DoubleArray, l: Int) {
        for (i in 0 until l) {
            writeDouble(a[i])
        }
    }

    @Throws(IOException::class)
    fun pad() {
        mOut.pad()
    }

    private class XdrOutputStream(private val mOut: OutputStream) : OutputStream() {

        private var mCount: Int = 0

        init {
            mCount = 0
        }

        @Throws(IOException::class)
        override fun write(b: Int) {
            mOut.write(b)
            mCount++
        }

        @Throws(IOException::class)
        override fun write(b: ByteArray) {
            mOut.write(b)
            mCount += b.size
        }

        @Throws(IOException::class)
        override fun write(b: ByteArray, offset: Int, length: Int) {
            mOut.write(b, offset, length)
            mCount += length
        }

        @Throws(IOException::class)
        fun pad() {
            var pad = 0
            val mod = mCount % 4
            if (mod > 0) {
                pad = 4 - mod
            }
            while (pad-- > 0) {
                write(0)
            }
        }
    }
}
