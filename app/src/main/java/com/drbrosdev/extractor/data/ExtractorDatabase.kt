package com.drbrosdev.extractor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.dao.PreviousSearchDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.PreviousSearchEntity
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity
import com.drbrosdev.extractor.data.entity.UserEmbeddingEntity
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity

@Database(
    entities = [
        PreviousSearchEntity::class,
        ExtractionEntity::class,
        TextEmbeddingEntity::class,
        VisualEmbeddingEntity::class,
        UserEmbeddingEntity::class
    ],
    version = 5
)
abstract class ExtractorDatabase : RoomDatabase() {

    abstract fun previousSearchDao(): PreviousSearchDao

    abstract fun extractionEntityDao(): ExtractionEntityDao

    abstract fun imageDataWithEmbeddingsDao(): ImageEmbeddingsDao

    abstract fun textEmbeddingDao(): TextEmbeddingDao

    abstract fun visualEmbeddingDao(): VisualEmbeddingDao

    abstract fun userEmbeddingDao(): UserEmbeddingDao

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
