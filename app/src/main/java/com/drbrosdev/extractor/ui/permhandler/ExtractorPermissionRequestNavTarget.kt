package com.drbrosdev.extractor.ui.permhandler

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.findActivity
import com.drbrosdev.extractor.util.openAppSettings
import dev.olshevski.navigation.reimagined.replaceAll
import kotlinx.parcelize.Parcelize


@Parcelize
object ExtractorPermissionRequestNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val activity = LocalContext.current.findActivity()
        val navController = LocalNavController.current

        val imagePermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    navController.replaceAll(ExtractorSearchNavTarget())
                }
            }
        )

        ExtractorPermissionRequestScreen(
            onRequestPermission = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    imagePermissionResultLauncher.launch(
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                } else {
                    imagePermissionResultLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            },
            onOpenSettings = {
                activity.openAppSettings()
            }
        )
    }
}