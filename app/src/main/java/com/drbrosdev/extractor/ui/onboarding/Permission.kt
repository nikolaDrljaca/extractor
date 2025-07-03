package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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


@Composable
fun PermissionCard(
    modifier: Modifier = Modifier,
    onPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CompositionLocalProvider(
            LocalContentColor provides Color.White
        ) {
            Column {
                Surface(
                    color = Color.White,
                    contentColor = seedColor,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    shape = CircleShape,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_shield_toggle_24),
                        contentDescription = "",
                        modifier = Modifier.size(64.dp)
                            .padding(8.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.permission),
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.function_permission),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            AppOnboardingButton(
                onClick = onPermission
            ) {
                Text(
                    text = stringResource(R.string.grant_permission)
                )
            }
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = seedColor) {
            PermissionCard(
                onPermission = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}