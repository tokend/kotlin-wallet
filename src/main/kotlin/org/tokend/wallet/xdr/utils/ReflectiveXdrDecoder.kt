package org.tokend.wallet.xdr.utils

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
    private val primitives = setOf("int", "long", "boolean", "java.lang.String", "[B")

    private fun isPrimitive(type: Class<out Any>): Boolean {
        return type.name in primitives
    }

    private fun readPrimitive(type: Class<out Any>, stream: XdrDataInputStream): Any {
        return when (type.name) {
            "int" -> Int.fromXdr(stream)
            "long" -> Long.fromXdr(stream)
            "boolean" -> Boolean.fromXdr(stream)
            "java.lang.String" -> String.fromXdr(stream)
            "[B" -> XdrOpaque.fromXdr(stream)
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
    private fun getUnionSwitchDiscriminantType(type: Class<out Any>): Class<*>? {
        val constructor = type.declaredConstructors[0]
        val paramAnnotations = constructor.parameterAnnotations
        val paramTypes = constructor.parameterTypes

        paramAnnotations.forEachIndexed { i, annotations ->
            if (annotations.any { it is XdrDiscriminantField })
                return paramTypes[i]
        }

        return null
    }

    private fun isUnionSwitch(type: Class<out Any>): Boolean {
        return !type.isArray && Modifier.isAbstract(type.modifiers)
                && getUnionSwitchDiscriminantType(type) != null
    }

    private fun readUnionSwitch(type: Class<out Any>, stream: XdrDataInputStream): Any {
        val discriminantEnumType = getUnionSwitchDiscriminantType(type)!!
        val discriminantEnumValue = readEnum(discriminantEnumType, stream)

        val nameKey = discriminantEnumValue.toString()
                .toLowerCase()
                .replace("_", "")

        val armClass = type.declaredClasses
                .find {
                    it.simpleName.toLowerCase() == nameKey
                }
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

        val valueField = type.getDeclaredField("value")

        valueField?.isAccessible = true

        val found = values.find {
            valueField?.get(it) == value
        }

        valueField?.isAccessible = false

        return found ?: error("Can't find ${type.name} enum value for $value")
    }
    // endregion

    // region Complex
    private fun readComplex(type: Class<out Any>, stream: XdrDataInputStream): Any {
        val constructor = type.declaredConstructors[0]
        val paramTypes = constructor.parameterTypes
        val paramAnnotations = constructor.parameterAnnotations

        val args = paramTypes.mapIndexed { i, paramType ->
            val isOptional = paramAnnotations[i].any { annotation ->
                annotation is XdrOptionalField
            }

            // Read value if it's not optional or is optional and present.
            if (!isOptional || Boolean.fromXdr(stream)) {
                read(paramType, stream)
            } else {
                null
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