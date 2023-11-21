package com.drbrosdev.extractor.ui.components.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.domain.model.LabelType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class StatEmbed(val value: String, val type: LabelType)

sealed interface ExtractorStatsUiState {
    data object Loading : ExtractorStatsUiState

    data class View(val statEmbeds: List<StatEmbed>) : ExtractorStatsUiState
}

class ExtractorStatsViewModel(
    private val visualEmbedDao: VisualEmbeddingDao
) : ViewModel() {

    val state = visualEmbedDao.getMostUsed(amount = 7)
        .map {
            ExtractorStatsUiState.View(
                statEmbeds = it.map { out -> StatEmbed(out.value, LabelType.IMAGE) }
            )

        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ExtractorStatsUiState.Loading)

}