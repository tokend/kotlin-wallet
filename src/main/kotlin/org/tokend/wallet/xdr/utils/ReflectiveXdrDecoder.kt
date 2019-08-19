package org.tokend.wallet.xdr.utils

import org.tokend.wallet.xdr.XdrByteArrayFixed16
import org.tokend.wallet.xdr.XdrByteArrayFixed32
import org.tokend.wallet.xdr.XdrByteArrayFixed4
import java.lang.reflect.Modifier

object ReflectiveXdrDecoder {
    fun <T : Any> read(type: Class<out T>, stream: XdrDataInputStream): T {
        return when {
            isPrimitive(type) -> readPrimitive(type, stream)
            isEnum(type) -> readEnum(type, stream)
            isFixedByteArray(type) -> readFixedByteArray(type, stream)
            isUnionSwitch(type) -> readUnionSwitch(type, stream)
            else -> readComplex(type, stream)
        } as T
    }

    // region Primitive
    private val primitives = setOf(
            "int", "long", "boolean", "java.lang.String", "byte[]"
    )

    private fun isPrimitive(type: Class<out Any>): Boolean {
        return type.typeName in primitives
    }

    private fun readPrimitive(type: Class<out Any>, stream: XdrDataInputStream): Any {
        return when (type.typeName) {
            "int" -> Int.fromXdr(stream)
            "long" -> Long.fromXdr(stream)
            "boolean" -> Boolean.fromXdr(stream)
            "java.lang.String" -> String.fromXdr(stream)
            "byte[]" -> XdrOpaque.fromXdr(stream)
            else -> error("Unknwon primitive $type")
        }
    }
    // endregion

    // region Fixed byte arrays
    private fun isFixedByteArray(type: Class<out Any>): Boolean {
        return XdrFixedByteArray::class.java.isAssignableFrom(type)
    }

    private fun readFixedByteArray(type: Class<out Any>, stream: XdrDataInputStream):
            XdrFixedByteArray {
        val readByteArray = { size: Int ->
            ByteArray(size).also { stream.read(it) }
        }

        return when (type) {
            XdrByteArrayFixed4::class.java -> XdrByteArrayFixed4(readByteArray(4))
            XdrByteArrayFixed16::class.java -> XdrByteArrayFixed16(readByteArray(16))
            XdrByteArrayFixed32::class.java -> XdrByteArrayFixed32(readByteArray(32))
            else -> error("Unknown fixed byte array $type")
        }
    }
    // endregion

    // region Union switch
    private fun isUnionSwitch(type: Class<out Any>): Boolean {
        return Modifier.isAbstract(type.modifiers)
                && try {
            type.getDeclaredField("discriminant")
        } catch (_: Exception) {
            null
        } != null
    }

    private fun readUnionSwitch(type: Class<out Any>, stream: XdrDataInputStream): Any {
        val discriminantEnumType = type.getDeclaredField("discriminant").type
        val discriminantEnumValue = readEnum(discriminantEnumType, stream)

        val ordinal = discriminantEnumType.enumConstants.indexOf(discriminantEnumValue)

        val armClass = type.declaredClasses.getOrNull(ordinal)
                ?: error("Unknown union switch $type arm index $ordinal")

        return readComplex(armClass, stream)
    }
    // endregion

    // region Enum
    private fun isEnum(type: Class<out Any>): Boolean {
        return type.isEnum
    }

    fun readEnum(type: Class<out Any>, stream: XdrDataInputStream): Any {
        val value = Int.fromXdr(stream)
        val values = type.enumConstants
        val valueField = values[0]::class.java.getDeclaredField("value")

        valueField.isAccessible = true

        val found = values.find {
            valueField.get(it) == value
        }

        valueField.isAccessible = false

        return found ?: error("Can't find $type enum value for $value")
    }
    // endregion

    // region Complex
    fun readComplex(type: Class<out Any>, stream: XdrDataInputStream): Any {
        val constructor = type.declaredConstructors[0]
        val fieldValues = type.declaredFields.map {
            read(it.type, stream)
        }
        return constructor.newInstance(*fieldValues.toTypedArray())
    }
    // endregion
}