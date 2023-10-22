package com.drbrosdev.extractor.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.findActivity
import com.drbrosdev.extractor.openAppSettings
import com.drbrosdev.extractor.ui.components.shared.ExtractorActionButton
import com.drbrosdev.extractor.ui.components.shared.OnboardingCard
import com.drbrosdev.extractor.ui.components.shared.OnboardingCardHeadline
import com.drbrosdev.extractor.ui.components.shared.OutlinedExtractorActionButton
import com.drbrosdev.extractor.ui.onboarding.worker.StartWorkerOnbCard
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize

@Parcelize
object PermissionOnbCard : OnbNavTarget {

    @Composable
    override fun Content() {
        val navController = LocalOnbNavController.current
        val imagePermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) navController.navigate(StartWorkerOnbCard)
            }
        )
        val activity = LocalContext.current.findActivity()

        OnboardingCard(
            body = stringResource(id = R.string.lorem),
            headline = {
                OnboardingCardHeadline(
                    headline = "Permission",
                    onBack = { navController.pop() }
                )
            },
            actionButton = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        4.dp,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    OutlinedExtractorActionButton(onClick = {
                        activity.openAppSettings()
                    }) {
                        Text(text = "Open Settings")
                    }

                    ExtractorActionButton(onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            imagePermissionResultLauncher.launch(
                                Manifest.permission.READ_MEDIA_IMAGES
                            )
                        } else {
                            imagePermissionResultLauncher.launch(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        }
                        //do something
                    }) {
                        Text(text = "Grant Permission")
                    }
                }
            }
        )
    }
}