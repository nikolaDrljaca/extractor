package com.drbrosdev.extractor.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.requiresApi
import com.drbrosdev.extractor.ui.components.shared.ExtractorActionButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextButton
import com.drbrosdev.extractor.ui.components.shared.OnboardingCard
import com.drbrosdev.extractor.ui.components.shared.OnboardingCardHeadline
import com.drbrosdev.extractor.ui.onboarding.worker.StartWorkerOnbCard
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.findActivity
import com.drbrosdev.extractor.util.openAppSettings
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

        PermissionCard(
            onPermissionClick = {
                requiresApi(
                    versionCode = Build.VERSION_CODES.TIRAMISU,
                    fallback = {
                        imagePermissionResultLauncher.launch(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    },
                    block = {
                        imagePermissionResultLauncher.launch(
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                    }
                )
            },
            onSettingsClick = { activity.openAppSettings() },
            onBack = { navController.pop() }
        )
    }
}

@Composable
private fun PermissionCard(
    onPermissionClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    OnboardingCard(
        body = stringResource(id = R.string.onb_permission),
        headline = {
            OnboardingCardHeadline(
                headline = stringResource(R.string.permission),
                onBack = onBack
            )
        },
        actionButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ExtractorTextButton(onClick = onSettingsClick, contentColor = Color.White) {
                    Text(text = stringResource(R.string.open_settings))
                }

                ExtractorActionButton(onClick = onPermissionClick) {
                    Text(text = stringResource(R.string.grant_permission))
                }
            }
        }
    )
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        PermissionCard(
            onPermissionClick = { /*TODO*/ },
            onSettingsClick = { /*TODO*/ },
            onBack = { /*TODO*/ })
    }
}