package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.ImageEmbeddingSearchStrategy
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class CompileTextAlbums(
    private val dispatcher: CoroutineDispatcher,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val imageEmbeddingsDao: ImageEmbeddingsDao,
    private val albumRepository: AlbumRepository
) {

    suspend operator fun invoke() = withContext(dispatcher) {
        val allText = textEmbeddingDao.findAllTextEmbedValues()
        /*
        1. Tokenize allText into List<String> where each entry is one word
        2. Filter out most common words based on a stop list
        3. Build a word frequency map, pass in each word
        4. Sort by frequency and grab the first 7
        5. For each of those 7, fetch imageEmbeddings to create albums, reference visual
         */

        val tokens = allText
            .tokenizeToFlow()
            .filter { it.filterWithStopWords(stopWords) }
            .flowOn(dispatcher)
            .toList()

        val job = tokens
            .createFrequencyMap()
            .generateMostCommon()
            .asFlow()
            .map { topWord ->
                //TODO @nikola Full strategy text search does not work, query?
                val strategy = ImageEmbeddingSearchStrategy.Partial(topWord)
                val embeddings = imageEmbeddingsDao.findByTextEmbedding(strategy.query)
                embeddings to topWord
            }
            .filter { (embeddings, _) -> embeddings.isNotEmpty() }
            .map {
                val newAlbum = it.createNewAlbumFromPayload()
                albumRepository.createAlbum(newAlbum)
            }
            .flowOn(dispatcher)
            .launchIn(this)

    }

    private fun String.prepare(): String = this
        .trim()
        .replace(Regex("\\s+"), " ")
        .lowercase()

    private fun String.tokenizeToFlow(): Flow<String> = flow {
        val temp = this@tokenizeToFlow.prepare()

        val pattern = Pattern.compile("\\b\\w+\\b") // Matches word boundaries
        val matcher = pattern.matcher(temp)

        while (matcher.find()) {
            emit(matcher.group())
        }
    }

    private fun String.filterWithStopWords(stopWords: Set<String>) = when {
        length < 3 -> false
        toDoubleOrNull() != null -> false
        this in stopWords -> false
        else -> true
    }

    private fun List<String>.createFrequencyMap(): Map<String, Int> {
        return groupingBy { it }.eachCount()
    }

    private fun Map<String, Int>.generateMostCommon(amount: Int = 7): List<String> {
        return this.entries
            .sortedByDescending { it.value }
            .map { it.key }
            .take(amount)
    }

    private fun CompileAlbumPayload.createNewAlbumFromPayload(): NewAlbum {
        val (embeds, embedUsage) = this
        val entries = embeds.map {
            NewAlbum.Entry(
                uri = MediaImageUri(it.imageEntity.uri),
                id = MediaImageId(it.imageEntity.mediaStoreId)
            )
        }

        return NewAlbum(
            keyword = embedUsage,
            name = embedUsage,
            searchType = SearchType.FULL,
            labelType = LabelType.TEXT,
            entries = entries,
            origin = NewAlbum.Origin.TEXT_COMPUTED
        )
    }

    private val stopWords = setOf(
        "a", "an", "and", "are", "as", "at", "be", "for", "from", "has",
        "he", "in", "is", "it", "its", "of", "on", "that", "the", "to",
        "was", "were", "will", "with", "by", "can", "do", "does", "doing",
        "done", "had", "has", "have", "having", "if", "into", "is", "it",
        "its", "not", "now", "or", "our", "should", "so", "than", "that", "the", "their", "there",
        "you", "your", "this", "com"
    )
}