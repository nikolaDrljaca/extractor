package com.drbrosdev.extractor.ui.imageviewer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.MediaStoreImage
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorImageViewerModel(
    private val mediaStoreImageRepository: MediaStoreImageRepository
) : ViewModel() {

    private val _currentMediaImage = MutableStateFlow<MediaStoreImage?>(null)
    val currentMediaImageInfo = _currentMediaImage.asStateFlow()

    private val eventChannel = Channel<ExtractorImageViewerEvents>()
    val events = eventChannel.receiveAsFlow()
        .debounce(200L)

    fun loadImageDetails(uri: Uri) {
        viewModelScope.launch {
            _currentMediaImage.update {
                mediaStoreImageRepository.findByUri(uri)
            }
        }
    }

    fun processEvent(item: ExtractorBottomBarItem) {
        viewModelScope.launch {
            eventChannel.send(item.toEvent())
        }
    }
}