package com.drbrosdev.extractor.ui.image

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.MediaImageInfo
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImageDetailViewModel(
    private val mediaImageRepository: MediaImageRepository
): ViewModel() {

    private val _state = MutableStateFlow<MediaImageInfo?>(null)
    val state = _state.asStateFlow()

    fun loadImageDetails(uri: Uri) {
        viewModelScope.launch {
            _state.update { mediaImageRepository.findByUri(uri) }
        }
    }

}