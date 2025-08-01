package by.liscode.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterDao {
    @Insert
    suspend fun insert(filter: FilterEntity)

    @Query("DELETE FROM filters")
    suspend fun clearAll()

    @Query("SELECT * FROM filters")
    fun getAllFilters(): Flow<List<FilterEntity>>
}