package com.drbrosdev.extractor.ui.imageinfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import com.drbrosdev.extractor.domain.repository.payload.EmbedUpdate
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextFieldState
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorImageInfoViewModel(
    private val mediaImageId: Long,
    private val stateHandle: SavedStateHandle,
    private val extractorDataRepository: LupaImageRepository
) : ViewModel() {
    private val checkedVisualEmbeds = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    private val checkedUserEmbeds = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    val textEmbedding = stateHandle.saveable(
        key = "text_embedding",
        saver = ExtractorTextFieldState.Saver
    ) {
        ExtractorTextFieldState()
    }

    val imageInfoModel = combine(
        extractorDataRepository.findImageDataByMediaId(mediaImageId = MediaImageId(mediaImageId))
            .filterNotNull(),
        checkedVisualEmbeds,
        checkedUserEmbeds
    ) { imageEmbeds, visualChecked, userChecked ->
        // set the text state
        textEmbedding.updateTextValue(imageEmbeds.textEmbed)

        ExtractorImageInfoUiState(
            mediaImageId = MediaImageId(mediaImageId),
            userEmbedding = imageEmbeds.userEmbeds.map {
                UserEmbedUiModel(
                    text = it,
                    isChecked = userChecked.getOrDefault(it, false)
                )
            },
            visualEmbedding = imageEmbeds.visualEmbeds.map {
                VisualEmbedUiModel(
                    text = it,
                    isChecked = visualChecked.getOrDefault(it, false),
                )
            }
        )
    }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            ExtractorImageInfoUiState()
        )

    fun clearVisualEmbedding(embedding: String) {
        checkedVisualEmbeds.update {
            val current = it.getOrDefault(embedding, false)
            val out = mapOf(embedding to current.not())
            it + out
        }
    }

    // Will run on click of the already existing(added) embeds
    fun updateUserEmbedding(embedding: String) {
        checkedUserEmbeds.update {
            val current = it[embedding] ?: false
            val out = mapOf(embedding to current.not())
            it + out
        }
    }

    fun saveEmbeddings() {
        viewModelScope.launch {
            val id = MediaImageId(mediaImageId)

            extractorDataRepository.updateTextEmbed(
                EmbedUpdate(
                    value = textEmbedding.textValue.trim(),
                    mediaImageId = id
                )
            )

            imageInfoModel.value.userEmbedding
                .filter { it.isChecked }
                .forEach { extractorDataRepository.deleteUserEmbed(id, it.text) }

            imageInfoModel.value.visualEmbedding
                .filter { it.isChecked }
                .forEach { extractorDataRepository.deleteVisualEmbed(id, it.text) }
        }
    }
}