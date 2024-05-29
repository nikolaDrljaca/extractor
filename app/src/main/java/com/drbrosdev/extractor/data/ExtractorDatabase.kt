package com.drbrosdev.extractor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.drbrosdev.extractor.data.dao.AlbumConfigurationDao
import com.drbrosdev.extractor.data.dao.AlbumDao
import com.drbrosdev.extractor.data.dao.AlbumEntryDao
import com.drbrosdev.extractor.data.dao.AlbumRelationDao
import com.drbrosdev.extractor.data.dao.ExtractionDao
import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.dao.SearchIndexDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserExtractionDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.AlbumConfigurationEntity
import com.drbrosdev.extractor.data.entity.AlbumEntity
import com.drbrosdev.extractor.data.entity.AlbumEntryEntity
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.SearchIndexEntity
import com.drbrosdev.extractor.data.entity.SearchIndexFts
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity
import com.drbrosdev.extractor.data.entity.UserEmbeddingEntity
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity

@Database(
    entities = [
        ExtractionEntity::class,
        TextEmbeddingEntity::class,
        VisualEmbeddingEntity::class,
        UserEmbeddingEntity::class,
        AlbumEntity::class,
        AlbumEntryEntity::class,
        AlbumConfigurationEntity::class,
        SearchIndexEntity::class,
        SearchIndexFts::class
    ],
    version = 15,
)
@TypeConverters(DatabaseConverters::class)
abstract class ExtractorDatabase : RoomDatabase() {

    abstract fun extractionEntityDao(): ExtractionDao

    abstract fun imageDataWithEmbeddingsDao(): ImageEmbeddingsDao

    abstract fun textEmbeddingDao(): TextEmbeddingDao

    abstract fun visualEmbeddingDao(): VisualEmbeddingDao

    abstract fun userEmbeddingDao(): UserEmbeddingDao

    abstract fun albumDao(): AlbumDao

    abstract fun albumConfigurationDao(): AlbumConfigurationDao

    abstract fun albumRelationDao(): AlbumRelationDao

    abstract fun albumEntryDao(): AlbumEntryDao

    abstract fun searchIndexDao(): SearchIndexDao

    abstract fun userExtractionDao(): UserExtractionDao

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
