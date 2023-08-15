package com.drbrosdev.extractor

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.await
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.usecase.Extractor
import com.drbrosdev.extractor.domain.usecase.ImageSearch
import com.drbrosdev.extractor.domain.worker.ExtractorWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val workManager: WorkManager,
    private val extractor: Extractor,
    private val imageSearch: ImageSearch
) : ViewModel() {
    var permissionGranted = mutableStateOf(false)
        private set

    private val _imageDescFlow = MutableStateFlow<List<MediaImage>>(emptyList())
    val images = _imageDescFlow.asStateFlow()

    val isWorkRunning = workManager
        .getWorkInfosForUniqueWorkLiveData("extractor_work")
        .asFlow()
        .map { it.any { workInfo -> !workInfo.state.isFinished } }

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

        workManager.enqueueUniqueWork(
            "extractor_work",
            ExistingWorkPolicy.KEEP,
            extractorWorkRequest
        )
    }

    fun performSearch(term: String) {
//        _imageDescFlow.update {
//            it.filter { item -> item.labels.contains(term) }.copy()
//        }
        viewModelScope.launch {
            val out = imageSearch.execute(term)
            _imageDescFlow.update {
                out
            }
        }
    }

    private fun <T> List<T>.copy(): List<T> {
        val inner = mutableListOf<T>()
        inner.addAll(this)
        return inner
    }
}