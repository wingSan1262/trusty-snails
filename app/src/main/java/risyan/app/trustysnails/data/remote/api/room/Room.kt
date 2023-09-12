package risyan.app.trustysnails.data.remote.api.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import risyan.app.trustysnails.data.remote.model.HistoryItem

@Database(entities = [HistoryItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyItemDao(): HistoryItemDao
}

@Dao
interface HistoryItemDao {

    @Query("SELECT * FROM history_items WHERE url LIKE '%' || :searchQuery || '%'" +
            " OR title LIKE '%' || :searchQuery || '%'" +
            " OR timestampString LIKE '%' || :searchQuery || '%'" +
            " ORDER BY timestamp DESC LIMIT :pageSize OFFSET :startIndex")
    suspend fun searchHistoryItems(searchQuery: String, pageSize: Int, startIndex: Int): List<HistoryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyItem: HistoryItem)
}
