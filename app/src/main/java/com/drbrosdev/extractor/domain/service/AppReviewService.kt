package com.drbrosdev.extractor.domain.service

interface AppReviewService {

    suspend fun requestReview(): Result<Unit>

}
