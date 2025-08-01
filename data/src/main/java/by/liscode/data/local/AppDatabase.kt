package by.liscode.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FilterEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract val filterDao: FilterDao
}