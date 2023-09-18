package com.drbrosdev.extractor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.ExtractorLeaderButtonState.IDLE
import com.drbrosdev.extractor.ui.components.ExtractorLeaderButtonState.WORKING
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import kotlinx.coroutines.delay

enum class ExtractorLeaderButtonState {
    IDLE,
    WORKING
}

@Composable
fun ExtractorLeaderButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonState: ExtractorLeaderButtonState,
    percentageDone: Int? = null
) {
    val transition = updateTransition(
        targetState = buttonState,
        label = "Button animation"
    )

    val backgroundColor by transition.animateColor(label = "") {
        when (it) {
            IDLE -> MaterialTheme.colorScheme.surfaceVariant
            WORKING -> MaterialTheme.colorScheme.primary
        }
    }

    val textColor by transition.animateColor(label = "") {
        when (it) {
            IDLE -> MaterialTheme.colorScheme.onBackground
            WORKING -> Color.White
        }
    }

    val bottomText = when (buttonState) {
        IDLE -> "Tap for more."
        WORKING -> "$percentageDone% done."
    }

    val header = when (buttonState) {
        IDLE -> stringResource(id = R.string.app_name)
        WORKING -> "Working..."
    }

    var shouldShowInfoText by rememberSaveable {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = buttonState) {
        delay(3500L)
        shouldShowInfoText = when (buttonState) {
            IDLE -> false
            WORKING -> true
        }
    }

    Surface(
        modifier = Modifier
            .then(modifier),
        color = backgroundColor,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(14.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .width(180.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Start
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_android_24),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp),
                tint = textColor
            )

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = header,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = textColor
                    )
                )

                AnimatedVisibility(
                    visible = shouldShowInfoText,
                ) {
                    Text(
                        text = bottomText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = textColor
                        )
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun ButtonsPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            ExtractorLeaderButton(
                onClick = {},
                buttonState = IDLE
            )

            ExtractorLeaderButton(
                onClick = {},
                buttonState = WORKING
            )
        }
    }
}
