package com.drbrosdev.extractor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.data.dao.ImageDataWithEmbeddingsDao
import com.drbrosdev.extractor.data.dao.PreviousSearchDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.PreviousSearchEntity
import com.drbrosdev.extractor.data.entity.TextEmbedding
import com.drbrosdev.extractor.data.entity.UserEmbedding
import com.drbrosdev.extractor.data.entity.VisualEmbedding

@Database(
    entities = [
        PreviousSearchEntity::class,
        ExtractionEntity::class,
        TextEmbedding::class,
        VisualEmbedding::class,
        UserEmbedding::class
    ],
    version = 5
)
abstract class ExtractorDatabase : RoomDatabase() {

    abstract fun previousSearchDao(): PreviousSearchDao

    abstract fun extractionEntityDao(): ExtractionEntityDao

    abstract fun imageDataWithEmbeddingsDao(): ImageDataWithEmbeddingsDao

    abstract fun textEmbeddingDao(): TextEmbeddingDao

    abstract fun visualEmbeddingDao(): VisualEmbeddingDao

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
