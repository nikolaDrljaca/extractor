package com.drbrosdev.extractor.ui.components.recommendsearch

import androidx.compose.runtime.Stable
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.GenerateUserCollage
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.album.CompileTextAlbums
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedKeys
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.ui.overview.OverviewGridState
import com.drbrosdev.extractor.util.WhileUiSubscribed
import com.drbrosdev.extractor.util.asAlbumName
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@Stable
class RecommendedSearchesComponent(
    private val coroutineScope: CoroutineScope,
    private val trackExtractionProgress: TrackExtractionProgress,
    private val generateUserCollage: GenerateUserCollage,
    private val compileTextAlbums: CompileTextAlbums,
    private val createAlbum: suspend (NewAlbum) -> Unit,
    private val navigators: Navigators
) {
    private val eventBus = Channel<RecommendedSearchesEvents>()
    val events = eventBus.receiveAsFlow()

    val overviewGridState = OverviewGridState()

    val state: StateFlow<RecommendedSearchesState> = trackExtractionProgress.invoke()
        .flatMapLatest { transformState(it) }
        .stateIn(
            coroutineScope,
            WhileUiSubscribed,
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

    private fun transformState(status: ExtractionStatus): Flow<RecommendedSearchesState> {
        return when (status) {
            is ExtractionStatus.Running ->
                flowOf(RecommendedSearchesState.SyncInProgress(status.percentage))

            is ExtractionStatus.Done -> flow {
                val content = compileTextAlbums.invoke(7)
                emit(
                    when {
                        content.isNotEmpty() ->
                            RecommendedSearchesState.Content(
                                items = content,
                                onImageClick = ::handleImageClickEvent
                            )

                        else -> RecommendedSearchesState.Empty
                    }
                )
            }
        }
    }

    private fun handleImageClickEvent(keyword: String, index: Int) =
        state.value.findCollageByKeyword(keyword)?.let { collage ->
            val images = collage.extractions.map { it.uri.toUri() }
            navigators.navController.navigate(
                ExtractorImageViewerNavTarget(
                    images = images,
                    initialIndex = index
                )
            )
        }

    private suspend fun createNewAlbum(extractions: List<Extraction>) {
        if (extractions.isEmpty()) return
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
            entries = extractions.map {
                NewAlbum.Entry(
                    uri = it.uri,
                    id = it.mediaImageId
                )
            }
        )
        createAlbum(newAlbum)
    }
}