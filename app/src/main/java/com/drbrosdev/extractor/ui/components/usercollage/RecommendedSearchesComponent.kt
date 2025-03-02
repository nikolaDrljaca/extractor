package com.drbrosdev.extractor.ui.components.usercollage

import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.usecase.GenerateUserCollage
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.album.CompileTextAlbums
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList

class RecommendedSearchesComponent(
    private val coroutineScope: CoroutineScope,
    private val trackExtractionProgress: TrackExtractionProgress,
    private val generateUserCollage: GenerateUserCollage,
    private val compileTextAlbums: CompileTextAlbums,
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

    val state = combine(
        recommendation,
        progress
    ) { content, progress -> transformState(content, progress) }
        .stateIn(
            coroutineScope,
            WhileUiSubscribed,
            RecommendedSearchesState.Loading
        )

    private fun transformState(
        content: List<ExtractionCollage>,
        progress: ExtractionStatus
    ): RecommendedSearchesState {
        return when (progress) {
            is ExtractionStatus.Running -> RecommendedSearchesState.SyncInProgress(progress = progress.percentage)
            is ExtractionStatus.Done -> when {
                content.isNotEmpty() -> RecommendedSearchesState.Content(
                    items = content,
                    onImageClick = { keyword, index -> }
                )

                else -> RecommendedSearchesState.Empty
            }
        }
    }
}