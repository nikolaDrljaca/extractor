package com.drbrosdev.extractor

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.drbrosdev.extractor.data.ExtractorDatabase
import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.data.dao.ImageDataWithEmbeddingsDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VisualEmbeddingDaoTest {
    private lateinit var database: ExtractorDatabase

    private lateinit var extractorDao: ExtractionEntityDao
    private lateinit var textDao: TextEmbeddingDao
    private lateinit var visualDao: VisualEmbeddingDao
    private lateinit var queryDao: ImageDataWithEmbeddingsDao

    @Before
    fun init() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context = context,
            ExtractorDatabase::class.java
        ).build()
        extractorDao = database.extractionEntityDao()
        textDao = database.textEmbeddingDao()
        visualDao = database.visualEmbeddingDao()
        queryDao = database.imageDataWithEmbeddingsDao()
    }

    private suspend fun setup() {
        val imageEntityId = 10L
        val visualEmbeddingEntities = buildList {
            repeat(10) {
                add(
                    VisualEmbeddingEntity(
                        id = (it + 1).toLong(),
                        imageEntityId = imageEntityId,
                        value = "someText$it"
                    )
                )
            }
        }
        extractorDao.insert(
            ExtractionEntity(
                mediaStoreId = imageEntityId,
                uri = "some_uri"
            )
        )
        visualEmbeddingEntities.forEach {
            visualDao.insert(it)
        }
    }

    private suspend fun teardown() {
        database.clearAllTables()
    }

    @Test
    fun shouldBeAdded() = runBlocking {
        setup()
        assert(visualDao.getCount() == 10) { "Not all were inserted" }
        teardown()
    }

    @Test
    fun shouldDeleteAllVisualEmbeddings() = runBlocking {
        setup()
        visualDao.deleteByMediaId(mediaId = 10L)
        val size = visualDao.getCount()
        assert(size == 0) { "All embeddings were not deleted." }
        teardown()
    }
}