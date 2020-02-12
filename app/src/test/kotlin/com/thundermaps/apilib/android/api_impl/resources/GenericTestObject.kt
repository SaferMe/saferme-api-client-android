package com.thundermaps.apilib.android.api_impl.resources

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import com.thundermaps.apilib.android.api.resources.SaferMeDatum
import java.text.SimpleDateFormat
import java.util.Date
import java.util.GregorianCalendar
import java.util.Random
import java.util.TimeZone

// Useful class for testing HTTP Calls & Serialization
data class GenericTestObject(
    @Expose
    val intVal: Int?,

    @Expose
    val stringVal: String?,

    @Expose
    val dateVal: Date?,

    @Expose
    val boolVal: Boolean?

) : SaferMeDatum {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is GenericTestObject) return false

        // intval
        val i = (this.intVal == other.intVal)

        // StringVal
        val s = (this.stringVal == other.stringVal)

        // dateVal
        val d = (this.dateVal == other.dateVal)

        // BoolVal
        val b = (this.boolVal == other.boolVal)

        return i && s && d && b
    }

    fun toJsonString(): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        fmt.timeZone = TimeZone.getTimeZone("UTC")
        val obj = JsonObject()
        obj.addProperty("intVal", intVal)
        obj.addProperty("stringVal", stringVal)
        obj.addProperty("dateVal", fmt.format(dateVal))
        obj.addProperty("boolVal", boolVal)
        return obj.toString()
    }

    fun toJsonObject(): JsonObject {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        fmt.timeZone = TimeZone.getTimeZone("UTC")
        val obj = JsonObject()
        obj.addProperty("intVal", intVal)
        obj.addProperty("stringVal", stringVal)
        obj.addProperty("dateVal", fmt.format(dateVal))
        obj.addProperty("boolVal", boolVal)
        return obj
    }

    class GenericListToken : TypeToken<List<GenericTestObject>>()

    companion object {
        private val random = Random()
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + (' ')

        private fun randomDate(): Date {
            val gc = GregorianCalendar(TimeZone.getTimeZone("UTC"))
            val year = random.nextInt(200) + 1900
            gc.set(GregorianCalendar.YEAR, year)
            val dayOfYear = random.nextInt((gc.getActualMaximum(GregorianCalendar.DAY_OF_YEAR) - 1)) + 1
            gc.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear)

            // TODO We don't currently support millisecond accuracy
            gc.set(GregorianCalendar.MILLISECOND, 0)
            return gc.time
        }

        fun random(): GenericTestObject {
            val len = random.nextInt(255)
            val randomString = (1..len)
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
            return GenericTestObject(random.nextInt(), randomString, randomDate(), random.nextBoolean())
        }

        fun randomList(count: Int = 5): List<GenericTestObject> {
            val list = ArrayList<GenericTestObject>()
            for (i in 1..5) list.add(random())
            return list
        }

        fun listToJson(list: List<GenericTestObject>): String {
            val arr = JsonArray()
            for (item in list) arr.add(item.toJsonObject())
            return arr.toString()
        }
    }
}
