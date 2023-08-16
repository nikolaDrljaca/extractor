package com.drbrosdev.extractor.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.findActivity
import com.drbrosdev.extractor.openAppSettings
import com.drbrosdev.extractor.ui.components.OnboardingCard
import com.drbrosdev.extractor.ui.components.OnboardingCardHeadline

class PermissionNode(
    buildContext: BuildContext,
    private val onBackPressed: () -> Unit
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        val imagePermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) finish()
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    viewModel.onPermissionResult(
//                        Manifest.permission.READ_MEDIA_IMAGES,
//                        isGranted = isGranted
//                    )
//                } else {
//                    viewModel.onPermissionResult(
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        isGranted = isGranted
//                    )
//                }
            }
        )
        val activity = LocalContext.current.findActivity()

        OnboardingCard(
            body = stringResource(id = R.string.lorem),
            headline = {
                OnboardingCardHeadline(
                    headline = "Permission",
                    onBack = {
                        onBackPressed()
                    }
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
                    OutlinedButton(onClick = {
                        activity.openAppSettings()
                    }) {
                        Text(text = "Open Settings")
                    }

                    Button(onClick = {
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