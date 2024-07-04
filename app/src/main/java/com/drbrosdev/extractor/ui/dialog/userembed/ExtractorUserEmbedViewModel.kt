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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _events = Channel<ExtractorUserEmbedDialogEvents>()
    val events = _events.receiveAsFlow()

    private val _suggestedJob = flow {
        emit(suggestUserKeywords.invoke())
    }
        .onEach { embeds ->
            _suggestedEmbeddings.update {
                when {
                    embeds.isEmpty() -> ExtractorSuggestedEmbedsUiState.Empty
                    else -> ExtractorSuggestedEmbedsUiState.Content(
                        suggestions = embeds.map {
                            UserEmbedUiModel(
                                text = it.value,
                                isChecked = false
                            )
                        }
                    )
                }
            }
        }
        .launchIn(viewModelScope)

    private val _suggestedEmbeddings = MutableStateFlow<ExtractorSuggestedEmbedsUiState>(
        ExtractorSuggestedEmbedsUiState.Loading
    )
    val suggestedEmbeddings = _suggestedEmbeddings.asStateFlow()

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
            _events.send(ExtractorUserEmbedDialogEvents.KeywordAdded)
        }
    }

    fun checkEmbedding(embed: String) {
        viewModelScope.launch {
            val id = MediaImageId(mediaImageId)

            val existing = extractorRepository.findImageDataByMediaId(id)
                .filterNotNull()
                .map { it.userEmbeds }
                .firstOrNull() ?: return@launch

            val newEmbed = Embed.User(embed)

            // if already existing, return out
            if (newEmbed in existing) return@launch

            // otherwise update
            val updated = (existing + newEmbed)
                .map { EmbedUpdate(id, it.value) }

            extractorRepository.updateUserEmbed(updated)

            // remove added embed from suggestions
            _suggestedEmbeddings.update {
                ExtractorSuggestedEmbedsUiState.Content(
                    it.getSuggestionsExcluding(embed)
                )
            }
            _events.send(ExtractorUserEmbedDialogEvents.KeywordAdded)
        }
    }
}