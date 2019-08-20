package org.tokend.wallet.xdr.utils

import kotlinx.reflect.lite.ReflectionLite
import org.tokend.wallet.xdr.XdrByteArrayFixed16
import org.tokend.wallet.xdr.XdrByteArrayFixed32
import org.tokend.wallet.xdr.XdrByteArrayFixed4
import java.lang.reflect.Modifier

/**
 * Used to decode XDRs with reflection.
 */
object ReflectiveXdrDecoder {
    /**
     * @return [ReflectiveXdrDecoder]-based [XdrDecodable] instance for given type
     */
    inline fun <reified T: Any>wrapType(): XdrDecodable<T> {
        return object : XdrDecodable<T> {
            override fun fromXdr(stream: XdrDataInputStream): T {
                return read(T::class.java, stream)
            }
        }
    }

    /**
     * @return Value of [clazz] type decoded from [stream] content
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> read(clazz: Class<out T>, stream: XdrDataInputStream): T {
        return when {
            isPrimitive(clazz) -> readPrimitive(clazz, stream)
            isEnum(clazz) -> readEnum(clazz, stream)
            isFixedByteArray(clazz) -> readFixedByteArray(clazz, stream)
            isUnionSwitch(clazz) -> readUnionSwitch(clazz, stream)
            isArray(clazz) -> readArray(clazz, stream)
            else -> readComplex(clazz, stream)
        } as T
    }

    // region Primitives
    private val primitives = setOf("int", "long", "boolean", "java.lang.String", "byte[]")

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
            else -> error("Unknown primitive $type")
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
                && type.declaredFields.firstOrNull()?.type?.isEnum == true
    }

    private fun readUnionSwitch(type: Class<out Any>, stream: XdrDataInputStream): Any {
        // Assume that union switch first field is a discriminant.
        val discriminantEnumType = type.declaredFields.first().type
        val discriminantEnumValue = readEnum(discriminantEnumType, stream)

        val nameKey = discriminantEnumValue.toString()
                .toLowerCase()
                .replace("_", "")

        val armClass = type.declaredClasses
                .find { it.simpleName.toLowerCase() == nameKey }
                ?: error("Unknown union switch $type arm index $discriminantEnumValue")

        return readComplex(armClass, stream)
    }
    // endregion

    // region Enum
    private fun isEnum(type: Class<out Any>): Boolean {
        return type.isEnum
    }

    private fun readEnum(type: Class<out Any>, stream: XdrDataInputStream): Any {
        val value = Int.fromXdr(stream)
        val values = type.enumConstants

        // Assume that XDR enums have only one custom field and it's an int value.
        val valueField = values[0]::class.java.declaredFields.last()

        valueField.isAccessible = true

        val found = values.find {
            valueField.get(it) == value
        }

        valueField.isAccessible = false

        return found ?: error("Can't find ${type.name} enum value for $value")
    }
    // endregion

    // region Complex
    private fun readComplex(type: Class<out Any>, stream: XdrDataInputStream): Any {
        val constructor = type.declaredConstructors[0]
        val constructorMetadata = ReflectionLite.loadClassMetadata(type)
                ?.getConstructor(constructor)
        val args = constructor.parameterTypes.mapIndexed { i, it ->
            val isOptional = constructorMetadata?.parameters?.get(i)?.type?.isNullable == true

            if (isOptional) {
                val isPresent = Boolean.fromXdr(stream)
                if (isPresent) {
                    read(it, stream)
                } else {
                    null
                }
            } else {
                read(it, stream)
            }
        }

        return constructor.newInstance(*args.toTypedArray())
    }
    // endregion

    // region Arrays
    private fun isArray(type: Class<out Any>): Boolean {
        return type.isArray
    }

    private fun readArray(type: Class<out Any>, stream: XdrDataInputStream): Any {
        val elementType = type.componentType
        val size = Int.fromXdr(stream)
        val array = java.lang.reflect.Array.newInstance(elementType, size)
        for (i in 0 until size) {
            java.lang.reflect.Array.set(array, i, read(elementType, stream))
        }
        return array
    }
    // endregion
}