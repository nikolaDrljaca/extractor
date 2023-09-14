package com.drbrosdev.extractor.ui.home

import com.drbrosdev.extractor.data.entity.PreviousSearchEntity


data class HomeUiState(
    val searches: List<PreviousSearchEntity> = emptyList(),
)

sealed interface HomeScreenEvents {

    data class PerformSearch(val query: String) : HomeScreenEvents
    data class OnDeleteSearch(val value: PreviousSearchEntity) : HomeScreenEvents

    data object OnNavToAbout: HomeScreenEvents

}
