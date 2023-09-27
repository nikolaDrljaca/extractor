package com.drbrosdev.extractor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import kotlinx.coroutines.delay


@Composable
fun ExtractorHeader(
    modifier: Modifier = Modifier,
    headerText: String = stringResource(id = R.string.app_name),
    bottomText: String = "Tap left for more.",
) {
    var shouldShowInfoText by rememberSaveable {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = Unit) {
        delay(3500L)
        shouldShowInfoText = false
    }

    Surface(
        modifier = Modifier
            .then(modifier),
        color = Color.Transparent,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = headerText,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            AnimatedVisibility(
                visible = shouldShowInfoText,
            ) {
                Text(
                    text = bottomText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}


@Preview
@Composable
private fun ButtonsPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            ExtractorHeader()
        }
    }
}
