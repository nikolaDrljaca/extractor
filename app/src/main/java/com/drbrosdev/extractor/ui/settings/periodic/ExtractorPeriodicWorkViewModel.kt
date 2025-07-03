package com.drbrosdev.extractor.ui.settings.periodic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.drbrosdev.extractor.domain.service.ExtractorWorkerService
import com.drbrosdev.extractor.framework.workmanager.ExtractorWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.TimeUnit

/*
NOTE: Should migrate this code to the workerService
start and observeWork
 */
class ExtractorPeriodicWorkViewModel(
    private val workManager: WorkManager
) : ViewModel() {

    private val periodicExtraction =
        PeriodicWorkRequestBuilder<ExtractorWorker>(1, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()

    val doesPeriodicWorkExist =
        workManager.getWorkInfosForUniqueWorkLiveData(ExtractorWorkerService.EXTRACTOR_PERIODIC)
            .asFlow()
            .map { workInfo ->
                when {
                    workInfo.isEmpty() -> false

                    workInfo
                        .map { info -> info.state }
                        .all { it.isFinished } -> false

                    else -> true
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                false
            )

    fun onCheckedChange(value: Boolean) {
        if (value) {
            if (doesPeriodicWorkExist.value) return

            workManager.enqueueUniquePeriodicWork(
                ExtractorWorkerService.EXTRACTOR_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicExtraction
            )
        } else {
            workManager.cancelUniqueWork(ExtractorWorkerService.EXTRACTOR_PERIODIC)
        }
    }
}
