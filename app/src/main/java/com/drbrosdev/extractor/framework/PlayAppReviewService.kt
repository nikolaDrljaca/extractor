package com.drbrosdev.extractor.framework

import android.content.Context
import arrow.core.raise.result
import com.drbrosdev.extractor.BuildConfig
import com.drbrosdev.extractor.domain.service.AppReviewService
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import kotlinx.coroutines.tasks.await

class PlayAppReviewService(
    context: Context,
    private val activityProvider: ActivityProvider
) : AppReviewService {
    private val manager by lazy {
        when {
            // for debug(local development) builds use fake
            BuildConfig.DEBUG -> FakeReviewManager(context)

            // otherwise use real reviewManager
            else -> ReviewManagerFactory.create(context)
        }
    }

    override suspend fun requestReview() = result {
        // try to find the activity
        val activity = activityProvider.getActivity()!!
        // create reviewInfo object - makes API call
        val reviewInfo = manager.requestReviewFlow()
            .await()
        // launch review flow
        manager.launchReviewFlow(activity, reviewInfo).await()
        // mask return type
        Unit
    }
}