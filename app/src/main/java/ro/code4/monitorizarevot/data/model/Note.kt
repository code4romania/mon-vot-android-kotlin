package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(
    tableName = "note", foreignKeys = [ForeignKey(
        entity = Question::class,
        parentColumns = ["id"],
        childColumns = ["questionId"],
        onDelete = ForeignKey.CASCADE
    )]
)
class Note {
    @PrimaryKey(autoGenerate = true)
    private var id: Int = 0

    @Expose
    var uriPath: String? = null

    @Expose
    var description: String? = null

    @Expose
    var questionId: Int? = null

}