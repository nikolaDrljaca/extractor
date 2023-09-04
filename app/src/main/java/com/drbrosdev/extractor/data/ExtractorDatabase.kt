package com.drbrosdev.extractor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ImageDataEntity::class, PreviousSearchEntity::class],
    version = 2
)
abstract class ExtractorDatabase : RoomDatabase() {
    abstract fun imageDataDao(): ImageDataDao

    abstract fun previousSearchDao(): PreviousSearchDao

    companion object {
        fun createExtractorDatabase(context: Context): ExtractorDatabase {
            return Room.databaseBuilder(
                context,
                ExtractorDatabase::class.java,
                "extractor_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
