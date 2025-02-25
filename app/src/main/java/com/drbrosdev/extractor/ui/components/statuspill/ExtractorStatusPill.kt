package com.drbrosdev.extractor.ui.components.statuspill

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

// Change to be outlined style
// could even change to a pill shaped status button micro feature
// show either leftover count / syncing / idle / outOfSync
// increase the size overall - padding around components

sealed interface ExtractorStatusPillState {
    // sync is idle -- show leftover search count
    data object Idle : ExtractorStatusPillState

    // extraction is running -- report progress
    data object SyncInProgress : ExtractorStatusPillState

    // auto sync is off -- report extraction out of sync and numbers
    data object OutOfSync : ExtractorStatusPillState
}

@Composable
fun ExtractorStatusPill(
    modifier: Modifier = Modifier,
    state: ExtractorStatusPillState = ExtractorStatusPillState.Idle
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            ExtractorStatusPillState.Idle -> MaterialTheme.colorScheme.surface
            ExtractorStatusPillState.OutOfSync -> MaterialTheme.colorScheme.error
            ExtractorStatusPillState.SyncInProgress -> MaterialTheme.colorScheme.tertiary
        },
        label = ""
    )
    val contentColor by animateColorAsState(
        targetValue = when (state) {
            ExtractorStatusPillState.Idle -> MaterialTheme.colorScheme.onSurface
            ExtractorStatusPillState.OutOfSync -> MaterialTheme.colorScheme.onErrorContainer
            ExtractorStatusPillState.SyncInProgress -> MaterialTheme.colorScheme.onTertiary
        },
        label = ""
    )

    Surface(
        modifier = Modifier
            .then(modifier),
        contentColor = backgroundColor,
        color = Color.Transparent,
        shape = CircleShape,
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.labelSmall
        ) {
            AnimatedContent(targetState = state) {
                when (it) {
                    ExtractorStatusPillState.Idle -> Text(text = "Searches left: 1000")
                    ExtractorStatusPillState.OutOfSync -> Text(text = "Data out of sync.")
                    ExtractorStatusPillState.SyncInProgress -> Text(text = "Current progress: 323 / 1234")
                }
            }
        }
    }
}

@Composable
private fun StatusText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}


@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ExtractorStatusPill(state = ExtractorStatusPillState.Idle)
                ExtractorStatusPill(state = ExtractorStatusPillState.OutOfSync)
                ExtractorStatusPill(state = ExtractorStatusPillState.SyncInProgress)
            }
        }
    }
}