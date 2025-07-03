package com.drbrosdev.extractor.framework

import android.app.Activity
import com.drbrosdev.extractor.util.UseWithCare
import java.lang.ref.WeakReference


/**
 * USE WITH EXTREME CARE!
 *
 * Avoid using in random places to avoid random NPEs and memory leaks.
 */
@UseWithCare
class ActivityProvider {
    private var activityRef: WeakReference<Activity>? = null

    fun setActivity(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    fun getActivity() = activityRef?.get()
}