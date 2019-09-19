package ro.code4.monitorizarevot.data.helper

import androidx.room.TypeConverter
import java.util.*

object DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? = if (timestamp == null) null else Date(timestamp)

    @TypeConverter
    fun toTimestamp(date: Date?): Long? = (date?.time)
}