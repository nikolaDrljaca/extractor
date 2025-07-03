package com.drbrosdev.extractor.ui.components.shared

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
    bottomText: String? = null,
) {
    var shouldShowInfoText by rememberSaveable {
        mutableStateOf(bottomText != null)
    }

    LaunchedEffect(key1 = Unit) {
        if (bottomText == null) return@LaunchedEffect
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
                bottomText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun ExtractorHeader(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    headerText: String = stringResource(id = R.string.app_name),
    bottomText: String? = null,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Surface(
        modifier = Modifier
            .then(modifier),
        color = Color.Transparent,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = alignment
        ) {
            Text(
                text = headerText,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            bottomText?.let {
                Text(
                    text = it,
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
        Surface {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                ExtractorHeader()
            }
        }
    }
}
