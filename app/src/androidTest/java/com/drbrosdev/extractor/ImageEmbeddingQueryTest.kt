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
import com.drbrosdev.extractor.data.entity.ImageDataWithEmbeddings
import com.drbrosdev.extractor.data.entity.TextEmbedding
import com.drbrosdev.extractor.data.entity.VisualEmbedding
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageEmbeddingQueryTest {
    private lateinit var database: ExtractorDatabase

    private lateinit var extractorDao: ExtractionEntityDao
    private lateinit var textDao: TextEmbeddingDao
    private lateinit var visualDao: VisualEmbeddingDao
    private lateinit var queryDao: ImageDataWithEmbeddingsDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context = context,
            ExtractorDatabase::class.java
        ).build()
        extractorDao = database.extractionEntityDao()
        textDao = database.textEmbeddingDao()
        visualDao = database.visualEmbeddingDao()
        queryDao = database.imageDataWithEmbeddingsDao()

        //Init some data
        val imageEntityId = 10L
        val textData = TextEmbedding(id = 1, imageEntityId = imageEntityId, value = "this")
        val visualData =
            VisualEmbedding(id = 1, imageEntityId = imageEntityId, value = "visualData")
        val extractionEntity =
            ExtractionEntity(mediaStoreId = imageEntityId, uri = "sadfasdf")

        runBlocking {
            textDao.insert(textData)
            visualDao.insert(visualData)
            visualDao.insert(
                VisualEmbedding(
                    id = 2,
                    imageEntityId = 10L,
                    value = "some_other"
                )
            )
            extractorDao.insert(extractionEntity)
        }

    }

    @Test
    fun shouldNotFindEmbeddingsAfterDeletion() = runBlocking {
        val imageEntityId = 11L
        val textData = TextEmbedding(id = 10, imageEntityId = imageEntityId, value = "this")
        val visualData =
            VisualEmbedding(id = 10, imageEntityId = imageEntityId, value = "visualData")
        val extractionEntity =
            ExtractionEntity(mediaStoreId = imageEntityId, uri = "sadfasdf")

        textDao.insert(textData)
        visualDao.insert(visualData)
        extractorDao.insert(extractionEntity)

        //TODO: Have to delete both, extractor entity and related embeddings
        //TODO: Possibly use a Repository or composite DAO for this - Service?
        extractorDao.deleteByMediaId(imageEntityId)
        visualDao.delete(visualData)
        textDao.delete(textData)

        assert(textDao.findById(10L) == null) { "Text embedding entity NOT deleted." }
        assert(visualDao.findById(10L) == null) { "Visual embedding NOT deleted." }
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun shouldFindTextEmbedding() = runBlocking {
        val query = "this"
        val result = queryDao.findByLabel(query)

        assert(result.isNotEmpty()) { "Result set is empty." }

        assert(
            result.first().textEmbedding.value.contains(query)
        ) { "Text embedding value not present." }
    }

    @Test
    fun shouldFindVisualEmbedding() = runBlocking {
        val query = "visualData"
        val result = queryDao.findByLabel(query)
        assert(result.isNotEmpty()) { "Result set is empty." }

        println(result)

        assert(
            result.first().visualEmbeddings.first().value.contains(query)
        ) { "Visual embedding value not present." }
    }

    @Test
    fun shouldFindWithCombinedQuery() = runBlocking {
        val query = "visualData this"
        val out = mutableListOf<List<ImageDataWithEmbeddings>>()
        query.split(" ").forEach {
            out.add(queryDao.findByLabel(it))
        }

        println(out)
        assert(out.isNotEmpty()) { "Result set is empty." }
    }

    @Test
    fun shouldFindByVisualEmbedding() = runBlocking {
        val query = "visualData"
        val result = queryDao.findByVisualEmbedding(query)
        println(result)
        assert(result.isNotEmpty()) { "Result set is empty." }

        assert(
            result.first().visualEmbeddings.contains(
                VisualEmbedding(
                    id = 1,
                    imageEntityId = 10L,
                    value = "visualData"
                )
            )
        )
    }

    @Test
    fun shouldNotFindUserEmbedding() = runBlocking {
        val query = "userEmbed"
        val result = queryDao.findByLabel(query)

        assert(result.isEmpty()) { "Result set is NOT empty." }
    }
}