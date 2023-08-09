package com.drbrosdev.extractor

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var permissionGranted = mutableStateOf(false)
        private set

    private val _imageDescFlow = MutableStateFlow<List<ImageDescription>>(emptyList())
    val images = _imageDescFlow.asStateFlow()

    fun onPermissionResult(permission: String, isGranted: Boolean) {
        permissionGranted.value = isGranted
    }

    fun runExtraction(uri: Uri, extractor: Extractor) {
        viewModelScope.launch {
            println("---Loading extraction...")
            val out = extractor.run(uri)
            println(out)
            println("---Finished")
        }
    }

    fun performSearch(term: String) {
        _imageDescFlow.update {
            it.filter { item -> item.labels.contains(term) }.copy()
        }
    }
    private fun <T> List<T>.copy(): List<T> {
        val inner = mutableListOf<T>()
        inner.addAll(this)
        return inner
    }

    fun runExtraction(extractor: PersistentExtractor) {
        viewModelScope.launch {
            println("---Loading extraction + persistence")
            val out = extractor.run()
            _imageDescFlow.update { out }
            println("---Finished")
        }
    }

}