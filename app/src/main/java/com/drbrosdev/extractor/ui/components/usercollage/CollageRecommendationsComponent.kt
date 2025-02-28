package com.drbrosdev.extractor.ui.components.usercollage

import com.drbrosdev.extractor.domain.usecase.GenerateUserCollage
import com.drbrosdev.extractor.domain.usecase.album.CompileTextAlbums
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList

class CollageRecommendationsComponent(
    private val coroutineScope: CoroutineScope,
    private val generateUserCollage: GenerateUserCollage,
    private val compileTextAlbums: CompileTextAlbums
) {
    val state = flowOf(compileTextAlbums)
        .map { it.invoke(5) }
        .map {
            val userCollages = generateUserCollage.invoke()
                .map { user -> ExtractionCollage(user.userEmbed, user.extractions) }
                .toList()
            userCollages.plus(it)
        }
        .map {
            when {
                it.isEmpty() -> CollageRecommendationState.Empty
                else -> CollageRecommendationState.Content(it)
            }
        }
        .stateIn(
            coroutineScope,
            WhileUiSubscribed,
            CollageRecommendationState.Loading
        )


}