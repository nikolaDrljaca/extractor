package com.drbrosdev.extractor

import androidx.room.withTransaction
import com.drbrosdev.extractor.data.ExtractorDatabase

class TransactionProvider(
    private val database: ExtractorDatabase
) {

    suspend fun <T> runTransaction(block: suspend () -> T): T {
        return database.withTransaction(block)
    }
}


