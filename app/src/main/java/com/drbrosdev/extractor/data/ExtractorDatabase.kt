package com.drbrosdev.extractor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.drbrosdev.extractor.data.album.dao.AlbumConfigurationDao
import com.drbrosdev.extractor.data.album.dao.AlbumDao
import com.drbrosdev.extractor.data.album.dao.AlbumEntryDao
import com.drbrosdev.extractor.data.album.dao.AlbumRelationDao
import com.drbrosdev.extractor.data.album.record.AlbumConfigurationRecord
import com.drbrosdev.extractor.data.album.record.AlbumEntryRecord
import com.drbrosdev.extractor.data.album.record.AlbumRecord
import com.drbrosdev.extractor.data.extraction.dao.DescriptionEmbeddingDao
import com.drbrosdev.extractor.data.extraction.dao.ExtractionDao
import com.drbrosdev.extractor.data.extraction.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.extraction.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.extraction.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.extraction.dao.UserExtractionDao
import com.drbrosdev.extractor.data.extraction.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.extraction.record.DescriptionEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.LupaImageMetadataRecord
import com.drbrosdev.extractor.data.extraction.record.TextEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.UserEmbeddingRecord
import com.drbrosdev.extractor.data.extraction.record.VisualEmbeddingRecord
import com.drbrosdev.extractor.data.search.SearchIndexDao
import com.drbrosdev.extractor.data.search.SearchIndexFts
import com.drbrosdev.extractor.data.search.SearchIndexRecord

@Database(
    entities = [
        LupaImageMetadataRecord::class,
        TextEmbeddingRecord::class,
        DescriptionEmbeddingRecord::class,
        VisualEmbeddingRecord::class,
        UserEmbeddingRecord::class,
        AlbumRecord::class,
        AlbumEntryRecord::class,
        AlbumConfigurationRecord::class,
        SearchIndexRecord::class,
        SearchIndexFts::class
    ],
    version = 18,
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

    abstract fun descriptionEmbeddingDao(): DescriptionEmbeddingDao

    companion object {
        fun createExtractorDatabase(context: Context): ExtractorDatabase {
            return Room.databaseBuilder(
                context,
                ExtractorDatabase::class.java,
                "extractor_database"
            )
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }
}
