package com.drbrosdev.extractor.ui.dialog.userembed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.payload.EmbedUpdate
import com.drbrosdev.extractor.domain.usecase.suggestion.SuggestUserKeywords
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextFieldState
import com.drbrosdev.extractor.ui.imageinfo.UserEmbedUiModel
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorUserEmbedViewModel(
    private val mediaImageId: Long,
    private val stateHandle: SavedStateHandle,
    private val extractorRepository: ExtractorRepository,
    private val suggestUserKeywords: SuggestUserKeywords
) : ViewModel() {

    val embedTextFieldState = stateHandle.saveable(
        key = "embed_user",
        saver = ExtractorTextFieldState.Saver
    ) {
        ExtractorTextFieldState()
    }

    private val checkedUserEmbeds = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    private val _events = Channel<ExtractorUserEmbedDialogEvents>()
    val events = _events.receiveAsFlow()

    private val _suggested = flow {
        emit(suggestUserKeywords.invoke())
    }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val suggestedEmbeddingsState = combine(
        checkedUserEmbeds,
        _suggested
    ) { checked, suggested ->
        when {
            suggested.isEmpty() -> ExtractorSuggestedEmbedsUiState.Empty
            else -> ExtractorSuggestedEmbedsUiState.Content(
                suggestions = suggested.map {
                    UserEmbedUiModel(
                        text = it.value,
                        isChecked = checked.getOrDefault(it.value, false)
                    )
                }
            )
        }
    }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            ExtractorSuggestedEmbedsUiState.Loading
        )

    fun createNewUserEmbed() {
        viewModelScope.launch {
            // fetch existing
            val id = MediaImageId(mediaImageId)
            val existing = extractorRepository.findImageDataByMediaId(id)
                .map { it?.userEmbeds }
                .first() ?: return@launch

            // parse input as CSV and map to new entries
            val newUserEmbed = embedTextFieldState.textValue.split(",")
                .map { it.trim() }
                .distinct()
                .map { Embed.User(it) }

            // append to existing
            val updated = (newUserEmbed + existing)
                .map { EmbedUpdate(id, it.value) }

            // update
            extractorRepository.updateUserEmbed(updated)

            // clear text field
            embedTextFieldState.updateTextValue("")
        }
    }

    fun checkEmbedding(embed: String) {
        checkedUserEmbeds.update {
            val current = it[embed] ?: false
            val out = mapOf(embed to current.not())
            it + out
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            // fetch existing
            val id = MediaImageId(mediaImageId)
            val existing = extractorRepository.findImageDataByMediaId(id)
                .map { it?.userEmbeds }
                .first() ?: return@launch

            val newUserEmbeds = suggestedEmbeddingsState.value.getSuggestions()
                .filter { it.isChecked }
                .map { Embed.User(it.text) }
            val updated = (newUserEmbeds + existing)
                .map { EmbedUpdate(id, it.value) }

            extractorRepository.updateUserEmbed(updated)
            _events.send(ExtractorUserEmbedDialogEvents.ChangesSaved)
        }
    }
}