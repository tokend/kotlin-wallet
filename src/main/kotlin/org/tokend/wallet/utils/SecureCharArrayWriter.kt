package org.tokend.wallet.utils

import org.tokend.crypto.ecdsa.erase
import java.io.CharArrayWriter

/**
 * [CharArrayWriter] that does not leave buffer copies
 * on size expansion.
 */
class SecureCharArrayWriter(initialSize: Int) : CharArrayWriter(initialSize) {
    /**
     * Writes a character to the buffer.
     */
    override fun write(c: Int) {
        synchronized(lock) {
            val newCount = count + 1
            if (newCount > buf.size) {
                val toErase = buf
                buf = buf.copyOf(Math.max(buf.size shl 1, newCount))
                toErase.erase()
            }
            buf[count] = c.toChar()
            count = newCount
        }
    }

    /**
     * Writes characters to the buffer.
     * @param c the data to be written
     * @param off       the start offset in the data
     * @param len       the number of chars that are written
     */
    override fun write(c: CharArray, off: Int, len: Int) {
        if (off < 0 || off > c.size || len < 0 ||
                off + len > c.size || off + len < 0) {
            throw IndexOutOfBoundsException()
        } else if (len == 0) {
            return
        }
        synchronized(lock) {
            val newCount = count + len
            if (newCount > buf.size) {
                val toErase = buf
                buf = buf.copyOf(Math.max(buf.size shl 1, newCount))
                toErase.erase()
            }
            System.arraycopy(c, off, buf, count, len)
            count = newCount
        }
    }

    /**
     * Write a portion of a string to the buffer.
     * @param  str  String to be written from
     * @param  off  Offset from which to start reading characters
     * @param  len  Number of characters to be written
     */
    override fun write(str: String, off: Int, len: Int) {
        synchronized(lock) {
            val newCount = count + len
            if (newCount > buf.size) {
                val toErase = buf
                buf = buf.copyOf(Math.max(buf.size shl 1, newCount))
                toErase.erase()
            }
            str.toCharArray(buf, count, off, off + len)
            count = newCount
        }
    }

    /**
     * Erases the buffer and resets chars count to 0.
     */
    fun erase() {
        close()
        buf.erase()
        reset()
    }
}