package com.drbrosdev.extractor.data

import androidx.room.withTransaction

class TransactionProvider(
    private val database: ExtractorDatabase
) {
    suspend fun <T> withTransaction(block: suspend () -> T): T {
        return database.withTransaction(block)
    }
}


