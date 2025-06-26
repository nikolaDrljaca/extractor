package com.drbrosdev.extractor.ui.imageinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.AnnotationType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.imageinfo.edit.EditLupaAnnotationsNavTarget
import com.drbrosdev.extractor.util.WhileUiSubscribed
import com.drbrosdev.extractor.util.asFormatDate
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExtractorImageInfoViewModel(
    private val mediaImageId: Long,
    private val navigators: Navigators,
    private val lupaImageRepository: LupaImageRepository
) : ViewModel() {

    private val navController = navigators.navController

    val imageDetailState = lupaImageRepository.findByIdAsFlow(MediaImageId(mediaImageId))
        .filterNotNull()
        .map { lupaImage ->
            LupaImageInfoState(
                heading = LupaImageHeaderState(
                    mediaImageId = lupaImage.metadata.mediaImageId.id,
                    uri = lupaImage.metadata.uri.uri,
                    dateAdded = lupaImage.metadata.dateAdded.asFormatDate()
                ),
                description = when {
                    lupaImage.annotations.descriptionEmbed.isBlank() -> null
                    else -> lupaImage.annotations.descriptionEmbed
                },
                editables = LupaImageEditablesState(
                    textEmbed = lupaImage.annotations.textEmbed,
                    visualEmbeds = LupaImageAnnotationsState(lupaImage.annotations.visualEmbeds),
                    userEmbeds = LupaImageAnnotationsState(lupaImage.annotations.userEmbeds),
                    eventSink = ::handleEditablesEvent
                )
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            null
        )

    private fun handleEditablesEvent(event: LupaImageEditablesEvents) {
        when (event) {
            LupaImageEditablesEvents.OnTextEdit -> Unit
            LupaImageEditablesEvents.OnUserEdit -> Unit
            LupaImageEditablesEvents.OnVisualEdit -> navController.navigate(
                EditLupaAnnotationsNavTarget(
                    mediaImageId = mediaImageId,
                    type = AnnotationType.VISUAL
                )
            )
        }
    }
}