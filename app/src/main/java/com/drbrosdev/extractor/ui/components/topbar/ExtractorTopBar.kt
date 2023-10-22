package com.drbrosdev.extractor.ui.components.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import androidx.work.await
import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.worker.WorkNames
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorStatusButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class ExtractorTopBarViewModel(
    private val mediaImageRepository: MediaImageRepository,
    private val extractionEntityDao: ExtractionEntityDao,
    private val workManager: WorkManager
) : ViewModel() {
    val percentageDoneFlow = flow {
        while (true) {
            //TODO Will need more logic once we have a coroutine based extraction on demand
            val isWorking = workManager.getWorkInfosForUniqueWork(WorkNames.EXTRACTOR_WORK).await()
            if (isWorking.isEmpty()) {
                emit(null)
                break
            }

            if (isWorking.first().state.isFinished) {
                emit(null)
                break
            }

            val onDevice = mediaImageRepository.getCount().toDouble()
            val inStorage = extractionEntityDao.getCount().toDouble()
            val percentage = inStorage.div(onDevice).times(100).toInt()
            emit(percentage)
            delay(3000L)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
}

sealed interface ExtractorTopBarEvents {
    data object OnExtractorButtonClicked : ExtractorTopBarEvents

    data object OnAboutClicked : ExtractorTopBarEvents
}

@Composable
fun ExtractorTopBar(
    onEvent: (ExtractorTopBarEvents) -> Unit,
    modifier: Modifier = Modifier,
    donePercentage: Int?,
) {
    Row(
        modifier = Modifier
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ExtractorStatusButton(
            onClick = { onEvent(ExtractorTopBarEvents.OnExtractorButtonClicked) },
            state = if (donePercentage == null)
                ExtractorStatusButtonState.IDLE else ExtractorStatusButtonState.WORKING,
            donePercentage = donePercentage
        )

        ExtractorHeader()

        IconButton(
            onClick = { onEvent(ExtractorTopBarEvents.OnAboutClicked) }
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "About App",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Column(modifier = Modifier.fillMaxWidth()) {
            ExtractorTopBar(onEvent = {}, donePercentage = null)
            ExtractorTopBar(onEvent = {}, donePercentage = 34)
        }
    }
}
