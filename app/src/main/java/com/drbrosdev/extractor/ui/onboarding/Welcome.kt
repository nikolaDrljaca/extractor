package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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


@Composable
fun WelcomeCard(
    modifier: Modifier = Modifier
) {
    val appName = stringResource(id = R.string.app_name)
    val painter = painterResource(id = R.drawable.ic_launcher)
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
                text = stringResource(R.string.onb_what_is, appName),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    OnboardingCard(
        modifier = modifier,
        painter = {
            Image(
                painter = painter,
                contentDescription = stringResource(R.string.welcome),
                modifier = Modifier.size(128.dp)
            )
        },
        topBar = {
            InfoIconButton(onClick = { shouldShowInfoDialog = true })
        },
    ) {
        Text(text = appName, style = headlineStyle)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.search_gallery),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            WelcomeCard()
        }
    }
}