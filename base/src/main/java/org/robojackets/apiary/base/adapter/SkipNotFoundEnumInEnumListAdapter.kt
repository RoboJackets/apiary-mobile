package org.robojackets.apiary.base.adapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException

// Taken from https://stackoverflow.com/a/53675505
class SkipNotFoundEnumInEnumListAdapter<T : Enum<T>>(enumType: Class<T>) : JsonAdapter<List<T>>() {
    private val nameStrings: Array<String>
    private val constants: Array<T>
    private val options: JsonReader.Options

    init {
        try {
            constants = enumType.enumConstants
            nameStrings = Array(constants.size) {
                val constant = constants[it]
                val annotation = enumType.getField(constant.name).getAnnotation(Json::class.java)
                annotation?.name ?: constant.name
            }
            options = JsonReader.Options.of(*nameStrings)
        } catch (e: NoSuchFieldException) {
            throw AssertionError("Missing field in " + enumType.name, e)
        }
    }

    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): List<T> {
        reader.beginArray()
        val list = mutableListOf<T>()
        while (reader.hasNext()) {
            val index = reader.selectString(options)
            if (index != -1) {
                list += constants[index]
            } else {
                reader.skipValue()
            }
        }
        reader.endArray()
        return list
    }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: List<T>?) {
        if (value == null) {
            throw IllegalArgumentException("Wrap in .nullSafe()")
        }
        writer.beginArray()
        for (i in value.indices) {
            writer.value(nameStrings[value[i].ordinal])
        }
        writer.endArray()
    }
}
// End of code from https://stackoverflow.com/a/53675505
