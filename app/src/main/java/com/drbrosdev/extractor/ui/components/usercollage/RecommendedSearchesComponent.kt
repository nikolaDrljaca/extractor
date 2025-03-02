package com.drbrosdev.extractor.ui.components.usercollage

import android.net.Uri
import androidx.compose.runtime.Stable
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.domain.usecase.GenerateUserCollage
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.album.CompileTextAlbums
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedKeys
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.ui.overview.OverviewGridState
import com.drbrosdev.extractor.util.WhileUiSubscribed
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ShareImagesEvent(val uris: List<Uri>)

@Stable
class RecommendedSearchesComponent(
    private val coroutineScope: CoroutineScope,
    private val trackExtractionProgress: TrackExtractionProgress,
    private val generateUserCollage: GenerateUserCollage,
    private val compileTextAlbums: CompileTextAlbums,
    private val navigators: Navigators
) {
    private val recommendation = flowOf(compileTextAlbums)
        .map { it.invoke(5) }
        .map {
            val userCollages = generateUserCollage.invoke()
                .map { user -> ExtractionCollage(user.userEmbed, user.extractions) }
                .toList()
            userCollages.plus(it)
        }
    private val progress = trackExtractionProgress.invoke()

    private val eventBus = Channel<ShareImagesEvent>()
    val events = eventBus.receiveAsFlow()

    val overviewGridState = OverviewGridState()

    val state = combine(
        recommendation,
        progress
    ) { content, progress -> transformState(content, progress) }
        .stateIn(
            coroutineScope,
            WhileUiSubscribed,
            RecommendedSearchesState.Loading
        )

    fun multiselectBarEventHandler(event: MultiselectAction) {
        when (event) {
            MultiselectAction.Delete -> Unit
            // TODO
            MultiselectAction.CreateAlbum -> Unit

            MultiselectAction.Cancel -> overviewGridState.gridState.clearSelection()

            MultiselectAction.Share -> coroutineScope.launch {
                withContext(Dispatchers.Default) {
                    val indices = overviewGridState.gridState.checkedKeys()
                        .mapNotNull { state.value.getImageUris()[it] }
                    eventBus.send(ShareImagesEvent(indices))
                }
            }
        }
    }

    private fun transformState(
        content: List<ExtractionCollage>,
        progress: ExtractionStatus
    ): RecommendedSearchesState {
        return when (progress) {
            is ExtractionStatus.Running -> RecommendedSearchesState.SyncInProgress(progress = progress.percentage)
            is ExtractionStatus.Done -> when {
                content.isNotEmpty() -> RecommendedSearchesState.Content(
                    items = content,
                    onImageClick = ::handleImageClickEvent
                )

                else -> RecommendedSearchesState.Empty
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
}