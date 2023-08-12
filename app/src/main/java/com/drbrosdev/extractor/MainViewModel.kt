package com.drbrosdev.extractor

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.usecase.Extractor
import com.drbrosdev.extractor.domain.worker.ExtractorWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkRunner(private val context: Context) {

    private val manager = WorkManager.getInstance(context)

    fun run() {
        val extractorWorkRequest = OneTimeWorkRequestBuilder<ExtractorWorker>()
            .build()
        manager.enqueue(extractorWorkRequest)
    }
}

class MainViewModel(
    private val workManager: WorkManager,
    private val extractor: Extractor
) : ViewModel() {
    var permissionGranted = mutableStateOf(false)
        private set

    private val _imageDescFlow = MutableStateFlow<List<ImageDescription>>(emptyList())
    val images = _imageDescFlow.asStateFlow()

    fun onPermissionResult(permission: String, isGranted: Boolean) {
        permissionGranted.value = isGranted
    }

    fun runExtraction(mediaImage: MediaImage) {
        viewModelScope.launch {
            println("--- Running extraction on single image")
            extractor.execute(mediaImage)
            println("--- Finished")
        }
    }

    fun spawnWorkRequest() {
        val extractorWorkRequest = OneTimeWorkRequestBuilder<ExtractorWorker>()
            .build()
        workManager.enqueue(extractorWorkRequest)
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