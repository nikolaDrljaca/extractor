package com.drbrosdev.extractor.ui.extractorimageinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.entity.ImageDataWithEmbeddings
import com.drbrosdev.extractor.data.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.ui.components.embeddingsform.EmbeddingsFormState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExtractorImageInfoViewModel(
    private val mediaImageId: Long,
    private val mediaImageRepository: MediaImageRepository,
    private val extractorRepository: ExtractorRepository
) : ViewModel() {

    val imageInfoModel = extractorRepository
        .findImageDataByMediaId(mediaImageId = mediaImageId)
        .filterNotNull()
        .map { it.mapToInfoModel() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ImageInfoUiModel())


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
        }
    }
}

data class ImageInfoUiModel(
    val mediaImageId: Long = 0L,
    val userEmbedding: String? = null,
    val textEmbedding: String = "",
    val visualEmbedding: List<String> = emptyList(),
) {
    val embeddingsFormState = EmbeddingsFormState.create(textEmbedding, userEmbedding ?: "")
}

fun ImageDataWithEmbeddings.mapToInfoModel(): ImageInfoUiModel {
    return ImageInfoUiModel(
        mediaImageId = this.imageEntity.mediaStoreId,
        userEmbedding = this.userEmbedding?.value,
        textEmbedding = this.textEmbedding.value,
        visualEmbedding = this.visualEmbeddings.map { it.value },
    )
}