package com.drbrosdev.extractor.ui.imageinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorImageInfoViewModel(
    private val mediaImageId: Long,
    private val mediaImageRepository: MediaImageRepository,
    private val extractorRepository: ExtractorRepository
) : ViewModel() {
    private val checkedVisualEmbeds = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    val imageInfoModel = extractorRepository
        .findImageDataByMediaId(mediaImageId = mediaImageId)
        .filterNotNull()
        .map { it.mapToInfoModel() }
        .combine(checkedVisualEmbeds) { imageInfoUiModel, checkedEmbeds ->
            ImageInfoUiModel(
                mediaImageId = imageInfoUiModel.mediaImageId,
                userEmbedding = imageInfoUiModel.userEmbedding,
                textEmbedding = imageInfoUiModel.textEmbedding,
                visualEmbedding = imageInfoUiModel.visualEmbedding.map {
                    VisualEmbedUiModel(
                        text = it.text,
                        isChecked = checkedEmbeds.getOrDefault(it.text, false)
                    )
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ImageInfoUiModel())

    fun clearVisualEmbedding(embedding: String) {
        checkedVisualEmbeds.update {
            val current = it.getOrDefault(embedding, false)
            val out = mapOf(embedding to current.not())
            it + out
        }
    }

    fun saveEmbeddings() {
        viewModelScope.launch {
            extractorRepository.updateTextEmbed(
                value = imageInfoModel.value.embeddingsFormState.textEmbedding.trim(),
                imageEntityId = mediaImageId
            )

            extractorRepository.updateUserEmbed(
                value = imageInfoModel.value.embeddingsFormState.userEmbedding.trim(),
                imageEntityId = mediaImageId
            )

            imageInfoModel.value.visualEmbedding
                .filter { it.isChecked }
                .forEach { extractorRepository.deleteVisualEmbedding(it.text) }
        }
    }
}