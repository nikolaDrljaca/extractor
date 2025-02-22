package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.Image
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
import androidx.compose.ui.tooling.preview.Preview
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.ExtractorDialog
import com.drbrosdev.extractor.ui.components.shared.InfoIconButton
import com.drbrosdev.extractor.ui.components.shared.OnboardingCard
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@Composable
fun StartWorkerCard(
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
                text = stringResource(id = R.string.onb_start),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    OnboardingCard(
        modifier = modifier,
        painter = {
            Image(
                painter = painterResource(id = R.drawable.ilu_success),
                contentDescription = stringResource(R.string.start_extracting),
            )
        },
        topBar = {
            InfoIconButton(onClick = { shouldShowInfoDialog = true })
        },
    ) {
        Text(text = stringResource(R.string.start_extracting), style = headlineStyle)
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            StartWorkerCard()
        }
    }
}