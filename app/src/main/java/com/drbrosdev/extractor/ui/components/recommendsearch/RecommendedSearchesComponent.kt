package com.drbrosdev.extractor.ui.components.recommendsearch

import androidx.compose.runtime.Stable
import com.drbrosdev.extractor.domain.model.ExtractionProgress
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.LupaImageMetadata
import com.drbrosdev.extractor.domain.model.asStatus
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.generate.GenerateMostCommonExtractionBundles
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedKeys
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.components.showcase.ExtractorShowcaseDefaults
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.ui.overview.OverviewGridState
import com.drbrosdev.extractor.util.asAlbumName
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@Stable
class RecommendedSearchesComponent(
    private val coroutineScope: CoroutineScope,
    private val extractionProgress: Flow<ExtractionProgress>,
    private val generateBundles: GenerateMostCommonExtractionBundles,
    private val createAlbum: suspend (NewAlbum) -> Unit,
    private val mostRecentExtractionFlow: Flow<LupaImageHighlight>,
    private val navigators: Navigators
) {
    private val eventBus = Channel<RecommendedSearchesEvents>()
    val events = eventBus.receiveAsFlow()

    val overviewGridState = OverviewGridState()

    val state = extractionProgress
        .asStatus()
        .flatMapLatest {
            when (it) {
                ExtractionStatus.DONE -> {
                    flowOf(generateBundles.execute())
                        .map { bundles ->
                            when {
                                bundles.isNotEmpty() ->
                                    RecommendedSearchesState.Content(
                                        items = bundles,
                                        onImageClick = ::handleImageClickEvent
                                    )

                                else -> RecommendedSearchesState.Empty
                            }
                        }
                }

                ExtractionStatus.RUNNING -> mostRecentExtractionFlow
                    .map { internal -> RecommendedSearchesState.SyncInProgress(internal) }
                    .conflate()
                    .onEach { internal ->
                        if (internal.isShowcaseActive()) {
                            delay(ExtractorShowcaseDefaults.SHOWCASE_SAMPLE_RATE)
                        }
                    }
            }
        }
        .stateIn(
            coroutineScope,
            SharingStarted.Lazily,
            RecommendedSearchesState.Loading
        )

    fun multiselectBarEventHandler(event: MultiselectAction) {
        when (event) {
            // Not supported here
            MultiselectAction.Delete -> Unit

            MultiselectAction.CreateAlbum -> coroutineScope.launch {
                withContext(Dispatchers.Default) {
                    // retrieve from checked items
                    val extractions = overviewGridState.gridState.checkedKeys()
                        .mapNotNull { state.value.getImageUris()[it] }
                    // create album
                    createNewAlbum(extractions)
                    // clear selection
                    overviewGridState.gridState.clearSelection()
                    // send event
                    eventBus.send(RecommendedSearchesEvents.AlbumCreated)
                }
            }

            MultiselectAction.Cancel -> overviewGridState.gridState.clearSelection()

            MultiselectAction.Share -> coroutineScope.launch {
                withContext(Dispatchers.Default) {
                    val indices = overviewGridState.gridState.checkedKeys()
                        .mapNotNull { state.value.getImageUris()[it] }
                        .map { it.uri.toUri() }
                    eventBus.send(RecommendedSearchesEvents.ShareImages(indices))
                    overviewGridState.gridState.clearSelection()
                }
            }
        }
    }

    private fun handleImageClickEvent(keyword: String, index: Int) {
        state.value.findCollageByKeyword(keyword)?.let { collage ->
            val images = collage.images.map { it.uri.toUri() }
            navigators.navController.navigate(
                ExtractorImageViewerNavTarget(
                    images = images,
                    initialIndex = index
                )
            )
        }
    }

    private suspend fun createNewAlbum(lupaImageMetadata: List<LupaImageMetadata>) {
        if (lupaImageMetadata.isEmpty()) return
        // create album
        val name = LocalDateTime.now()
            .asAlbumName()
        val keyword = state.value.keywords().first()
        val newAlbum = NewAlbum(
            name = name,
            keyword = keyword,
            searchType = SearchType.PARTIAL,
            keywordType = KeywordType.TEXT,
            origin = NewAlbum.Origin.USER_GENERATED,
            entries = lupaImageMetadata.map {
                NewAlbum.Entry(
                    uri = it.uri,
                    id = it.mediaImageId
                )
            }
        )
        createAlbum(newAlbum)
    }
}