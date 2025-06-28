package com.drbrosdev.extractor.ui.imageinfo.edit

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import com.drbrosdev.extractor.domain.model.AnnotationType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import com.drbrosdev.extractor.domain.repository.payload.EmbedUpdate
import com.drbrosdev.extractor.domain.usecase.suggestion.SuggestUserKeywords
import com.drbrosdev.extractor.ui.imageinfo.LupaImageHeaderState
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class EditLupaAnnotationsViewModel(
    private val mediaImageId: Long,
    private val stateHandle: SavedStateHandle,
    private val annotationType: AnnotationType,
    private val lupaImageRepository: LupaImageRepository,
    private val suggestUserKeywords: SuggestUserKeywords
) : ViewModel() {
    private val imageId = MediaImageId(mediaImageId)

    private val lupaImage = lupaImageRepository.findByIdAsFlow(MediaImageId(mediaImageId))
        .stateIn(
            scope = viewModelScope,
            WhileUiSubscribed,
            null
        )

    val headerState = lupaImage
        .map { it?.let { LupaImageHeaderState.fromMetadata(it.metadata) } }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            null
        )

    val textAnnotationState = stateHandle.saveable(
        key = "text_annotation_state",
        saver = TextFieldState.Saver,
        init = { TextFieldState("") }
    )
    private val job = lupaImage
        .filterNotNull()
        .map { it.annotations.textEmbed }
        .take(1)
        .onEach { textAnnotationState.setTextAndPlaceCursorAtEnd(it) }
        .launchIn(viewModelScope)
    private val updateJob = snapshotFlow { textAnnotationState.text }
        .debounce(300L)
        .onEach {
            lupaImageRepository.updateTextEmbed(
                EmbedUpdate(
                    mediaImageId = imageId,
                    value = it.toString()
                        .trim()
                )
            )
        }
        .flowOn(Dispatchers.Default)
        .launchIn(viewModelScope)

    val visualAnnotationState = lupaImage
        .map { it?.annotations?.visualEmbeds ?: emptyList() }
        .map {
            VisualAnnotationsState(
                keywords = it,
                onDelete = ::deleteVisualEmbed
            )
        }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            VisualAnnotationsState(emptyList(), {})
        )

    val userEditComponent by lazy {
        UserAnnotationsEditorComponentImpl(
            scope = viewModelScope,
            stateHandle = stateHandle,
            getSuggestions = { suggestUserKeywords.execute(imageId) },
            annotationsFlow = { lupaImage },
            createNewKeyword = ::createNewUserKeyword,
            deleteKeyword = ::deleteUserEmbed
        )
    }

    private fun deleteVisualEmbed(value: String) {
        viewModelScope.launch {
            lupaImageRepository.deleteVisualEmbed(imageId, value)
        }
    }

    private fun deleteUserEmbed(value: String) {
        viewModelScope.launch {
            lupaImageRepository.deleteUserEmbed(imageId, value)
        }
    }

    private fun createNewUserKeyword(value: String) {
        viewModelScope.launch {
            // fetch existing
            val id = MediaImageId(mediaImageId)
            val existing = lupaImageRepository.findImageDataByMediaId(id)
                .map { it?.userEmbeds }
                .first() ?: return@launch

            // parse input as CSV and map to new entries
            val newUserEmbed = value.split(",")
                .map { it.trim() }
                .distinct()

            // append to existing
            val updated = (newUserEmbed + existing)
                .map { EmbedUpdate(id, it) }

            // update
            lupaImageRepository.updateUserEmbed(updated)
        }
    }
}