package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.ExtractorDialog
import com.drbrosdev.extractor.ui.components.shared.InfoIconButton
import com.drbrosdev.extractor.ui.components.shared.OnboardingCard
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

/*
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
 */

@Composable
fun PermissionCard(
    modifier: Modifier = Modifier,
) {
    val headlineStyle = MaterialTheme.typography.headlineMedium
    var shouldShowInfoDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (shouldShowInfoDialog) {
        ExtractorDialog(
            onAction = {
                shouldShowInfoDialog = !shouldShowInfoDialog
            },
        ) {
            Text(
                text = stringResource(id = R.string.onb_permission),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    OnboardingCard(
        modifier = modifier,
        painter = {
            Image(
                painter = painterResource(id = R.drawable.ilu_permissions),
                contentDescription = "",
            )
        },
        topBar = {
            InfoIconButton(onClick = { shouldShowInfoDialog = true })
        },
    ) {
        Text(text = stringResource(id = R.string.permission), style = headlineStyle)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "In order to function properly, we need permission to view your images.",
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
    /*
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
     */
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PermissionCard()
        }
    }
}