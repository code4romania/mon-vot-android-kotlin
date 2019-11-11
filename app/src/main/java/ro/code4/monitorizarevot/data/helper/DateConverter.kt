package ro.code4.monitorizarevot.data.helper

import androidx.room.TypeConverter
import java.util.*

object DateConverter {
    @TypeConverter
    @JvmStatic
    fun toDate(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    @JvmStatic
    fun toTimestamp(date: Date): Long = date.time
}