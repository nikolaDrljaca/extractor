package com.drbrosdev.extractor

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.bumble.appyx.navigation.integration.NodeHost
import com.drbrosdev.extractor.ui.components.TransparentSystemBars
import com.drbrosdev.extractor.ui.onboarding.OnboardingNode
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

class MainActivity : ComponentNodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ExtractorTheme(dynamicColor = false) {
                TransparentSystemBars()

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NodeHost(
                        integrationPoint = appyxV2IntegrationPoint,
                    ) {
//                    RootNode(it)
                        OnboardingNode(it)
                    }
                }
            }
        }
    }
}

@Composable
fun RequestPermissionScreen(
    onRequestPermission: () -> Unit,
    onOpenAppSettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            12.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            onRequestPermission()
        }) {
            Text(text = "Request Image permission")
        }

        Button(onClick = onOpenAppSettings) {
            Text(text = "Open App Settings")
        }
    }
}

//TODO Move this under ActivityExt file
fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also { startActivity(it) }
}

//TODO Move this to ContextExt file, under utils or extensions
fun Context.checkAndRequestPermission(
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val result = ContextCompat.checkSelfPermission(this, permission)
    if (result != PackageManager.PERMISSION_GRANTED) {
        launcher.launch(permission)
    }
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    error("No activity found.")
}