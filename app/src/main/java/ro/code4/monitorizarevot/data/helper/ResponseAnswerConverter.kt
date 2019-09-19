package ro.code4.monitorizarevot.data.helper

import androidx.room.TypeConverter
import com.google.gson.Gson
import org.koin.core.KoinComponent
import org.koin.core.inject
import ro.code4.monitorizarevot.data.model.response.ResponseAnswer

object ResponseAnswerConverter : KoinComponent {
    private val gson: Gson by inject()

    @TypeConverter
    fun toString(list: ArrayList<ResponseAnswer>?): String? = gson.toJson(list)

    @TypeConverter
    fun toList(value: String): ArrayList<ResponseAnswer>? {
        return gson.fromJson(value, Array<ResponseAnswer>::class.java).toCollection(ArrayList())
    }
}