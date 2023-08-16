package com.drbrosdev.extractor.util

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.bumble.appyx.navigation.integrationpoint.ActivityIntegrationPoint
import com.bumble.appyx.navigation.integrationpoint.IntegrationPointProvider

open class ComponentNodeActivity : ComponentActivity(), IntegrationPointProvider {
    override lateinit var appyxV2IntegrationPoint: ActivityIntegrationPoint
        protected set

    protected open fun createIntegrationPoint(savedInstanceState: Bundle?) =
        ActivityIntegrationPoint(
            activity = this,
            savedInstanceState = savedInstanceState
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appyxV2IntegrationPoint = createIntegrationPoint(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appyxV2IntegrationPoint.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        appyxV2IntegrationPoint.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        appyxV2IntegrationPoint.onSaveInstanceState(outState)
    }

}
