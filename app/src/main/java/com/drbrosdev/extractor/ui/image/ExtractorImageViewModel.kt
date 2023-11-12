package com.drbrosdev.extractor.ui.image

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorImageViewModel(
    private val mediaImageRepository: MediaImageRepository,
) : ViewModel() {

    private val _currentMediaImage = MutableStateFlow<MediaImage?>(null)
    val currentMediaImageInfo = _currentMediaImage.asStateFlow()

    private val eventChannel = Channel<ExtractorImageEvents>()
    val events = eventChannel.receiveAsFlow()
        .debounce(200L)

    fun loadImageDetails(uri: Uri) {
        viewModelScope.launch {
            _currentMediaImage.update { mediaImageRepository.findByUri(uri) }
        }
    }

    fun processEvent(item: ExtractorBottomBarItem) {
        viewModelScope.launch {
            eventChannel.send(item.toEvent())
        }
    }
}