package risyan.app.trustysnails.data.remote.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "history_items")
data class HistoryItem(
    @PrimaryKey(autoGenerate = false)
    var url: String,
    var title: String,
    val timestamp: Long = getCurrentDateInLongFormat(),
    val timeStampString : String = getCurrentDateInLongFormat().convertToUserFriendlyFormat()
)

fun getCurrentDateInLongFormat(): Long {
    val currentDate = Date()
    return currentDate.time
}

fun Long.convertLongToDate(): Date = Date(this)

val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

fun Date.toStringFormat(): String = dateFormat.format(this)

fun Long.convertToUserFriendlyFormat(): String {
    try {
        val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy HH:mm", Locale.getDefault())
        return outputFormat.format(this.convertLongToDate())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "Date Broken"
}

fun String.toDateFormat(): Date? {
    try {
        return dateFormat.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}
