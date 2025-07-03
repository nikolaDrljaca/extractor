package com.drbrosdev.extractor.framework.logger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.time.LocalDateTime

class DatabaseEventLogTree : Timber.Tree(), KoinComponent {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val logEntryDao: EventLogDao by inject()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        scope.launch {
            logEntryDao.insert(
                message = message,
                tag = tag ?: "",
                timestamp = LocalDateTime.now().toString(),
                localizedMessage = t?.localizedMessage ?: ""
            )
        }
    }

    // NOTE: Call during app teardown
    fun close() {
        scope.cancel()
    }
}
