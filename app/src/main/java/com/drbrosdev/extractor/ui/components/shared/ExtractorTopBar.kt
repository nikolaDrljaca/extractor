package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

enum class ExtractorTopBarState {
    NORMAL,
    ELEVATED
}

@Composable
fun ExtractorTopBar(
    modifier: Modifier = Modifier,
    state: ExtractorTopBarState = ExtractorTopBarState.NORMAL,
    leadingSlot: (@Composable RowScope.() -> Unit)? = null,
    trailingSlot: (@Composable RowScope.() -> Unit)? = null,
    centerSlot: (@Composable RowScope.() -> Unit)? = null,
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val transition = updateTransition(targetState = state, label = "")
    val elevation by transition.animateDp(label = "") {
        when (it) {
            ExtractorTopBarState.NORMAL -> 0.dp
            ExtractorTopBarState.ELEVATED -> 4.dp
        }
    }

    val spacerHeight by transition.animateDp(label = "") {
        when (it) {
            ExtractorTopBarState.NORMAL -> 12.dp
            ExtractorTopBarState.ELEVATED -> 6.dp
        }
    }

    val cornerShape by transition.animateDp(label = "") {
        when (it) {
            ExtractorTopBarState.NORMAL -> 0.dp
            ExtractorTopBarState.ELEVATED -> 14.dp
        }
    }

    Surface(
        modifier = Modifier
            .then(modifier),
        tonalElevation = elevation,
        shape = RoundedCornerShape(bottomEnd = cornerShape, bottomStart = cornerShape),
        color = MaterialTheme.colorScheme.background //TODO Maybe keep default
    ) {
        Column(
            modifier = Modifier
                .padding(
                    PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = spacerHeight + topPadding,
                        bottom = 4.dp
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(modifier),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) { leadingSlot?.invoke(this) }
                Row { centerSlot?.invoke(this) }
                Row {
                    when {
                        trailingSlot != null -> trailingSlot()
                        else -> Spacer(modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column {
            ExtractorTopBar {
                Column {
                    Text(text = "Header")
                    Text(text = "small text", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExtractorTopBar(
                state = ExtractorTopBarState.ELEVATED
            ) {
                Text(text = "Header")
            }
        }
    }
}