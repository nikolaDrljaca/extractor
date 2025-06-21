package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.AppOnboardingButton
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.ui.theme.seedColor


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WelcomeCard(
    modifier: Modifier = Modifier,
    onGetStarted: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides Color.White
        ) {
            AppTopBar()
            Spacer(Modifier.height(1.dp))
            Column {
                Text(
                    text = "Find your memories!",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = stringResource(R.string.search_gallery),
                )
                Spacer(Modifier.height(36.dp))
                AppOnboardingButton(
                    onClick = onGetStarted,
                ) { size ->
                    Text(text = "Get Started")
                    Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Get started",
                        modifier = Modifier.size(ButtonDefaults.iconSizeFor(size))
                    )
                }
            }
        }
    }
}

@Composable
private fun AppTopBar(modifier: Modifier = Modifier) {
    val painter = painterResource(id = R.drawable.ic_launcher)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.welcome),
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = stringResource(R.string.app_name) + ".",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(
            color = seedColor
        ) {
            WelcomeCard(
                onGetStarted = {}
            )
        }
    }
}