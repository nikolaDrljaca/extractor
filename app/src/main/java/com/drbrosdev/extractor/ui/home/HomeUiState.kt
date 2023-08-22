package com.drbrosdev.extractor.ui.home

import com.drbrosdev.extractor.domain.model.MediaImage

data class SyncStatus(
    val localCount: Int = 0,
    val deviceCount: Int = 0
)

data class HomeUiState(
    val syncStatus: SyncStatus = SyncStatus(),
    val images: List<MediaImage> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface HomeScreenEvents {
    data class PerformSearch(val query: String) : HomeScreenEvents
    data object RunExtraction: HomeScreenEvents

}
