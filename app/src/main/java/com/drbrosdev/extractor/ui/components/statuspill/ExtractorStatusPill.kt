package com.drbrosdev.extractor.ui.components.statuspill

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun ExtractorStatusPill(
    modifier: Modifier = Modifier,
    state: ExtractorStatusPillState
) {
    val contentColor by animateColorAsState(
        targetValue = when (state) {
            is ExtractorStatusPillState.Idle -> MaterialTheme.colorScheme.onSurface
            ExtractorStatusPillState.OutOfSync -> MaterialTheme.colorScheme.error
            is ExtractorStatusPillState.SyncInProgress -> MaterialTheme.colorScheme.tertiary
            is ExtractorStatusPillState.Disabled -> MaterialTheme.colorScheme.onSurface
        },
        label = ""
    )

    Surface(
        modifier = Modifier
            .then(modifier),
        contentColor = contentColor,
        color = Color.Transparent,
        shape = CircleShape,
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.labelSmall
        ) {
            when (state) {
                is ExtractorStatusPillState.Idle -> Text(
                    text = stringResource(
                        R.string.searches_left_count,
                        state.searchesLeft
                    )
                )

                ExtractorStatusPillState.OutOfSync -> Text(text = stringResource(R.string.data_out_of_sync))
                is ExtractorStatusPillState.SyncInProgress -> Text(
                    text = stringResource(
                        R.string.current_progress_percent,
                        state.progress
                    )
                )

                is ExtractorStatusPillState.Disabled -> Text(
                    text = stringResource(
                        R.string.indexed_images_count,
                        state.indexCount
                    )
                )
            }
        }
    }
}


@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ExtractorStatusPill(state = ExtractorStatusPillState.Idle(232))
                ExtractorStatusPill(state = ExtractorStatusPillState.OutOfSync)
                ExtractorStatusPill(state = ExtractorStatusPillState.SyncInProgress(90))
                ExtractorStatusPill(state = ExtractorStatusPillState.Disabled(522))
            }
        }
    }
}