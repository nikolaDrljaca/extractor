package com.drbrosdev.extractor.ui.imageviewer

import android.net.Uri
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExtractorImageViewerModel(
    private val mediaStoreImageRepository: MediaStoreImageRepository,
    private val lupaImageRepository: LupaImageRepository,
    private val images: List<Uri>,
    private val initialIndex: Int
) : ViewModel() {

    val pagerState = PagerState(currentPage = initialIndex) { images.size }

    val mediaImageInfo = snapshotFlow { pagerState.currentPage }
        .map { images.getOrNull(it) }
        .filterNotNull()
        .map { mediaStoreImageRepository.findByUri(it) }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            null
        )

    val annotations = snapshotFlow { pagerState.currentPage }
        .map { images.getOrNull(it) }
        .filterNotNull()
        .map {
            val uri = MediaImageUri(it.toString())
            val description = lupaImageRepository.findImageByUri(uri)
                ?.annotations
                // TODO Replace this with descriptions
                ?.textEmbed
            description?.ifBlank { null }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            null
        )

    private val eventChannel = Channel<ExtractorImageViewerEvents>()
    val events = eventChannel.receiveAsFlow()
        .debounce(200L)

    fun processEvent(item: ExtractorBottomBarItem) {
        viewModelScope.launch {
            eventChannel.send(item.toEvent())
        }
    }
}