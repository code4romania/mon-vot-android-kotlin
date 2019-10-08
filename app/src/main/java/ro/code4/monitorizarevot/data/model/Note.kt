package ro.code4.monitorizarevot.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "note", foreignKeys = [ForeignKey(
        entity = Question::class,
        parentColumns = ["id"],
        childColumns = ["questionId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = BranchDetails::class,
        parentColumns = ["countyCode", "branchNumber"],
        childColumns = ["countyCode", "branchNumber"]
    )], indices = [Index(value = ["countyCode", "branchNumber", "questionId"], unique = false)]
)
class Note {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var uriPath: String? = null

    lateinit var description: String

    var questionId: Int? = null

    var date: Date = Date()

    lateinit var countyCode: String
    var branchNumber = 0

    var synced = false

}