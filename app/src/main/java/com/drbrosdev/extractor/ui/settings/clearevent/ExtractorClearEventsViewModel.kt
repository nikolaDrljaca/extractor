package com.drbrosdev.extractor.ui.settings.clearevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.framework.logger.EventLogDao
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
NOTE: Exceptional case where abstraction over [EventLogDao] would
only create more boilerplate.
Furthermore the [EventLogDao] comes from the framework layer, so the ViewModel here
is not reaching into the data layer directly.
 */
class ExtractorClearEventsViewModel(
    private val eventDao: EventLogDao
) : ViewModel() {

    val eventCountState = eventDao.countAsFlow()
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            0
        )

    fun clearEventLogs() {
        viewModelScope.launch {
            eventDao.deleteAll()
        }
    }
}