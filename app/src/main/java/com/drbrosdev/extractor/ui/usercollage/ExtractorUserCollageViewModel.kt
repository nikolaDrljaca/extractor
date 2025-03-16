package com.drbrosdev.extractor.ui.usercollage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.domain.usecase.GenerateUserCollage
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExtractorUserCollageViewModel(
    private val stateHandle: SavedStateHandle,
    private val datastore: ExtractorDataStore,
    private val generateUserCollage: GenerateUserCollage
) : ViewModel() {

    private val _events = Channel<ExtractorUserCollageEvents>()
    val events = _events.receiveAsFlow()

    val showBanner = datastore.showYourKeywordsBanner
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false
        )

    val userCollageState = flow {
        emit(ExtractorUserCollageUiState.Loading)

        emit(
            ExtractorUserCollageUiState.Content(
                collages = generateUserCollage.invoke().toList(),
                onItemClicked = { keyword, index ->
                    onImageItemClicked(keyword, index)
                },
                onShare = ::onShare
            )
        )
    }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            ExtractorUserCollageUiState.Loading
        )

    fun hideYourKeywordsBanner() {
        viewModelScope.launch {
            datastore.hasSeenYourKeywordsBanner()
        }
    }

    private fun onImageItemClicked(keyword: String, index: Int) {
        viewModelScope.launch {
            val collage = withContext(Dispatchers.Default) {
                userCollageState.value.findCollageByKeyword(keyword)
            }

            collage?.let {
                val uris = it.extractions.map { extraction -> extraction.uri.toUri() }

                _events.send(
                    ExtractorUserCollageEvents.NavToImageViewer(
                        index,
                        uris
                    )
                )
            }
        }
    }

    private fun onShare(keyword: String) {
        viewModelScope.launch {
            val collage = withContext(Dispatchers.Default) {
                userCollageState.value.findCollageByKeyword(keyword)
            }

            collage?.let {
                val uris = it.extractions.map { extraction -> extraction.uri.toUri() }
                _events.send(
                    ExtractorUserCollageEvents.ShareCollage(
                        images = uris
                    )
                )
            }
        }
    }

}