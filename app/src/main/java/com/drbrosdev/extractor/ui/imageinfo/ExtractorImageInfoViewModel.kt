package com.drbrosdev.extractor.ui.imageinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.payload.EmbedUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorImageInfoViewModel(
    private val mediaImageId: Long,
    private val extractorDataRepository: ExtractorRepository
) : ViewModel() {
    private val checkedVisualEmbeds = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    private val checkedUserEmbeds = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    val imageInfoModel = extractorDataRepository
        .findImageDataByMediaId(mediaImageId = MediaImageId(mediaImageId))
        .filterNotNull()
        .combine(checkedVisualEmbeds) { imageInfoUiModel, checkedEmbeds ->
            ExtractorImageInfoUiState(
                mediaImageId = MediaImageId(mediaImageId),
                // TODO
                userEmbedding = imageInfoUiModel.userEmbeds.joinToString(separator = ",") { it.value },
                textEmbedding = imageInfoUiModel.textEmbed.value,
                visualEmbedding = imageInfoUiModel.visualEmbeds.map {
                    VisualEmbedUiModel(
                        text = it.value,
                        isChecked = checkedEmbeds.getOrDefault(it.value, false),
                    )
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ExtractorImageInfoUiState())

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
            extractorDataRepository.updateTextEmbed(
                EmbedUpdate(
                    value = imageInfoModel.value.embeddingsFormState.textEmbedding.trim(),
                    mediaImageId = MediaImageId(mediaImageId)
                )
            )

            // TODO Disabled as this update logic will be different
//            extractorDataRepository.upsertUserEmbed(
//                EmbedUpdate(
//                    value = imageInfoModel.value.embeddingsFormState.userEmbedding.trim(),
//                    mediaImageId = MediaImageId(mediaImageId)
//                )
//            )

            imageInfoModel.value.visualEmbedding
                .filter { it.isChecked }
                .forEach {
                    extractorDataRepository.deleteVisualEmbed(
                        MediaImageId(mediaImageId),
                        it.text
                    )
                }
        }
    }
}